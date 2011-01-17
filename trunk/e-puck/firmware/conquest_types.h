#ifndef conquest_types_h__
#define conquest_types_h__

#include "common.h"

#include "sen_line_types.h"
#include "sen_prox_types.h"


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


/*!
 * \brief
 * Specifies the node edges as bit mask.
 */
typedef enum {
	CONQUEST_DIRECTION__UP = 1, ///< Node edge in face direction.
	CONQUEST_DIRECTION__RIGHT = 4, ///< Node edge to the right of the face direction.
	CONQUEST_DIRECTION__DOWN = 16, ///< Node edge in opposite to the face direction.
	CONQUEST_DIRECTION__LEFT = 64 ///< Node edge to the left of the face direction.
} conquest_EDirection_t;


/*!
 * \brief
 * Specifies all available node types.
 *
 * The high byte represents the node type index. The low byte represents the direction mask (#conquest_EDirection_t).
 */
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


/*!
 * \brief
 * Specifies the states of the subsumption FSM.
 * 
 * Each subsumption layer can map to any number of subsumption states.
 * 
 * \see
 * conquest_getState | conquest_setState
 */
typedef enum {
	CONQUEST_STATE__START = 0, ///< Entry state of the global logic.
	CONQUEST_STATE__CALIBRATION, ///< Manually triggered calibration.
	CONQUEST_STATE__INITIAL, ///< Initial node detection state.
	CONQUEST_STATE__STOP, ///< Initialized & idle.
	CONQUEST_STATE__MOVE_FORWARD, ///< Advancing to next node.
	CONQUEST_STATE__TURN_LEFT, ///< Turn left on current node.
	CONQUEST_STATE__TURN_RIGHT, ///< Turn right on current node.
	CONQUEST_STATE__CENTER_LINE, ///< Center on the current line.
	CONQUEST_STATE__HIT_NODE, ///< Node detected & stopped.
	CONQUEST_STATE__COLLISION, ///< Collision detected & stopped.
	CONQUEST_STATE__ABYSS, ///< Abyss detected & stopped.
	CONQUEST_STATE__RETURN_NODE ///< A Collision happended; return to the last node.
} conquest_EState_t;


/*!
 * \brief
 * Specifies the states of the message FSM.
 * 
 * Each message handler has its own synchronization state.
 * 
 * \see
 * conquest_init
 */
typedef enum {
	CONQUEST_MESSAGE_STATE__NONE, ///< Ready to handle a message.
	CONQUEST_MESSAGE_STATE__RESET, ///< Processing a reset request.
	CONQUEST_MESSAGE_STATE__GET_STATUS, ///< Processing a status request.
	CONQUEST_MESSAGE_STATE__TURN, ///< Processing a turn request.
	CONQUEST_MESSAGE_STATE__MOVE, ///< Processing a move request.
	CONQUEST_MESSAGE_STATE__SET_SPEED, ///< Processing a speed change request.
	CONQUEST_MESSAGE_STATE__SET_LED ///< Processing a LED change request.
} conquest_EMessageState_t;


/*!
 * \brief
 * Specifies the heartbeat sensor image.
 *
 * This image is filled by #cbHeartbeat().
 * 
 * \see
 * conquest_getSensorImage
 */
typedef struct {
	sen_line_SData_t podRawLineSensors; ///< Holds the raw line sensor values.
	sen_line_SData_t podCalibratedLineSensors; ///< Holds the calibrated line sensor values.
	sen_prox_SData_t podRawProximitySensors; ///< Holds the raw IR proximity sensor values.
	bool ablCollisionMask[SEN_PROX_NUM_SENSORS]; ///< Holds a collision boolean for each IR proximity sensor.
	bool ablAbyssMask[SEN_LINE_NUM_SENSORS]; ///< Holds an abyss boolean for each line sensor.
	uint8_t : 8;
} conquest_SSensorImage_t;


#endif // conquest_types_h__
