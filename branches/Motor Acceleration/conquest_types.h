#ifndef conquest_types_h__
#define conquest_types_h__


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
	CONQUEST_MESSAGE_TYPE__REQUEST_RESET = 0x01FF, ///< Reset e-puck logic.
	CONQUEST_MESSAGE_TYPE__REQUEST_STATUS = 0x02FF, ///< Get e-puck status.
	CONQUEST_MESSAGE_TYPE__REQUEST_TURN = 0x03FF, ///< Turn e-puck by the specified degrees.
	CONQUEST_MESSAGE_TYPE__REQUEST_MOVE = 0x04FF, ///< Move e-puck in view direction until a node is found.
	CONQUEST_MESSAGE_TYPE__REQUEST_SET_SPEED = 0x05FF, ///< Change the e-puck speed.
	CONQUEST_MESSAGE_TYPE__REQUEST_SET_LED = 0x06FF, ///< Set the specified LEDs.

	CONQUEST_MESSAGE_TYPE__RESPONSE_OK = 0x81FF, ///< Request acknowledged.
	CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS = 0x82FF, ///< Current e-puck status.
	CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE = 0x83FF, ///< e-puck has hit a node.
	CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION = 0x84FF, ///< e-puck has detected a collision while moving.
	CONQUEST_MESSAGE_TYPE__RESPONSE_ABYSS = 0x85FF, ///< e-puck has detected an abyss while moving.
	CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED = 0x86FF ///< The previously sent request was rejected.
} conquest_EMessageType_t;


typedef enum {
	CONQUEST_NODE_TYPE__TOP_LEFT_EDGE = 0,
	CONQUEST_NODE_TYPE__TOP_RIGHT_EDGE = 1,
	CONQUEST_NODE_TYPE__BOTTOM_LEFT_EDGE = 2,
	CONQUEST_NODE_TYPE__BOTTOM_RIGHT_EDGE = 3,
	CONQUEST_NODE_TYPE__TOP_T = 4,
	CONQUEST_NODE_TYPE__RIGHT_T = 5,
	CONQUEST_NODE_TYPE__BOTTOM_T = 6,
	CONQUEST_NODE_TYPE__LEFT_T = 7,
	CONQUEST_NODE_TYPE__CROSS = 8,
	CONQUEST_NODE_TYPE__UNKNOWN = 9
} conquest_ENodeType_t;

#endif // conquest_types_h__
