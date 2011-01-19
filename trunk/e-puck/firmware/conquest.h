#ifndef conquest_h__
#define conquest_h__

#include "common.h"

#include "hal_motors_types.h"

#include "conquest_types.h"


enum {
	CONQUEST_COLLISION_THRESHOLD = 32, ///< Specifies the proximity sensor threshold above which a collision is detected.
	CONQUEST_ABYSS_THRESHOLD = 150, ///< Specifies the line sensor threshold below which an abyss is detected.
	CONQUEST_INITIAL_SPEED = 600, ///< Specifies the initial speed returned by #conquest_getRequestedLineSpeed().
	CONQUEST_HEARTBEAT_INTERVAL = 10 ///< Specifies the update rate of the logic in RTC time units. \see hal_rtc_init
};


void conquest_init( void);
void conquest_reset( void);

void conquest_cbConnection(
	IN const bool _blConnected
	);

conquest_EState_t conquest_getState( void);
void conquest_setState(
	IN const conquest_EState_t _eState
	);


static inline conquest_ENode_t conquest_convertDirMaskToNode(
	IN const uint8_t _ui8DirectionMask
	);

static inline uint16_t conquest_getRequestedLineSpeed( void);
static inline void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	);

static inline conquest_ENode_t conquest_getLastNode( void);
static inline void conquest_setLastNode(
	IN const conquest_ENode_t _eNodeType
	);

static inline const conquest_SSensorImage_t* conquest_getSensorImage( void);


/*!
 * \brief
 * Generates the node type from a direction bit mask.
 * 
 * \param _ui8DirectionMask
 * Specifies the node as direction bit mask based on #conquest_EDirection_t.
 * 
 * \returns
 * The fully qualified node type.
 *
 * \remarks
 * This function is fully independent.
 */
conquest_ENode_t conquest_convertDirMaskToNode(
	IN const uint8_t _ui8DirectionMask
	) {

	conquest_ENode_t eNodeType;

	switch( _ui8DirectionMask) {
		case CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__UP_T;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__LEFT_T;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__DOWN_T;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN: {
			eNodeType = CONQUEST_NODE__RIGHT_T;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT: {
			eNodeType = CONQUEST_NODE__UP_LEFT;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__UP_RIGHT;
			break;
		}
		case CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN: {
			eNodeType = CONQUEST_NODE__DOWN_LEFT;
			break;
		}
		case CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__DOWN_RIGHT;
			break;
		}
		case CONQUEST_DIRECTION__UP | CONQUEST_DIRECTION__LEFT | CONQUEST_DIRECTION__DOWN | CONQUEST_DIRECTION__RIGHT: {
			eNodeType = CONQUEST_NODE__CROSS;
			break;
		}
		default: {
			eNodeType = CONQUEST_NODE__INVALID;
		}
	}

	return eNodeType;
}


/*!
 * \brief
 * Returns the requested motor line speed in steps per seconds.
 * 
 * \returns
 * A value ranging from \c 0 to #HAL_MOTORS_MAX_ABS_SPEED.
 * 
 * The initial value is defined by #CONQUEST_INITIAL_SPEED.
 * 
 * \remarks
 * - This function is interrupt safe.
 * 
 * \see
 * conquest_setRequestedLineSpeed
 */
uint16_t conquest_getRequestedLineSpeed( void) {

	extern volatile uint16_t conquest_ui16Speed;

	return conquest_ui16Speed;
}


/*!
 * \brief
 * Sets the desired absolute motor line speed.
 * 
 * \param _ui16RequestedSpeed
 * A value ranging from \c 0 to #HAL_MOTORS_MAX_ABS_SPEED.
 * 
 * \remarks
 * - The new speed will be constrained to the range [ \c 0 ; \c #HAL_MOTORS_MAX_ABS_SPEED ].
 * - This function is interrupt safe.
 * 
 * \see
 * conquest_getRequestedLineSpeed
 */
void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	) {

	extern volatile uint16_t conquest_ui16Speed;

	conquest_ui16Speed = min( _ui16RequestedSpeed, HAL_MOTORS_MAX_ABS_SPEED);
}


/*!
 * \brief
 * Gets the type of the last visited node.
 * 
 * \returns
 * The last node type.
 * 
 * The initial node type is #CONQUEST_NODE__INVALID.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * conquest_setLastNode
 */
conquest_ENode_t conquest_getLastNode( void) {

	extern volatile conquest_ENode_t conquest_eLastNodeType;

	return conquest_eLastNodeType;
}


/*!
 * \brief
 * Sets the type of the last visited node.
 * 
 * \param _eNodeType
 * Specifies the new last visited node type.
 * 
 * This function must be called whenever the e-puck position or orientation changes.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * conquest_setLastNode
 */
void conquest_setLastNode(
	IN const conquest_ENode_t _eNodeType
	) {

	extern volatile conquest_ENode_t conquest_eLastNodeType;

	conquest_eLastNodeType = _eNodeType;
}


/*!
 * \brief
 * Gets the current sensor image.
 * 
 * \returns
 * The sensor image of the current heartbeat cycle.
 * 
 * The sensor image ensures that all logic levels get the same sensor values within a common update call.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * cbHeartbeat
 */
const conquest_SSensorImage_t* conquest_getSensorImage( void) {

	extern conquest_SSensorImage_t conquest_podSensorImage;

	return &conquest_podSensorImage;
}

#endif /* conquest_h__ */
