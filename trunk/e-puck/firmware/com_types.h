#ifndef com_types_h__
#define com_types_h__

#include "common.h"

/*!
 * \brief
 * Specifies the required message types for bluetooth communication in transparent mode.
 * 
 * Includes request & response messages as well as bluetooth module management messages.
 * 
 * \remarks
 * Write remarks for  here.
 * 
 * \see
 * com_SMessage_t | com_send
 */
typedef enum {
	COM_MESSAGE_TYPE__REQUEST_RESET = 0x01FF, ///< Reset e-puck logic.
	COM_MESSAGE_TYPE__REQUEST_STATUS = 0x02FF, ///< Get e-puck status.
	COM_MESSAGE_TYPE__REQUEST_TURN = 0x03FF, ///< Turn e-puck by the specified degrees.
	COM_MESSAGE_TYPE__REQUEST_MOVE = 0x04FF, ///< Move e-puck in view direction until a node is found.
	COM_MESSAGE_TYPE__REQUEST_SET_SPEED = 0x05FF, ///< Change the e-puck speed.
	COM_MESSAGE_TYPE__REQUEST_SET_LED = 0x06FF, ///< Set the specified LEDs.

	COM_MESSAGE_TYPE__RESPONSE_OK = 0x81FF, ///< Request acknowledged.
	COM_MESSAGE_TYPE__RESPONSE_STATUS = 0x82FF, ///< Current e-puck status.
	COM_MESSAGE_TYPE__RESPONSE_HIT_NODE = 0x83FF, ///< e-puck has hit a node.
	COM_MESSAGE_TYPE__RESPONSE_COLLISION = 0x84FF, ///< e-puck has detected a collision while moving.
	COM_MESSAGE_TYPE__RESPONSE_ABYSS = 0x85FF, ///< e-puck has detected an abyss while moving.
	COM_MESSAGE_TYPE__RESPONSE_REJECTED = 0x86FF, ///< The previously sent request was rejected.

	COM_MESSAGE_TYPE__BTM_REQUEST = 0x5202, ///< Bluetooth module request message.
	COM_MESSAGE_TYPE__BTM_RESPONSE = 0x4302, ///< Bluetooth module response message.
	COM_MESSAGE_TYPE__BTM_INDICATION = 0x6902, ///< Bluetooth module indication message.
	COM_MESSAGE_TYPE__BTM_REPLY = 0x7202 ///< Bluetooth module reply message.
} com_EMessageType_t;


/*!
 * \brief
 * Specifies the message frame for transparent bluetooth communication.
 * 
 * \see
 * subs_send
 */
typedef struct {
	com_EMessageType_t eType; ///< Holds the message type.
	uint8_t aui8Data[30]; ///< Holds additional data.
} com_SMessage_t;


/*!
 * \brief
 * Specifies a handler function for the chain-of-responsibility pattern.
 * 
 * The function must return true if it handled the message and false otherwise.
 */
typedef bool (*com_fnMessageHandler_t)( const com_SMessage_t* const);

#endif /* com_types_h__ */
