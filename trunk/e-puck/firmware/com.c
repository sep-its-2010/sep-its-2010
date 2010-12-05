#include <string.h>
#include <stdlib.h>

#include "hal_uart1.h"
#include "hal_int.h"
#include "ringbuf.h"

#include "com.h"


enum {
	BTM_OPCODE_SPP_TRANSPARENT_MODE = 0x11,
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
 * Initializes the bluetooth communication interface.
 * 
 * All message handlers are cleared.
 *
 * \remarks
 * The primary UART should be initialized before using this module.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * com_processIncoming | com_send | hal_uart1_enable | hal_uart1_configure
 */
void com_init( void) {

	memset( s_afnHandlers, 0, sizeof( s_afnHandlers) / sizeof( *s_afnHandlers));
}


/*!
 * \brief
 * Processes incoming bluetooth messages.
 * 
 * \returns
 * Whether or not an external message was dispatched.
 * 
 * Handles incoming messages according to the chain-of-responsibility pattern.
 * The handler call order is not guaranteed to be based on the registration sequence.
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - This function is interrupt safe concerning interrupts from the hal_uart1 module.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * com_init
 */
bool com_processIncoming( void) {

	bool blHasMessage = false;

	hal_int_disable( HAL_INT_SOURCE__UART1_RECEIVER);
	hal_uart1_forceRxMove();

	const uint16_t ui16BufUsage = ringbuf_getUsage( hal_uart1_getRxRingBuffer());
	if( ui16BufUsage >= sizeof( com_EMessageType_t)) {

		com_SMessage_t podMessage;
		ringbuf_getRange( hal_uart1_getRxRingBuffer(), &podMessage.eType, 0, sizeof( podMessage.eType));
		switch( podMessage.eType) {
			case COM_MESSAGE_TYPE__BTM_REPLY:
			case COM_MESSAGE_TYPE__BTM_INDICATION:
			case COM_MESSAGE_TYPE__BTM_RESPONSE:
			case COM_MESSAGE_TYPE__BTM_REQUEST: {
				if( ui16BufUsage >= BTM_MIN_FRAME_LEN) {
					uint16_t ui16DataLen = 0;
					ringbuf_getRange( hal_uart1_getRxRingBuffer(), &ui16DataLen, BTM_FRAME_DATA_LEN_OFFSET, sizeof( ui16DataLen));
					if( ui16BufUsage >= ui16DataLen + BTM_MIN_FRAME_LEN) {
						uint8_t* const lpui8Buffer = calloc( ui16DataLen + BTM_MIN_FRAME_LEN, 1);
						hal_uart1_read( lpui8Buffer, ui16DataLen + BTM_MIN_FRAME_LEN);

// 						if( lpui8Buffer[2] == BTM_OPCODE_SPP_TRANSPARENT_MODE &&
// 							podMessage.eType == COM_MESSAGE_TYPE__BTM_INDICATION) {
// 
// 							com_blConnected = lpui8Buffer[7] > 0 ? true : false;
// 							hal_led_set( 1);
// 						} else {
// 						}

						hal_uart1_write( lpui8Buffer, ui16DataLen + BTM_MIN_FRAME_LEN);
					}
				}

				hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);

				break;
			}
			default: {
				if( ui16BufUsage >= sizeof( podMessage)) {
					hal_uart1_read( &podMessage, sizeof( podMessage));

					bool blProcessed = false;
					for( uint16_t ui16 = 0; !blProcessed && ui16 < COM_MAX_HANDLERS; ui16++) {
						if( s_afnHandlers[ui16]) {
							blProcessed = s_afnHandlers[ui16]( &podMessage);
						}
					}

					blHasMessage = true;
				}

				hal_int_enable( HAL_INT_SOURCE__UART1_RECEIVER);
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
 * - This function is interrupt safe concerning interrupts from the hal_uart1 module.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
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
 * - \c true on success
 * - \c false on error (handler already exists or callback list is full).
 * 
 * The callbacks are called by #com_processIncoming() when a new message is dispatched. One can not register more than
 * #COM_MAX_HANDLERS callbacks.
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - A handler must return \c true if it processed the message and it must return \c false otherwise.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * com_init | com_unregister
 */
bool com_register(
	IN const com_fnMessageHandler_t _fnHandler
	) {

	bool blSuccess = false;

	if( _fnHandler) {
		bool blExists = false;
		uint16_t ui16NextFree = 0;
		for( uint16_t ui16 = 0; ui16 < COM_MAX_HANDLERS; ui16++) {
			if( s_afnHandlers[ui16] == _fnHandler) {
				blExists = true;
			}
			if( ui16NextFree == COM_MAX_HANDLERS && !s_afnHandlers[ui16]) {
				ui16NextFree = ui16;
			}
		}

		if( !blExists && ui16NextFree < COM_MAX_HANDLERS) {
			s_afnHandlers[ui16NextFree] = _fnHandler;
		}
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
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * com_init | com_register
 */
void com_unregister(
	IN const com_fnMessageHandler_t _fnHandler
	) {

	if( _fnHandler) {
		for( uint16_t ui16 = 0; ui16 < COM_MAX_HANDLERS; ui16++) {
			if( s_afnHandlers[ui16] == _fnHandler) {
				s_afnHandlers[ui16] = NULL;
			}
		}
	}
}
