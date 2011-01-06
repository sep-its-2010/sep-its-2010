#ifndef conquest_h__
#define conquest_h__

#include "common.h"

#include "com.h"
#include "hal_motors.h"

#include "conquest_types.h"


enum {
	CONQUEST_INITIAL_SPEED = 1000
};


void conquest_init( void);

conquest_ENode_t conquest_convertDirMaskToNode(
	IN const uint16_t _ui16DirectionMask
	);

static inline uint16_t conquest_getRequestedLineSpeed( void);
static inline void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	);

static inline conquest_ENode_t conquest_getLastNode( void);
static inline void conquest_setLastNode(
	IN const conquest_ENode_t _eNodeType
	);

static inline conquest_EState_t conquest_getState( void);
static inline void conquest_setState(
	IN const conquest_EState_t _eMove
	);

uint16_t conquest_getRequestedLineSpeed( void) {

	extern volatile uint16_t conquest_ui16Speed;

	return conquest_ui16Speed;
}


void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	) {

	extern volatile uint16_t conquest_ui16Speed;

	conquest_ui16Speed = min( _ui16RequestedSpeed, HAL_MOTORS_MAX_ABS_SPEED);
}


conquest_ENode_t conquest_getLastNode( void) {

	extern volatile conquest_ENode_t conquest_eLastNodeType;

	return conquest_eLastNodeType;
}


void conquest_setLastNode(
	IN const conquest_ENode_t _eNodeType
	) {

	extern volatile conquest_ENode_t conquest_eLastNodeType;

	conquest_eLastNodeType = _eNodeType;
}


conquest_EState_t conquest_getState( void) {

	extern volatile conquest_EState_t conquest_eMoveRequest;

	return conquest_eMoveRequest;
}

void conquest_setState(
	IN const conquest_EState_t _eState
	) {

	extern volatile conquest_EState_t conquest_eMoveRequest;

	conquest_eMoveRequest = _eState;
}

#endif /* conquest_h__ */
