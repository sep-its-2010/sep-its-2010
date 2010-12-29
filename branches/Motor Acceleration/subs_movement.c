#include <stdlib.h>

#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"
#include "conquest_types.h"

#include "subs_movement.h"


/*!
 * \brief
 * Indicates if the robot is currently in a turning-state.
 */
static bool s_blTurningActive = false;


/*!
 * \brief
 * Holds the number of detected lines on the surface.
 * This value helps to compute the number of already performed turnings.
 */
static uint8_t s_ui8NumberOfDetectedLines = 0;


/*!
 * \brief
 * Holds the number of demanded turnings.
 *
 * This is a value in the range from -2 to 2.
 * Negative values indicate turnings in counter-clockwise direction, positive values indicate turnings in clockwise direction.
 */
static int8_t s_i8DemandedTurnings = 0;


/*!
 * \brief
 * Specifies the type of the last smartphone message.
 */
static uint16_t s_ui16CurrentMessageType;


/*!
 * \brief
 * Holds the current line-speed of the robot in steps-per-seconds.
 * 
 * \remarks
 * Values above below -1000 or above 1000 can cause malfunctioning of the motors.
 */
volatile int16_t subs_movement_i16CurrentLineSpeed = 0;


/*!
 * \brief
 * Holds the current angular-speed of the robot.
 */
volatile int16_t subs_movement_i16CurrentAngularSpeed = 0;


static bool cbHandleRequestMove(
	IN const com_SMessage_t* const _lppodMessage
	);

static bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	);

static bool cbHandleRequestSetSpeed(
	IN const com_SMessage_t* const _lppodMessage
	);


/*!
 * \brief
 * Executes movement-commands sent by the smartphone.
 * 
 * \returns
 * True if a movement-command is going to be performed, false otherwise.
 *
 * Checks if the bluetooth-message-queue contains a message for moving or turning.
 * If there is one the robot starts to perform the demanded movement and sends an acknowledgment to the smartphone and deletes this message.
 */
bool subs_movement_run( void) {

	bool blActed = false;
	com_SMessage_t podOkMessage = { CONQUEST_MESSAGE_TYPE__RESPONSE_OK, {0}};

	switch( s_ui16CurrentMessageType) {
		case CONQUEST_MESSAGE_TYPE__REQUEST_TURN: {
			
			// Turning not active? -> Start to perform the demanded turnings.
			if( !s_blTurningActive) {
				s_blTurningActive = true;
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( subs_movement_i16CurrentLineSpeed, subs_movement_i16CurrentAngularSpeed);
			
			// Exit turning-state after all demanded turnings have been performed.
			} else if( s_blTurningActive) {
				sen_line_SData_t podSensorData = {{0}};
				sen_line_read( &podSensorData);
				sen_line_rescale( &podSensorData, &podSensorData);
				
				// Crossed a line? -> One turning is done.
				if( ( ( podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_MOVEMENT__LINE_THRESHOLD) ||
					( podSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_MOVEMENT__LINE_THRESHOLD) ||
					( podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_MOVEMENT__LINE_THRESHOLD)) &&
					( abs( hal_motors_getStepsLeft()) >= s_ui8NumberOfDetectedLines * 300)) {

					s_ui8NumberOfDetectedLines++;
				}

				// Performed all demanded turnings? -> Reset state and send message.
				if( (s_ui8NumberOfDetectedLines - 1) == s_i8DemandedTurnings) {
					hal_motors_setSpeed( 0, 0);
					hal_motors_setSteps( 0);
					s_blTurningActive = false;
					s_ui8NumberOfDetectedLines = 0;
					s_i8DemandedTurnings = 0;
					com_send( &podOkMessage);
				}				
			}			
			blActed = true;			
			break;
		}
		case CONQUEST_MESSAGE_TYPE__REQUEST_MOVE: {
			hal_motors_setSpeed( subs_movement_i16CurrentLineSpeed, subs_movement_i16CurrentAngularSpeed);
			com_send( &podOkMessage);
			blActed = true;
			break;
		}
		case CONQUEST_MESSAGE_TYPE__REQUEST_SET_SPEED: {			
			com_send( &podOkMessage);
			blActed = true;
			break;
		}
		default: {
			break;
		}
	}

	return blActed;
}

/*!
 * \brief
 * Handles turn-requests.
 * 
 * \param _podMessage
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
		s_ui16CurrentMessageType = CONQUEST_MESSAGE_TYPE__REQUEST_TURN;

		if( (int8_t)_lppodMessage->aui8Data[0] > 0) {
			subs_movement_i16CurrentAngularSpeed = 250;
		} else if( (int8_t)_lppodMessage->aui8Data[0] < 0) {
			subs_movement_i16CurrentAngularSpeed = -250;
		}	
		subs_movement_i16CurrentLineSpeed = 0;
		blHandledMessage = true;
	}

	return blHandledMessage;
}

/*!
 * \brief
 * Handles move-requests.
 * 
 * \param _podMessage
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
		s_ui16CurrentMessageType = CONQUEST_MESSAGE_TYPE__REQUEST_MOVE;
		blHandledMessage = true;
	}

	return blHandledMessage;
}

/*!
 * \brief
 * Handles setSpeed-requests.
 * 
 * \param _podMessage
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
		s_ui16CurrentMessageType = CONQUEST_MESSAGE_TYPE__REQUEST_SET_SPEED;
		subs_movement_i16CurrentLineSpeed = _lppodMessage->aui8Data[0] * 10;

		if( abs(subs_movement_i16CurrentLineSpeed) > 1000) {
			subs_movement_i16CurrentLineSpeed = 0;
			com_SMessage_t podRejectSetSpeed = {CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED, {0}};
			com_send( &podRejectSetSpeed);
		}
		subs_movement_i16CurrentAngularSpeed = 0;
		blHandledMessage = true;
	}

	return blHandledMessage;
}

/*!
 * \brief
 * Resets all movement data.
 * 
 * Registers all handler of this subsumption-layer for the Chain-of-Responsibility pattern.
 */
void subs_movement_reset( void) {

	s_blTurningActive = false;
	s_ui8NumberOfDetectedLines = 0;
	subs_movement_i16CurrentLineSpeed = SUBS_MOVEMENT__INITIAL_LINE_SPEED;
	subs_movement_i16CurrentAngularSpeed = 0;
	com_register( cbHandleRequestTurn);
	com_register( cbHandleRequestMove);
	com_register( cbHandleRequestSetSpeed);
}
