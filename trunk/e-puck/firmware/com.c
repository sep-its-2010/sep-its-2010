#include <string.h>

#include "hal_uart1.h"
#include "hal_int.h"
#include "ringbuf.h"

#include "com.h"


enum {
	BTM_SPP_INCOMING_LINK_ESTABLISHED = 0x0C, ///< Specifies the bluetooth module opcode for incoming serial connections.
	BTM_SPP_LINK_RELEASED = 0x0E, ///< Specifies the bluetooth module opcode for closed serial connections.
	BTM_MIN_FRAME_LEN = 7, ///< Specifies the minimal bluetooth module frame length.
	BTM_REQUEST_MESSAGE = 0x5202, ///< Bluetooth module request message (start sequence + type).
	BTM_RESPONSE_MESSAGE = 0x4302, ///< Bluetooth module response message (start sequence + type).
	BTM_INDICATION_MESSAGE = 0x6902, ///< Bluetooth module indication message (start sequence + type).
	BTM_REPLY_MESSAGE = 0x7202 ///< Bluetooth module reply message (start sequence + type).
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
 * Holds an optional connection management event callback.
 * 
 * \remarks
 * The pointer is initialized by #com_init().
 * 
 * \see
 * com_processIncoming
 */
static com_fnConnectionEvent_t s_fnConnectionEvent = NULL;


/*!
 * \brief
 * Initializes the bluetooth communication interface.
 *
 * \param _fnConnectionEvent
 * Specifies an optional connection management event callback.
 * 
 * All message handlers are cleared and the connection management callback is set. Check #com_fnConnectionEvent_t for details.
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
void com_init(
	IN OPT const com_fnConnectionEvent_t _fnConnectionEvent
	) {

	memset( s_afnHandlers, 0, sizeof( s_afnHandlers));
	s_fnConnectionEvent = _fnConnectionEvent;
}


/*!
 * \brief
 * Processes incoming bluetooth messages.
 * 
 * Handles incoming messages according to the chain-of-responsibility pattern.
 * The handler call order is not guaranteed to be based on the registration sequence.
 * 
 * Messages originating directly from the bluetooth module are handled separately and can trigger the connection management
 * callback in case a connection is established or closed. The event source is already dropped when the callback triggers.
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - The priority will escalate to the primary UART transmitter interrupt priority only by need.
 *
 * \warning
 * This function may not be preempted by any function from this module but #com_send().
 * 
 * \see
 * com_init
 */
void com_processIncoming( void) {

	hal_uart1_flushRx();

	// Get message type if available
	uint16_t ui16BufUsage;
	uint16_t ui16MessageType;
	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_RECEIVER)) {
		ui16BufUsage = ringbuf_getUsage( hal_uart1_getRxRingBuffer());
		ringbuf_getRange( hal_uart1_getRxRingBuffer(), &ui16MessageType, 0, sizeof( ui16MessageType));
	}
	if( ui16BufUsage >= sizeof( ui16MessageType)) {
		switch( ui16MessageType) {
			case BTM_REPLY_MESSAGE:
			case BTM_INDICATION_MESSAGE:
			case BTM_RESPONSE_MESSAGE:
			case BTM_REQUEST_MESSAGE: {

				// Enough data for a minimal bluetooth message frame?
				if( ui16BufUsage >= BTM_MIN_FRAME_LEN) {
					struct {
						uint8_t ui8StartSequence;
						uint8_t ui8Type;
						uint8_t ui8OpCode;
						uint16_t ui16PayloadLen;
					} __attribute__( ( packed)) podHeader = { 0, 0, 0, 0 };
					HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__UART1_RECEIVER)) {
						ringbuf_getRange( hal_uart1_getRxRingBuffer(), &podHeader, 0, sizeof( podHeader));
						if( ui16BufUsage >= podHeader.ui16PayloadLen + BTM_MIN_FRAME_LEN) {
 							ringbuf_drop( hal_uart1_getRxRingBuffer(), podHeader.ui16PayloadLen + BTM_MIN_FRAME_LEN);
						}
					}

					// Frame complete? Incoming connection or connection closed? -> trigger event if registered.
					if( s_fnConnectionEvent && 
						( ui16BufUsage >= podHeader.ui16PayloadLen + BTM_MIN_FRAME_LEN) && ui16MessageType == BTM_INDICATION_MESSAGE &&
						( podHeader.ui8OpCode == BTM_SPP_INCOMING_LINK_ESTABLISHED || podHeader.ui8OpCode == BTM_SPP_LINK_RELEASED)) {

						s_fnConnectionEvent( podHeader.ui8OpCode == BTM_SPP_INCOMING_LINK_ESTABLISHED);
					}
				}
				break;
			}
			default: {

				// Complete external command frame?
				com_SMessage_t podMessage;
				if( ui16BufUsage >= sizeof( podMessage)) {
					hal_uart1_read( &podMessage, sizeof( podMessage));
					bool blProcessed = false;
					for( uint16_t ui16 = 0; !blProcessed && ui16 < COM_MAX_HANDLERS; ui16++) {
						if( s_afnHandlers[ui16]) {
							blProcessed = s_afnHandlers[ui16]( &podMessage);
						}
					}
				}
			}
		}
	}
}


/*!
 * \brief
 * Sends a message.
 * 
 * \param _lppodMessage
 * Specifies the message to be sent.
 *
 * This function forwards directly to #hal_uart1_write(). Refer to that function for interrupt safety.
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
 * The callbacks are called by #com_processIncoming() when a new message is dispatched. One cannot register more than
 * #COM_MAX_HANDLERS callbacks.
 *
 * The following message types may not be used:
 * - \c 0x4302 (#BTM_RESPONSE_MESSAGE)
 * - \c 0x5202 (#BTM_REQUEST_MESSAGE)
 * - \c 0x6902 (#BTM_INDICATION_MESSAGE)
 * - \c 0x7202 (#BTM_REPLY_MESSAGE)
 * 
 * \remarks
 * - The communication interface needs to be initialized.
 * - A handler must return \c true if it processed the message or it must return \c false otherwise.
 *
 * \warning
 * This function may not be preempted by any function from this module but #com_send().
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
		uint16_t ui16NextFree = COM_MAX_HANDLERS;
		for( uint16_t ui16 = 0; !blExists && ui16 < COM_MAX_HANDLERS; ui16++) {
			if( s_afnHandlers[ui16] == _fnHandler) {
				blExists = true;
			} else if( ui16NextFree == COM_MAX_HANDLERS && !s_afnHandlers[ui16]) {
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
 * This function may not be preempted by any function from this module but #com_send().
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
