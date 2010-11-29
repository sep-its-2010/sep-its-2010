#include <string.h>

#include "hal_uart1.h"
#include "hal_int.h"
#include "ringbuf.h"

#include "com.h"


enum {
	BTM_MIN_FRAME_LEN = 7,
	BTM_FRAME_DATA_LEN_OFFSET = 3
};

/*!
 * \brief
 * Holds the message handler slots.
 * 
 * \remarks
 * The array is initialized by #com_init().
 * 
 * \see
 * com_processIncoming | com_register | com_unregister
 */
static com_fnMessageHandler_t s_afnHandlers[COM_MAX_HANDLERS] = { 0 };


/*!
 * \brief
 * Holds the receiver ring buffer space.
 * 
 * \remarks
 * The associated ring buffer is initialized by #com_init().
 * 
 * \see
 * s_aui8TxBufferSpace | com_processIncoming
 */
static uint8_t s_aui8RxBufferSpace[COM_RX_BUFFER_SIZE];


/*!
 * \brief
 * Holds the transmitter ring buffer space.
 * 
 * \remarks
 * The associated ring buffer is initialized by #com_init().
 * 
 * \see
 * s_aui8RxBufferSpace | com_send
 */
static uint8_t s_aui8TxBufferSpace[COM_TX_BUFFER_SIZE];


/*!
 * \brief
 * Initializes the bluetooth communication interface.
 * 
 * The following actions are taken:
 * - Receiver and transmitter ring buffer initialization
 * - UART1 module activation including interrupts
 * - Bluetooth module initialization in transparent mode
 * - Chain-of-responsibility handler slots reset.
 * 
 * \warning
 * - Calling this function must be exclusive to any other function calls of this or lower layers.
 * - This function is not interrupt safe.
 * 
 * \see
 * com_processIncoming | com_send
 */
void com_init( void) {

	ringbuf_init( hal_uart1_getRxRingBuffer(), s_aui8RxBufferSpace, sizeof( s_aui8RxBufferSpace));
	ringbuf_init( hal_uart1_getTxRingBuffer(), s_aui8TxBufferSpace, sizeof( s_aui8TxBufferSpace));
	hal_uart1_configure( HAL_UART_CONFIG__8N1, COM_UART1_BAUDRATE_DIVISOR);
	hal_uart1_open( true);
	hal_int_setPriority( HAL_INT_SOURCE__UART1_RECEIVER, HAL_INT_PRIORITY__7);
	hal_int_setPriority( HAL_INT_SOURCE__UART1_TRANSMITTER, HAL_INT_PRIORITY__7);

	memset( s_afnHandlers, 0, sizeof( s_afnHandlers) / sizeof( *s_afnHandlers));
}


/*!
 * \brief
 * Processes incoming bluetooth messages.
 * 
 * \returns
 * - \c true: dispatched a message.
 * - \c false: no message available.
 * 
 * Handles incoming messages according to the chain-of-responsibility pattern.
 * The handler call order is not guaranteed to be based on the registration sequence.
 * 
 * \remarks
 * The communication interface needs to be initialized.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * com_init
 */
bool com_processIncoming( void) {

	bool blHasMessage = false;

	hal_uart1_forceRxMove();
	com_EMessageType_t eType;
	const uint16_t ui16RingBufSize = ringbuf_getUsage( hal_uart1_getRxRingBuffer());
	if( ui16RingBufSize >= sizeof( eType)) {
		ringbuf_getRange( hal_uart1_getRxRingBuffer(), &eType, 0, sizeof( eType));
		switch( eType) {
			case COM_MESSAGE_TYPE__BTM_REPLY:
			case COM_MESSAGE_TYPE__BTM_INDICATION:
			case COM_MESSAGE_TYPE__BTM_RESPONSE:
			case COM_MESSAGE_TYPE__BTM_REQUEST: {
				if( ui16RingBufSize >= BTM_MIN_FRAME_LEN) {
					uint16_t ui16DataLen = 0;
					ringbuf_getRange( hal_uart1_getRxRingBuffer(), &ui16DataLen, BTM_FRAME_DATA_LEN_OFFSET, sizeof( ui16DataLen));
					if( ui16RingBufSize >= ui16DataLen + BTM_MIN_FRAME_LEN) {

						// TODO: analyze
						ringbuf_drop( hal_uart1_getRxRingBuffer(), ui16DataLen + BTM_MIN_FRAME_LEN);
					}
				}
				break;
			}
			default: {
				com_SMessage_t podMessage;
				if( ui16RingBufSize >= sizeof( podMessage)) {
					hal_uart1_read( &podMessage, sizeof( podMessage));

					// TODO: process
				}
			}
		}
	}

	return blHasMessage;
}


/*!
 * \brief
 * Sends a message.
 * 
 * \param _lppodMessage
 * Specifies the message to be sent.
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - This function is interrupt safe.
 * 
 * \see
 * com_init
 */
void com_send(
	IN const com_SMessage_t* const _lppodMessage
	) {

	hal_uart1_write( _lppodMessage, sizeof( *_lppodMessage));
}


/*!
 * \brief
 * Registers the specified callback within the message handler.
 * 
 * \param _fnHandler
 * Specifies a handler callback.
 * 
 * \returns
 * - \c true: Success
 * - \c false: The handler already exists or the callback list is full.
 * 
 * The callbacks are called by #com_processIncoming().
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - A handler must return true if it processed the message and false otherwise.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * com_init | com_unregister
 */
bool com_register(
	IN const com_fnMessageHandler_t _fnHandler
	) {

	bool blSuccess = false;

	if( _fnHandler) {

	}

	return blSuccess;
}


/*!
 * \brief
 * Removes the specified callback from the message handler.
 * 
 * \param _fnHandler
 * Specifies the handler to be removed.
 * 
 * \remarks
 * The communication interface needs to be initialized.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * com_init | com_register
 */
void com_unregister(
	IN const com_fnMessageHandler_t _fnHandler
	) {

	if( _fnHandler) {

	}
}
