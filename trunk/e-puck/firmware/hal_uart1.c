#include <libpic30.h>

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
 * _U1RXInterrupt | hal_int_setPriority | hal_int_enable | hal_int_disable | hal_uart1_forceTxMove
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
 * _U1TXInterrupt | hal_int_setPriority | hal_int_enable | hal_int_disable | hal_uart1_forceRxMove
 */
void _U1RXInterrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__UART1_RECEIVER);

	// There might have been a race condition between getch or read & this interrupt -> ensure that there is data available 
	while( U1STAbits.URXDA && !ringbuf_isFull( &hal_uart1_podRxBuffer)) {
		ringbuf_push( &hal_uart1_podRxBuffer, U1RXREG);
	}
}


/*!
 * \brief
 * Enables the primary UART.
 *
 * \param _blWithTransmitter
 * Specifies whether or not to enable the transmitter sub-module.
 * 
 * Once enabled, the U1TX and U1RX pins are configured as an output and an input respectively,
 * overriding the TRIS and LATCH register bit settings for the corresponding I/O port pins.
 * 
 * The transmission interrupt is set to trigger on an empty hardware buffer.
 * The reception interrupt is set to trigger on a full hardware buffer.
 * The associated interrupt priorities are set to #HAL_INT_PRIORITY__6.
 *
 * The ring buffers must be initialized before with #ringbuf_init().
 * Their context can be retrieved with #hal_uart1_getTxRingBuffer() and #hal_uart1_getRxRingBuffer()
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The UART should be configured before enabling it.
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
	IN const bool _blWithTransmitter
	) {

	U1MODEbits.UARTEN = false;

	// Trigger INT on empty Tx buffer
	U1STAbits.UTXISEL = 1;

	// Trigger INT on full Rx buffer
	U1STAbits.URXISEL = 3;

	// Enable UART module before transmitter (see spec.)
	U1MODEbits.UARTEN = true;
	if( _blWithTransmitter) {
		U1STAbits.UTXEN = true;
	}

	hal_int_setPriority( HAL_INT_SOURCE__UART1_RECEIVER, HAL_INT_PRIORITY__6);
	hal_int_setPriority( HAL_INT_SOURCE__UART1_TRANSMITTER, HAL_INT_PRIORITY__6);

	hal_int_clearFlag( HAL_INT_SOURCE__UART1_RECEIVER);
	hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
	if( _blWithTransmitter) {
		hal_int_clearFlag( HAL_INT_SOURCE__UART1_TRANSMITTER);
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART transmitter must be enabled and the priority of its associated interrupt must be above the CPU priority level.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_puts | hal_uart1_write | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_forceTxMove
 */
void hal_uart1_putch(
	IN const char _cValue
	) {

	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// Push directly if space available
	if( !U1STAbits.UTXBF) {
		U1TXREG = _cValue;

	// Use ring buffer
	} else {

		// Block until space available
		while( ringbuf_isFull( &hal_uart1_podTxBuffer)) {

			// Allow Tx interrupt once
			hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
			hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);
		}

		ringbuf_push( &hal_uart1_podTxBuffer, _cValue);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART transmitter must be enabled and the priority of its associated interrupt must be above the CPU priority level.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_putch | hal_uart1_puts | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_forceTxMove
 */
void hal_uart1_write(
	IN const void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	const char* lpc = (const char*)_lpvData;
	const char* const lpcEnd = (const char*)_lpvData + _ui16Length;

	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// Fill the hardware buffer first if the ring buffer is empty
	if( ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
		while( !U1STAbits.UTXBF && lpc < lpcEnd) {
			U1TXREG = *lpc++;
		}
	}

	while( lpc < lpcEnd) {

		// Block until space available
		while( ringbuf_isFull( &hal_uart1_podTxBuffer)) {

			// Allow Tx interrupt once
			hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
			hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);
		}

		ringbuf_push( &hal_uart1_podTxBuffer, *lpc++);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART transmitter must be enabled and the priority of its associated interrupt must be above the CPU priority level.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_putch | hal_uart1_write | hal_int_setPriority | hal_uart1_generateBreak | hal_uart1_forceTxMove
 */
void hal_uart1_puts(
	IN const char* const _cstrText
	) {

	const char* lpc = _cstrText;

	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// Fill the hardware buffer first if the ring buffer is empty
	if( ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
		while( !U1STAbits.UTXBF && *lpc) {
			U1TXREG = *lpc++;
		}
	}

	while( *lpc) {

		// Block until space available
		while( ringbuf_isFull( &hal_uart1_podTxBuffer)) {

			// Allow Tx interrupt once
			hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
			hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);
		}

		ringbuf_push( &hal_uart1_podTxBuffer, *lpc++);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART receiver must be enabled and the priority of its associated interrupt must be above the CPU priority level.
 *
 * \warning
 * - This function might block forever if the receiver interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the reception part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_read | hal_uart1_hasRxData | hal_int_setPriority | hal_uart1_forceRxMove
 */
char hal_uart1_getch( void) {

	char cReturn;

	hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);

	if( ringbuf_isEmpty( &hal_uart1_podRxBuffer)) {

		// Wait for Rx data
		while( !U1STAbits.URXDA) {
			hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
			hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);
		}

		cReturn = U1RXREG;
	} else {
		cReturn = ringbuf_pop( &hal_uart1_podRxBuffer);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);

	return cReturn;
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART receiver must be enabled and the priority of its associated interrupt must be above the CPU priority level.
 *
 * \warning
 * - This function might block forever if the receiver interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the reception part of this module.
 * 
 * \see
 * hal_uart1_enable | hal_uart1_getch | hal_uart1_hasRxData | hal_int_setPriority | hal_uart1_forceRxMove
 */
void hal_uart1_read(
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	char* lpc = (char*)_lpvData;
	const char* const lpcEnd = (char*)_lpvData + _ui16Length;

	hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);

	lpc += ringbuf_popRange( &hal_uart1_podRxBuffer, lpc, _ui16Length);

	// Pop directly from the hardware buffer if there was not enough data
	while( lpc < lpcEnd) {

		// Wait for Rx data
		while( !U1STAbits.URXDA) {
			hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
			hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);
		}

		*lpc++ = U1RXREG;
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
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
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART transmitter must be enabled.
 * - The amount of delay cycles is generated based on the baud rate divisor.
 *
 * \warning
 * - This function might block forever if the transmitter interrupt is not enabled.
 * - This function may not be preempted by any function which accesses the transmission part of this module.
 * 
 * \see
 * hal_uart1_enable
 */
void hal_uart1_generateBreak( void) {

	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);

	// Wait for pending activity to finish
	while( !hal_uart1_isTxIdle())
		;

	U1STAbits.UTXBRK = true;

	// Calculate ticks for one symbol; Inverted baud rate divisor formula
	uint32_t ui32DelayTicks = ( (uint32_t)U1BRG + 1) * 16;

	// 16 Symbols delay
	__delay32( ui32DelayTicks * 16);

	U1STAbits.UTXBRK = false;

	// 4 Symbols delay
	__delay32( ui32DelayTicks * 4);

	hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
}


/*!
 * \brief
 * Forces the primary UART transmitter hardware buffer content to be moved to the associated ring buffer.
 * 
 * This function acts like the transmitter interrupt. It is especially useful if one wants to operate on the transmitter
 * ring buffer directly without relying on #hal_uart1_putch(), #hal_uart1_puts() or #hal_uart1_write().
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART transmitter must be enabled.
 * 
 * \warning
 * This function may not be preempted by any function which accesses the transmission part of this module.
 *
 * \see
 * hal_uart1_enable | hal_uart1_getTxRingBuffer | hal_uart1_forceRxMove
 */
void hal_uart1_forceTxMove( void) {

	hal_int_disable( HAL_INT_SOURCE__UART1_TRANSMITTER);
	hal_int_clearFlag( HAL_INT_SOURCE__UART1_TRANSMITTER);

	while( !U1STAbits.UTXBF && !ringbuf_isEmpty( &hal_uart1_podTxBuffer)) {
		U1TXREG = ringbuf_pop( &hal_uart1_podTxBuffer);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_TRANSMITTER);
}


/*!
 * \brief
 * Forces the primary UART receiver hardware buffer content to be moved to the associated ring buffer.
 * 
 * This function acts like the receiver interrupt. It is especially useful if one wants to operate on the receiver
 * ring buffer directly without relying on #hal_uart1_getch() or #hal_uart1_read().
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The primary UART receiver must be enabled.
 *
 * \warning
 * This function may not be preempted by any function which accesses the reception part of this module.
 *
 * \see
 * hal_uart1_enable | hal_uart1_getRxRingBuffer | hal_uart1_forceTxMove
 */
void hal_uart1_forceRxMove( void) {

	hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);
	hal_int_clearFlag( HAL_INT_SOURCE__UART1_RECEIVER);

	while( U1STAbits.URXDA && !ringbuf_isFull( &hal_uart1_podRxBuffer)) {
		ringbuf_push( &hal_uart1_podRxBuffer, U1RXREG);
	}

	hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
}
