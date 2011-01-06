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
	CONQUEST_DIRECTION__UP = 1,
	CONQUEST_DIRECTION__RIGHT = 4,
	CONQUEST_DIRECTION__DOWN = 16,
	CONQUEST_DIRECTION__LEFT = 64
} conquest_EDirection_t;


typedef enum {
	CONQUEST_NODE__INVALID    = 0x0900,
	CONQUEST_NODE__CROSS      = 0x0800 | CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT,
	CONQUEST_NODE__UP_T       = 0x0400 |                          CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT,
	CONQUEST_NODE__LEFT_T     = 0x0700 | CONQUEST_DIRECTION__UP |                            CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT,
	CONQUEST_NODE__DOWN_T     = 0x0600 | CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT |                            CONQUEST_DIRECTION__RIGHT,
	CONQUEST_NODE__RIGHT_T    = 0x0500 | CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN,
	CONQUEST_NODE__UP_LEFT    = 0x0300 | CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT,
	CONQUEST_NODE__UP_RIGHT   = 0x0200 | CONQUEST_DIRECTION__UP |                                                       CONQUEST_DIRECTION__RIGHT,
	CONQUEST_NODE__DOWN_LEFT  = 0x0100 |                          CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN,
	CONQUEST_NODE__DOWN_RIGHT = 0x0000 |                                                     CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT
} conquest_ENode_t;


typedef enum {
	CONQUEST_STATE__STOP,
	CONQUEST_STATE__CENTER_AND_MOVE,
	CONQUEST_STATE__MOVE_FOWARD,
	CONQUEST_STATE__TURN_LEFT,
	CONQUEST_STATE__TURN_RIGHT,
	CONQUEST_STATE__IDENTIFY_NODE,
	CONQUEST_STATE__ABYSS
} conquest_EState_t;


#endif // conquest_types_h__
