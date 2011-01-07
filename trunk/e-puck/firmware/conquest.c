#include <string.h>
#include <stdlib.h>

#include "hal_rtc.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "hal_uart1.h"
#include "com.h"
#include "sen_line.h"
#include "sen_prox.h"

#include "subs.h"
#include "subs.h"
#include "subs_abyss.h"
#include "subs_calibration.h"
#include "subs_collision.h"
#include "subs_initial.h"
#include "subs_line.h"
#include "subs_movement.h"
#include "subs_node.h"

#include "conquest.h"


enum {
	SYS_UP_TIME_STATUS_OFFSET = 0,
	ABYSS_STATUS_OFFSET = SYS_UP_TIME_STATUS_OFFSET + sizeof( uint32_t),
	COLLISION_STATUS_OFFSET = ABYSS_STATUS_OFFSET + SEN_LINE_NUM_SENSORS,
	LAST_NODE_STATUS_OFFSET = COLLISION_STATUS_OFFSET + SEN_PROX_NUM_SENSORS
};

volatile conquest_EState_t conquest_eMoveRequest = CONQUEST_STATE__STOP;
volatile conquest_ENode_t conquest_eLastNodeType = CONQUEST_NODE__INVALID;
volatile uint16_t conquest_ui16Speed = CONQUEST_INITIAL_SPEED;


static bool cbHandleDefault(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestStatus(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestReset(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestSetLED(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestSetSpeed(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestMove(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	);

void conquest_init( void) {

	com_register( cbHandleRequestStatus);
	com_register( cbHandleRequestReset);
	com_register( cbHandleRequestSetLED);
	com_register( cbHandleRequestSetSpeed);
	com_register( cbHandleRequestTurn);
	com_register( cbHandleRequestMove);
	com_setDefault( cbHandleDefault);

	subs_register( subs_calibration_run, subs_calibration_reset, 0xFF);
	subs_register( subs_abyss_run, subs_abyss_reset, 0xEF);
	subs_register( subs_initial_run, subs_initial_reset, 0xE5);
// 	subs_register( subs_collision_run, subs_collision_reset, 0xDF);
 	subs_register( subs_movement_run, subs_movement_reset, 0xCF);
	subs_register( subs_node_run, subs_node_reset, 0xBF);
 	subs_register( subs_line_run, subs_line_reset, 0xAF);
}


conquest_ENode_t conquest_convertDirMaskToNode(
	IN const uint16_t _ui16DirectionMask
	) {

	conquest_ENode_t eNodeType;

	switch( _ui16DirectionMask & 0xFF) {
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


bool cbHandleDefault(
	IN const com_SMessage_t UNUSED* const _lppodMessage
	) {

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED;
	com_send( &podResponse);

	return true;
}


/*!
 * \brief
 * Handles status-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible a message containing all current status-data will be created and sent to the smartphone.
 * 
 * \remarks
 * Handler-functions have to be registered.
 */
bool cbHandleRequestStatus(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_STATUS) {
		com_SMessage_t podResponse;
		podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS;

		// System up-time in little endian format
		uint32_t ui32UpTime = hal_rtc_getSystemUpTime();
		memcpy( &podResponse.aui8Data[SYS_UP_TIME_STATUS_OFFSET], &ui32UpTime, sizeof( ui32UpTime));

		// Abyss status
		sen_line_SData_t podLine;
		sen_line_read( &podLine);
		sen_line_rescale( &podLine, &podLine);
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__LEFT] = podLine.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD;
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__MIDDLE] = podLine.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD;
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__RIGHT] = podLine.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD;

		// Collision status
		sen_prox_SData_t podProxSensorData;
		sen_prox_getCurrent( &podProxSensorData);
		for( uint16_t ui16 = 0; ui16 < SEN_PROX_NUM_SENSORS; ui16++) {
			podResponse.aui8Data[COLLISION_STATUS_OFFSET + ui16] = podProxSensorData.aui8Data[ui16] > SUBS_COLLISION_THRESHOLD;
		}

		// Last visited node-type
		if( conquest_getLastNode() == CONQUEST_NODE__INVALID) {
			conquest_setState( CONQUEST_STATE__IDENTIFY_NODE);

			// Wait for subs_initial to finish
			while( conquest_getState() == CONQUEST_STATE__IDENTIFY_NODE)
				;
		}
		podResponse.aui8Data[LAST_NODE_STATUS_OFFSET] = conquest_getLastNode() >> 8;

		com_send( &podResponse);

		blHandledMessage = true;
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Handles reset-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the robot will be reset to its default state.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 */
bool cbHandleRequestReset(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_RESET) {
		subs_reset();
		hal_motors_setSpeed( 0, 0);
		hal_motors_setSteps( 0);
		conquest_setLastNode( CONQUEST_NODE__INVALID);
		conquest_setRequestedLineSpeed( CONQUEST_INITIAL_SPEED);
		conquest_setState( CONQUEST_STATE__STOP);

		com_SMessage_t podResponse;
		podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
		com_send( &podResponse);
		blHandledMessage = true;
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Handles SetLED-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the LEDs which are specified in the first ten bytes of the message-data will be switched on,
 * all other LEDs will be switched off.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 */
bool cbHandleRequestSetLED(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_SET_LED) {
		hal_led_set( _lppodMessage->aui8Data[0] | ( _lppodMessage->aui8Data[1] << 8));

		com_SMessage_t podResponse;
		podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
		com_send( &podResponse);

		blHandledMessage = true;
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Handles setSpeed-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the first 8 bit of the transmitted data represent an Integer which delivers the new line-speed after it is multiplied with 10.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 *
 * \see
 * cbHandleRequestMove | cbHandleRequestTurn | cbHandleRequestSetLED
 */
bool cbHandleRequestSetSpeed(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_SET_SPEED) {
		com_SMessage_t podResponse;
		if( _lppodMessage->aui8Data[0] <= 100) {
			conquest_setRequestedLineSpeed( 10 * _lppodMessage->aui8Data[0]);
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
		} else {
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED;
		}
		com_send( &podResponse);

		blHandledMessage = true;
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Handles turn-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the number of turns is analyzed. Here the first Byte of the transmitted data is a signed Integer.
 * A positive Integer indicates the number of 90°-turns in clockwise-rotation, negative Integer indicate the number of 90°-turns in counterclockwise-rotation.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 * Integers between -2 and 2 are expected.
 *
 * \see
 * cbHandleRequestMove | cbHandleRequestSetSpeed
 */
bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;
	
	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_TURN) {
		if( conquest_getState() == CONQUEST_STATE__STOP) {
			if( (int8_t)_lppodMessage->aui8Data[0] > 0) {
				for( uint16_t ui16 = abs( (int8_t)_lppodMessage->aui8Data[0]); ui16; ui16--) {
					conquest_setState( CONQUEST_STATE__TURN_RIGHT);
					while( conquest_getState() != CONQUEST_STATE__STOP)
						;
				}
			} else if( (int8_t)_lppodMessage->aui8Data[0] < 0) {
				for( uint16_t ui16 = abs( (int8_t)_lppodMessage->aui8Data[0]); ui16; ui16--) {
					conquest_setState( CONQUEST_STATE__TURN_LEFT);
					while( conquest_getState() != CONQUEST_STATE__STOP)
						;
				}
			}
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
			com_send( &podResponse);

		} else {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED;
			com_send( &podResponse);
		}
		blHandledMessage = true;
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Handles move-requests.
 * 
 * \param _lppodMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the robot gets the advice to start moving as soon as subs_run is called.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 *
 * \see
 * cbHandleRequestTurn | cbHandleRequestSetSpeed
 */
bool cbHandleRequestMove(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_MOVE) {
		if( conquest_getState() == CONQUEST_STATE__STOP && ( conquest_getLastNode() & CONQUEST_DIRECTION__UP)) {
			conquest_setState( CONQUEST_STATE__CENTER_AND_MOVE);

			while( conquest_getState() != CONQUEST_STATE__STOP && conquest_getState() != CONQUEST_STATE__ABYSS)
				;

			if( conquest_getState() == CONQUEST_STATE__STOP) {
				com_SMessage_t podResponse;
				podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE;
				podResponse.aui8Data[0] = conquest_getLastNode() >> 8;
				com_send( &podResponse);
			}
		} else {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED;
			com_send( &podResponse);
		}
		blHandledMessage = true;
	}

	return blHandledMessage;
}
