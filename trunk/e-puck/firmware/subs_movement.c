#include "hal_motors.h"
#include "hal_led.h"
#include "com.h"
#include "com_types.h"
#include "sen_line.h"

#include "subs_movement.h"

static bool s_blTurningActive = false; ///< Indicates if the robot is currently performing turnings.
static uint8_t s_ui8PerformedTurnings = 0; ///< Holds a counter for the already performed 90°-turnings.
static int8_t s_i8DemandedTurnings = 0; ///< Holds the number of demanded turnings.
static com_EMessageType_t s_eCurrentMessageType; ///< Specifies the last smartphone-message-type.

static bool cbHandleRequestMove( IN const com_SMessage_t* const _lppodMessage);
static bool cbHandleRequestTurn( IN const com_SMessage_t* const _lppodMessage);
static bool cbHandleRequestSetSpeed( IN const com_SMessage_t* const _lppodMessage);

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
	bool blMovementChanged = false;
	com_SMessage_t podOkMessage = { COM_MESSAGE_TYPE__RESPONSE_OK, {0}};

	switch( s_eCurrentMessageType) {
		case COM_MESSAGE_TYPE__REQUEST_TURN: {
			
			// Turning not active? -> Start to perform the demanded turnings.
			if( !s_blTurningActive) {
				s_blTurningActive = true;
				//hal_motors_setSpeed( hal_motors_si16CurrentLineSpeed, hal_motors_si16CurrentAngularSpeed);
			
			// Exit turning-state after all demanded turnings have been performed.
			} else if( s_blTurningActive) {
				sen_line_SData_t podSensorData = {{0}};
				sen_line_read( &podSensorData);
				
				// Crossed a line? -> One turning is done.
				if( (2 * podSensorData.aui16Data[0] < 1) || // @TODO 1 durch EEPROM-Kalibrierwert ersetzen
					(2 * podSensorData.aui16Data[1] < 1) ||
					(2 * podSensorData.aui16Data[2] < 1)) {
					s_ui8PerformedTurnings++;
				}

				// Performed all demanded turnings? -> Reset state and send message.
				if( s_ui8PerformedTurnings == s_i8DemandedTurnings) {
					hal_motors_setSpeed( 0, 0);
					hal_motors_setSteps( 0);
					s_blTurningActive = false;
					s_ui8PerformedTurnings = 0;
					s_i8DemandedTurnings = 0;
					com_send( &podOkMessage);
				}				
			}			
			blMovementChanged = true;			
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_MOVE: {
			//hal_motors_setSpeed( hal_motors_si16CurrentLineSpeed, hal_motors_si16CurrentAngularSpeed);
			blMovementChanged = true;
			com_send( &podOkMessage);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_SET_SPEED: {
			blMovementChanged = true;
			com_send( &podOkMessage);
			break;
		}
		default: {
			
		}
	}
	return blMovementChanged;
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
 *
 * \see
 * cbHandleRequestMove | cbHandleRequestSetSpeed
 */
bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	) {
	bool blHandledMessage = false;
	
	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_TURN) {
		s_eCurrentMessageType = COM_MESSAGE_TYPE__REQUEST_TURN;
		s_i8DemandedTurnings = _lppodMessage->aui8Data[0];

		if( s_i8DemandedTurnings > 0) {
			//hal_motors_si16CurrentAngularSpeed = 250;
		} else if( s_i8DemandedTurnings <= 0) {
			//hal_motors_si16CurrentAngularSpeed = -250;
		}		
		//hal_motors_si16CurrentLineSpeed = 0;
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
 * If this handler is responsible 
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

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_MOVE) {
		s_eCurrentMessageType = COM_MESSAGE_TYPE__REQUEST_MOVE;
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

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_SET_SPEED) {
		s_eCurrentMessageType = COM_MESSAGE_TYPE__REQUEST_SET_SPEED;
		//hal_motors_si16CurrentLineSpeed = _lppodMessage->aui8Data[0] * 10;
		//hal_motors_si16CurrentAngularSpeed = 0;
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
	s_ui8PerformedTurnings = 0;
	//hal_motors_si16CurrentLineSpeed = 600;
	//hal_motors_si16CurrentAngularSpeed = 0;
	com_register( cbHandleRequestTurn);
	com_register( cbHandleRequestMove);
	com_register( cbHandleRequestSetSpeed);
}
