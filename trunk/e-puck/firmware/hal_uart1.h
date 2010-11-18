#ifndef hal_uart1_h__
#define hal_uart1_h__

#include "common.h"

#include <p30f6014A.h>

#include "ringbuf.h"
#include "hal_int.h"
#include "hal_uart_types.h"


void hal_uart1_open(
	IN const bool _blWithTransmitter
	);
static inline void hal_uart1_close( void);
static inline void hal_uart1_configure(
	IN const hal_uart_EConfig_t _eConfig,
	IN const uint16_t _ui16BaudRateDiv
	);

void hal_uart1_putch(
	IN const char _cValue
	);
void hal_uart1_write(
	IN const void* const _lpvData,
	IN const uint16_t _ui16Length
	);
void hal_uart1_puts(
	IN const char* const _cstrText
	);

char hal_uart1_getch( void);
void hal_uart1_read(
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	);

static inline bool hal_uart1_hasRxData( void);
static inline bool hal_uart1_hasRxOverflow( void);
static inline void hal_uart1_clearRxOverflow( void);

static inline bool hal_uart1_isTxIdle( void);
static inline bool hal_uart1_isRxIdle( void);

void hal_uart1_generateBreak( void);

static inline ringbuf_SContext_t* hal_uart1_getTxRingBuffer( void);
static inline ringbuf_SContext_t* hal_uart1_getRxRingBuffer( void);

void hal_uart1_forceTxMove( void);
void hal_uart1_forceRxMove( void);


/*!
 * \brief
 * Disables the primary UART.
 * 
 * If the primary UART is disabled, all I/O pins operate as port pins under the control of the latch and
 * TRIS bits of the corresponding port pins.
 *
 * Steps to perform a clean shutdown:
 * -# Poll #hal_uart1_isRxIdle() and #hal_uart1_isTxIdle() until both are true.
 * -# Disable UART interrupts with #hal_int_setPriority(). (*)
 * -# Transfer any remaining bytes to the ring buffers with #hal_uart1_forceTxMove() and #hal_uart1_forceRxMove(). (*)
 * -# Call #hal_uart1_close().
 * -# Use #hal_uart1_getTxRingBuffer() and #hal_uart1_getRxRingBuffer() to operate on the ring buffers. (*)
 *
 * (*) Only perform these steps when the ring buffer content is still relevant.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The hardware buffers, the baud rate counter, error and status flags will be reset.
 * - The configuration will not be reset.
 *
 * \warning
 * Disabling the UART during operation will abort all pending transmissions and receptions.
 * 
 * \see
 * hal_uart1_open | hal_uart1_isTxIdle | hal_uart1_isRxIdle
 */
void hal_uart1_close( void) {

	U1MODEbits.UARTEN = false;
}


/*!
 * \brief
 * Configures the data format of the primary UART.
 * 
 * \param _eConfig
 * Specifies the data representation.
 * 
 * \param _ui16BaudRateDiv
 * Specifies the baud rate divisor.
 * 
 * The data representation may be 8 bits with even, odd or no parity.
 *
 * \code
 * baudrate = freq / ( 16 * ( _ui16BaudRateDiv + 1))
 * \endcode
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The UART should be configured before enabling it.
 * - The 9 bit mode is not supported.
 * 
 * \see
 * hal_uart1_open
 */
void hal_uart1_configure(
	IN const hal_uart_EConfig_t _eConfig,
	IN const uint16_t _ui16BaudRateDiv
	) {

	switch( _eConfig) {
		case HAL_UART_CONFIG__8N1: {
			U1MODEbits.PDSEL = 0;
			U1MODEbits.STSEL = false;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		case HAL_UART_CONFIG__8N2: {
			U1MODEbits.PDSEL = 0;
			U1MODEbits.STSEL = true;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		case HAL_UART_CONFIG__8E1: {
			U1MODEbits.PDSEL = 1;
			U1MODEbits.STSEL = false;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		case HAL_UART_CONFIG__8E2: {
			U1MODEbits.PDSEL = 1;
			U1MODEbits.STSEL = true;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		case HAL_UART_CONFIG__8O1: {
			U1MODEbits.PDSEL = 2;
			U1MODEbits.STSEL = false;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		case HAL_UART_CONFIG__8O2: {
			U1MODEbits.PDSEL = 2;
			U1MODEbits.STSEL = true;
			U1BRG = _ui16BaudRateDiv;
			break;
		}
		default: {
	
		}
	}
}


/*!
 * \brief
 * Checks whether there is data to be read from the primary UART receiver.
 *
 * Checks the hardware buffer and the ring buffer of the primary UART.
 * 
 * \returns
 * - true: There is at least one byte in the buffers.
 * - false: Both buffers are empty.
 * 
 * This function can be used to build a non-blocking poll & get algorithm.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The primary UART receiver must be enabled.
 * 
 * \see
 * hal_uart1_open | hal_uart1_hasRxOverflow | hal_uart1_read
 */
bool hal_uart1_hasRxData( void) {

	extern ringbuf_SContext_t hal_uart1_podTxBuffer;

	return U1STAbits.URXDA || !ringbuf_isEmpty( &hal_uart1_podTxBuffer);
}


/*!
 * \brief
 * Checks whether there was a hardware buffer overflow in the primary UART receiver.
 * 
 * \returns
 * - true: At least one byte was dropped.
 * - false: No overflow occurred.
 * 
 * A buffer overflow will happen whenever the hardware and ring buffer are full and a byte is moved in by the shift register.
 * The overflow flag remains set until #hal_uart1_clearRxOverflow() is called.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The primary UART receiver must be enabled.
 * 
 * \see
 * hal_uart1_open | hal_uart1_clearRxOverflow
 */
bool hal_uart1_hasRxOverflow( void) {

	return U1STAbits.OERR;
}


/*!
 * \brief
 * Clears the hardware buffer overflow flags of the primary UART receiver.
 * 
 * A buffer overflow will happen whenever the hardware and ring buffers are full and a byte is moved in by the shift register.
 * The overflow flag can be checked with #hal_uart1_hasRxOverflow().
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The primary UART receiver must be enabled.
 * 
 * \see
 * hal_uart1_open | hal_uart1_hasRxOverflow
 */
void hal_uart1_clearRxOverflow( void) {

	U1STAbits.OERR = false;
}


/*!
 * \brief
 * Checks whether the primary UART transmitter is idle.
 * 
 * \returns
 * - true: The transmitter is inactive.
 * - false: There is at least one byte  being shifted or in the hardware buffer.
 * 
 * This function is especially useful to allow a clean shutdown of the UART.
 * Shutting down an active transmitter would terminate the operation.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The primary UART transmitter must be enabled.
 * 
 * \see
 * hal_uart1_open | hal_uart1_close
 */
bool hal_uart1_isTxIdle( void) {

	return U1STAbits.TRMT;
}


/*!
 * \brief
 * Checks whether the primary UART receiver is idle.
 * 
 * \returns
 * - true: The receiver is inactive.
 * - false: The receiver is shifting a byte.
 * 
 * This function is especially useful to allow a clean shutdown of the UART.
 * Shutting down an active receiver would terminate the operation.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The primary UART receiver must be enabled.
 * 
 * \see
 * hal_uart1_open | hal_uart1_close
 */
bool hal_uart1_isRxIdle( void) {

	return U1STAbits.RIDLE;
}


/*!
 * \brief
 * Gets the ring buffer context of the primary UART transmitter.
 * 
 * \returns
 * The primary UART transmitter buffer context.
 * 
 * The primary UART transmitter is interrupt based. Thus, it requires an initialized buffer.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The context must be initialized with #ringbuf_init() before the buffer can be used.
 * 
 * \see
 * hal_uart1_getRxRingBuffer | hal_uart1_forceTxMove
 */
ringbuf_SContext_t* hal_uart1_getTxRingBuffer( void) {

	extern ringbuf_SContext_t hal_uart1_podTxBuffer;

	return &hal_uart1_podTxBuffer;
}


/*!
 * \brief
 * Gets the ring buffer context of the primary UART receiver.
 * 
 * \returns
 * The primary UART receiver buffer context.
 * 
 * The primary UART receiver is interrupt based. Thus, it requires an initialized buffer.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The context must be initialized with #ringbuf_init() before the buffer can be used.
 * 
 * \see
 * hal_uart1_getTxRingBuffer | hal_uart1_forceRxMove
 */
ringbuf_SContext_t* hal_uart1_getRxRingBuffer( void) {

	extern ringbuf_SContext_t hal_uart1_podRxBuffer;

	return &hal_uart1_podRxBuffer;
}

#endif /* hal_uart1_h__ */
