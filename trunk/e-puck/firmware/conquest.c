#include <string.h>
#include <stdlib.h>

#include "hal_rtc.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "com.h"

#include "conquest.h"

static const EType_t s_aaeMap[DIM_X][DIM_Y] = {
	{ TYPE__DOWN_RIGHT, TYPE__UP_T,		TYPE__UP_T,		TYPE__UP_T,		TYPE__DOWN_LEFT },
	{ TYPE__LEFT_T,		TYPE__CROSS,	TYPE__DOWN_T,	TYPE__CROSS,	TYPE__RIGHT_T },
	{ TYPE__LEFT_T,		TYPE__RIGHT_T,	TYPE__INVALID,	TYPE__LEFT_T,	TYPE__RIGHT_T },
	{ TYPE__LEFT_T,		TYPE__CROSS,	TYPE__UP_T,		TYPE__CROSS,	TYPE__RIGHT_T },
	{ TYPE__UP_RIGHT,	TYPE__DOWN_T,	TYPE__DOWN_T,	TYPE__DOWN_T,	TYPE__UP_LEFT }
};

static EDirection_t s_eDirection = DIRECTION_UP;
static uint16_t s_ui16Speed = INITIAL_SPEED;
static uint16_t s_ui16PosX = INITIAL_POS_X;
static uint16_t s_ui16PosY = INITIAL_POS_Y;

static void visualizeDirection( void);

void visualizeDirection( void) {

	uint16_t ui16RawDirections = s_aaeMap[s_ui16PosY][s_ui16PosX] & 0xFF;
	if( s_eDirection == DIRECTION_RIGHT) {
		ui16RawDirections <<= 6;
		ui16RawDirections |= ui16RawDirections >> 8;
	} else if( s_eDirection == DIRECTION_DOWN) {
		ui16RawDirections <<= 4;
		ui16RawDirections |= ui16RawDirections >> 8;
	} else if( s_eDirection == DIRECTION_LEFT) {
		ui16RawDirections <<= 2;
		ui16RawDirections |= ui16RawDirections >> 8;
	}

	hal_led_set( ui16RawDirections & 0xFF);
}

bool cbDemoMessageHandler(
	IN const com_SMessage_t* _lppodMessage
	) {

	bool blAccepted = true;
	switch( _lppodMessage->eType) {
		case COM_MESSAGE_TYPE__REQUEST_RESET: {
			s_eDirection = DIRECTION_UP;
			s_ui16Speed = INITIAL_SPEED;
			s_ui16PosX = INITIAL_POS_X;
			s_ui16PosY = INITIAL_POS_Y;
			hal_led_switchOff( TYPE__CROSS & 0xFF);

			com_SMessage_t podResponse;
			podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_OK;
			com_send( &podResponse);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_STATUS: {
			com_SMessage_t podResponse;
			podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_STATUS;
			uint32_t ui32UpTime = hal_rtc_getSystemUpTime();
			memcpy( podResponse.aui8Data, &ui32UpTime, sizeof( ui32UpTime));
			memset( &podResponse.aui8Data[4], 0, 3 + 8);
			podResponse.aui8Data[15] = s_aaeMap[s_ui16PosY][s_ui16PosX] >> 8;
			visualizeDirection();
			com_send( &podResponse);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_TURN: {
			int16_t i16NewDir = ( ( ( (int8_t)_lppodMessage->aui8Data[0]) % 4) + 4) % 4;
			if( s_eDirection == DIRECTION_LEFT) {
				i16NewDir += 3;
			} else if( s_eDirection == DIRECTION_RIGHT) {
				i16NewDir++;
			} else if( s_eDirection == DIRECTION_DOWN) {
				i16NewDir += 2;
			}
			i16NewDir %= 4;
			if( !i16NewDir) {
				s_eDirection = DIRECTION_UP;
			} else if( i16NewDir == 1) {
				s_eDirection = DIRECTION_RIGHT;
			} else if( i16NewDir == 2) {
				s_eDirection = DIRECTION_DOWN;
			// i16NewDir == 3
			} else {
				s_eDirection = DIRECTION_LEFT;
			}

			if( _lppodMessage->aui8Data[0]) {
				for( uint16_t ui16 = 0; ui16 < abs( ( (int8_t)_lppodMessage->aui8Data[0]) % 4); ui16++) {
					hal_motors_setSpeed( 0, (int8_t)_lppodMessage->aui8Data[0] < 0 ? -s_ui16Speed : s_ui16Speed);
					hal_motors_setSteps( 0);
					while( hal_motors_getStepsLeft() < TURN_STEPS && hal_motors_getStepsRight() < TURN_STEPS)
						;
					hal_motors_setSpeed( 0, 0);
				}
			}

			visualizeDirection();

			com_SMessage_t podResponse;
			podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_OK;
			com_send( &podResponse);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_MOVE: {
			com_SMessage_t podResponse;
			if( s_aaeMap[s_ui16PosY][s_ui16PosX] & s_eDirection) {
				if( s_eDirection == DIRECTION_UP) {
					s_ui16PosY--;
				} else if( s_eDirection == DIRECTION_RIGHT) {
					s_ui16PosX++;
				} else if( s_eDirection == DIRECTION_DOWN) {
					s_ui16PosY++;

				// s_eDirection == DIRECTION_LEFT
				} else {
					s_ui16PosX--;
				}

				hal_motors_setSpeed( s_ui16Speed, 0);
				hal_motors_setSteps( 0);
				while( hal_motors_getStepsLeft() < FOWARD_STEPS && hal_motors_getStepsRight() < FOWARD_STEPS)
					;
				hal_motors_setSpeed( 0, 0);

				podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_HIT_NODE;
				podResponse.aui8Data[0] = s_aaeMap[s_ui16PosY][s_ui16PosX] >> 8;
				podResponse.aui8Data[1] = false;
				visualizeDirection();
			} else {
				podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_REJECTED;
			}
			com_send( &podResponse);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_SET_SPEED: {
			com_SMessage_t podResponse;
			if( _lppodMessage->aui8Data[0] <= 100) {
				s_ui16Speed = 10 * _lppodMessage->aui8Data[0];
				podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_OK;
			} else {
				podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_REJECTED;
			}
			com_send( &podResponse);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_SET_LED: {
			uint16_t ui16 = _lppodMessage->aui8Data[0] | ( _lppodMessage->aui8Data[1] << 8);
			hal_led_set( ui16);
			com_SMessage_t podResponse;
			podResponse.eType = COM_MESSAGE_TYPE__RESPONSE_OK;
			com_send( &podResponse);
			break;
		}
		default: {
			blAccepted = false;
		}
	}

	return blAccepted;
}
