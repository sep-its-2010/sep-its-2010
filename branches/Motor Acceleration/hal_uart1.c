#include <libpic30.h>
#include <string.h>

#include "hal_uart1.h"


/*!
 * \brief
 * Transmitter ring buffer context.
 */
ringbuf_SContext_t hal_uart1_podTxBuffer = { NULL, 0, 0, 0 };


/*!
 * \brief
 * Receiver ring buffer context.
 */
ringbuf_SContext_t hal_uart1_podRxBuffer = { NULL, 0, 0, 0 };


/*!
 * \brief
 * Holds the optional framing error callback.
 * 
 * \see
 * hal_uart1_enable
 */
static hal_uart_fnErrorHandler_t s_fnErrorHandler = NULL;


void _ISR NO_AUTO_PSV _U1TXInterrupt( void);

void _ISR NO_AUTO_PSV _U1RXInterrupt( void);


/*!
 * \internal
 *
 * \brief
 * Primary UART transmitter interrupt service routine.
 *
 * This interrupt triggers when the hardware buffer of the transmitter is empty due to shifting its last byte.
 * 
 * \remarks
 * The interrupt does not use shadow register.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * _U1RXInterrupt | hal_int_setPriority | hal_int_enable | hal_int_disable | hal_uart1_flushTx
 */
void _U1TXInterrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// There might have been a race condition between putch, puts or write & this interrupt
	// -> ensure that the hardware buffer is not full
	while( !U1STAbits.UTXBF && !ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
		U1TXREG = ringbuf_pop( &hal_uart1_podTxBuffer);
	}
}


/*!
 * \internal
 *
 * \brief
 * Primary UART receiver interrupt service routine.
 *
 * This interrupt triggers when the hardware buffer of the receiver is full due to shifting a byte.
 * 
 * \remarks
 * The interrupt does not use shadow register.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * _U1TXInterrupt | hal_int_setPriority | hal_int_enable | hal_int_disable | hal_uart1_flushRx
 */
void _U1RXInterrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__UART1_RECEIVER);

	// There might have been a race condition between getch or read & this interrupt
	// -> ensure that there is data available
	uint8_t aui8Data[5];
	uint16_t ui16Read = 0;
	const uint16_t ui16Free = min( sizeof( aui8Data), ringbuf_getFree( &hal_uart1_podRxBuffer));
	while( U1STAbits.URXDA && ui16Read < ui16Free) {

		// The frame error can also be a break; the error bit must be read before the data byte
		const bool blFramingError = U1STAbits.FERR;
		const uint8_t ui8Byte = U1RXREG;
		if( !blFramingError || ( s_fnErrorHandler && s_fnErrorHandler( ui8Byte))) {
			aui8Data[ui16Read++] = ui8Byte;
		}
	}
	ringbuf_pushRange( &hal_uart1_podRxBuffer, aui8Data, ui16Read);
}


/*!
 * \brief
 * Enables the primary UART.
 *
 * \param _blWithTransmitter
 * Specifies whether or not to enable the transmitter sub-module.
 *
 * \param _fnOnError
 * Specifies an optional handler for framing error. If the handler returns \c true the byte is enqueued and dismissed otherwise.
 * 
 * Once enabled, the U1TX and U1RX pins are configured as an output and an input respectively,
 * overriding the TRIS and LATCH register bit settings for the corresponding I/O port pins.
 * 
 * The transmission interrupt is set to trigger on an empty hardware buffer with initial priority #HAL_INT_PRIORITY__4.
 * The reception interrupt is set to trigger on a full hardware buffer with initial priority #HAL_INT_PRIORITY__5.
 *
 * The ring buffers must be initialized before with #ringbuf_init().
 * Their context can be retrieved with #hal_uart1_getTxRingBuffer() and #hal_uart1_getRxRingBuffer()
 * 
 * \remarks
 * - The UART should be configured before enabling it.
 * - This function is interrupt safe concerning interrupts from this module.
 * - The priority of the receiver interrupt should he higher or equal to the transmitter interrupt to avoid overflows.
 *
 * \warning
 * - Using uninitialized ring buffers can cause unpredictable results.
 * - Always call #hal_int_enable() after #hal_int_disable() when the UART interrupts need to be disabled temporarily.
 * - Interrupts are crucial for correct transmissions and especially receptions. Do not block them for too long.
 * - This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_uart1_close | hal_uart1_configure
 */
void hal_uart1_enable(
	IN const bool _blWithTransmitter,
	IN OPT const hal_uart_fnErrorHandler_t _fnOnError
	) {

	U1MODEbits.UARTEN = false;
	hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);
	hal_int_clearFlag( HAL_INT_SOURCE__UART1_RECEIVER);
	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);
	hal_int_clearFlag( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// Trigger INT on empty Tx buffer
	U1STAbits.UTXISEL = 1;

	// Trigger INT on full Rx buffer
	U1STAbits.URXISEL = 3;

	// Enable UART module before transmitter (see spec.)
	U1MODEbits.UARTEN = true;
	if( _blWithTransmitter) {
		U1STAbits.UTXEN = true;
	}

	s_fnErrorHandler = _fnOnError;

	hal_int_setPriority( HAL_INT_SOURCE__UART1_TRANSMITTER, HAL_INT_PRIORITY__4);
	hal_int_setPriority( HAL_INT_SOURCE__UART1_RECEIVER, HAL_INT_PRIORITY__7);

	hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
	if( _blWithTransmitter) {
		hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
	}
}


/*!
 * \brief
 * Writes a single byte to the primary UART buffer.
 * 
 * \param _cValue
 * Specifies the byte to be written.
 * 
 * Blocks until the primary UART Tx buffer is not full any more, then pushes the specified byte onto the buffer.
 * Transmission starts immediately after pushing the byte.
 * 
 * \remarks
 * - The primary UART transmitter must be enabled.
 * - The priority will escalate to the transmitter interrupt priority only by need.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_puts | hal_uart1_write | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_flushTx
 */
void hal_uart1_putch(
	IN const char _cValue
	) {

	bool blDone = false;
	while( !blDone) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {

			// Fill the hardware buffer if the ring buffer is empty and the hardware buffer is not full
			if( ringbuf_isEmpty( &hal_uart1_podTxBuffer) && !U1STAbits.UTXBF) {
				U1TXREG = _cValue;
				blDone = true;

			// Use ring buffer to enqueue the byte otherwise if possible
			} else if( !ringbuf_isFull( &hal_uart1_podTxBuffer)) {
				ringbuf_push( &hal_uart1_podTxBuffer, _cValue);
				blDone = true;
			}
		}
	}
}


/*!
 * \brief
 * Writes a specified amount of data to the primary UART buffer.
 * 
 * \param _lpvData
 * Specifies the data to be written.
 *
 * \param _ui16Length
 * Specifies the amount of data to be written.
 * 
 * Pushes the data bytes onto the primary UART Tx buffer as long as the buffer is not full; blocks otherwise.
 * Transmission starts immediately after pushing the first byte.
 * 
 * \remarks
 * - The primary UART transmitter must be enabled.
 * - The priority will escalate to the transmitter interrupt priority only by need.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_putch | hal_uart1_puts | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_flushTx
 */
void hal_uart1_write(
	IN const void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	const char* lpc = (const char*)_lpvData;
	const char* const lpcEnd = (const char*)_lpvData + _ui16Length;

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {

		// Fill the hardware buffer first if the ring buffer is empty
		if( ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
			while( !U1STAbits.UTXBF && lpc < lpcEnd) {
				U1TXREG = *lpc++;
			}
		}

		// Fill ring buffer next
		lpc += ringbuf_pushRange( &hal_uart1_podTxBuffer, lpc, (uint16_t)( lpcEnd - lpc));
	}

	// Fill remainder
	while( lpc < lpcEnd) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {
			lpc += ringbuf_pushRange( &hal_uart1_podTxBuffer, lpc, (uint16_t)( lpcEnd - lpc));
		}
	}
}


/*!
 * \brief
 * Writes a c-string to the primary UART.
 * 
 * \param _cstrText
 * Specifies the zero-terminated string to be written.
 * 
 * Pushes the string bytes onto the primary UART Tx buffer as long as the buffer is not full; blocks otherwise.
 * Transmission starts immediately after pushing the first byte.
 * 
 * \remarks
 * - The primary UART transmitter must be enabled.
 * - The priority will escalate to the transmitter interrupt priority only by need.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_putch | hal_uart1_write | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_flushTx
 */
void hal_uart1_puts(
	IN const char* const _cstrText
	) {

	const char* lpc = _cstrText;
	const char* const lpcEnd = _cstrText + strlen( lpc);

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {

		// Fill the hardware buffer first if the ring buffer is empty
		if( ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
			while( !U1STAbits.UTXBF && lpc < lpcEnd) {
				U1TXREG = *lpc++;
			}
		}

		// Fill ring buffer next
		lpc += ringbuf_pushRange( &hal_uart1_podTxBuffer, lpc, (uint16_t)( lpcEnd - lpc));
	}

	// Fill remainder
	while( lpc < lpcEnd) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {
			lpc += ringbuf_pushRange( &hal_uart1_podTxBuffer, lpc, (uint16_t)( lpcEnd - lpc));
		}
	}
}


/*!
 * \brief
 * Reads a single byte from the primary UART.
 * 
 * Blocks until the primary UART Rx buffer is not empty, then pops the oldest byte from the buffer.
 *
 * \returns
 * The oldest byte in the buffer.
 * 
 * \remarks
 * - The primary UART receiver must be enabled.
 * - The priority will escalate to the receiver interrupt priority only by need.
 *
 * \warning
 * - This function might block forever if the receiver interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the reception part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_read | hal_uart1_hasRxData | hal_int_setPriority | hal_uart1_flushRx
 */
char hal_uart1_getch( void) {

	for( ;;) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_RECEIVER)) {
			if( !ringbuf_isEmpty( &hal_uart1_podRxBuffer)) {
				return ringbuf_pop( &hal_uart1_podRxBuffer);
			}
		}
	}
}


/*!
 * \brief
 * Reads a specified amount of data from the primary UART.
 * 
 * \param _lpvData
 * Specifies the data buffer where incoming data should be read to.
 *
 * \param _ui16Length
 * Specifies the amount of data to be read.
 * 
 * Pops the specified amount of data from the UART Rx buffer as long as it is not empty; blocks otherwise. 
 * 
 * \remarks
 * - The primary UART receiver must be enabled.
 * - The priority will escalate to the receiver interrupt priority only by need.
 *
 * \warning
 * - This function might block forever if the receiver interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the reception part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_getch | hal_uart1_hasRxData | hal_int_setPriority | hal_uart1_flushRx
 */
void hal_uart1_read(
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	char* lpc = (char*)_lpvData;
	const char* const lpcEnd = (char*)_lpvData + _ui16Length;

	while( lpc < lpcEnd) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_RECEIVER)) {
			lpc += ringbuf_popRange( &hal_uart1_podRxBuffer, lpc, (uint16_t)( lpcEnd - lpc));
		}
	}
}


/*!
 * \brief
 * Generates a break condition on the primary UART transmitter.
 * 
 * The transmitter interrupt is temporarily disabled.
 * As soon as the transmitter is idle a break condition is generated which lasts at least for 16 UART symbols.
 * Afterwards the break is released and the normal operation continues.
 *
 * A break is defined as an UART frame with the stop bit being 'low'. This actually generates a frame error.
 * 
 * \remarks
 * - The primary UART transmitter must be enabled.
 * - The amount of delay cycles is generated based on the baud rate divisor.
 * - The priority will escalate to the transmitter interrupt priority during operation.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable
 */
void hal_uart1_generateBreak( void) {

	// Wait for pending activity to finish
	while( !hal_uart1_isTxIdle())
		;

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {
		U1STAbits.UTXBRK = true;

		// Calculate ticks for one symbol; Inverted baud rate divisor formula
		uint32_t ui32DelayTicks = ( (uint32_t)U1BRG + 1) * 16;

		// 16 Symbols delay
		__delay32( ui32DelayTicks * 16);

		U1STAbits.UTXBRK = false;

		// 4 Symbols delay
		__delay32( ui32DelayTicks * 4);
	}
}


/*!
 * \brief
 * Forces the primary UART transmitter ring buffer content to be sent completely.
 * 
 * When this function returns the ring buffer is guaranteed to be empty.
 * 
 * \remarks
 * - The primary UART transmitter must be enabled.
 * - The priority will escalate to the transmitter interrupt priority during operation.
 * 
 * \warning
 * This function may not be preempted by any function which accesses the transmission part of this module.
 *
 * \see
 * hal_uart1_enable | hal_uart1_getTxRingBuffer | hal_uart1_flushRx
 */
void hal_uart1_flushTx( void) {

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_TRANSMITTER)) {
		while( !ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
			while( !U1STAbits.UTXBF) {
				U1TXREG = ringbuf_pop( &hal_uart1_podTxBuffer);
			}
		}
	}
}


/*!
 * \brief
 * Forces the primary UART receiver hardware buffer content to be moved to the associated ring buffer.
 * 
 * This function acts like the receiver interrupt. It is especially useful if one wants to operate on the receiver
 * ring buffer directly without relying on #hal_uart1_getch() or #hal_uart1_read().
 *
 * The incoming data is moved from the hardware buffer to the ring buffer until the hardware buffer is empty or the ring buffer
 * is full.
 * 
 * \remarks
 * - The primary UART receiver must be enabled.
 * - The priority will escalate to the receiver interrupt priority during operation.
 *
 * \warning
 * This function may not be preempted by any function which accesses the reception part of this module.
 *
 * \see
 * hal_uart1_enable | hal_uart1_getRxRingBuffer | hal_uart1_flushTx
 */
void hal_uart1_flushRx( void) {

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_RECEIVER)) {
		uint8_t aui8Data[5];
		uint16_t ui16Read = 0;
		const uint16_t ui16Free = min( sizeof( aui8Data), ringbuf_getFree( &hal_uart1_podRxBuffer));
		while( U1STAbits.URXDA && ui16Read < ui16Free) {

			// The frame error can also be a break; the error bit must be read before the data byte
			const bool blFramingError = U1STAbits.FERR;
			const uint8_t ui8Byte = U1RXREG;
			if( !blFramingError || ( s_fnErrorHandler && s_fnErrorHandler( ui8Byte))) {
				aui8Data[ui16Read++] = ui8Byte;
			}
		}
		ringbuf_pushRange( &hal_uart1_podRxBuffer, aui8Data, ui16Read);
	}
}
