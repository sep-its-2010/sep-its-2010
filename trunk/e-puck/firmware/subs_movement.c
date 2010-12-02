#include "hal_motors.h"
#include "hal_led.h"
#include "com.h"
#include "com_types.h"

#include "subs_movement.h"

enum {
	NINETY_DEGREE_IN_MOTORSTEPS = 250 // @TODO diese Zahl muss noch genauer justiert werden
};

com_EMessageType_t podCurrentMessage; ///< Specifies the last smartphone-message-type.
int16_t i16CurrentLineSpeed = 600; ///< Stores the current movement-speed of the robot. Default: 600 steps per second.
int16_t i16CurrentAngularSpeed = 0; ///< Stores angular-speed for turning in left or right direction.

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
	bool movementChanged = false;

	switch( podCurrentMessage) {
		case COM_MESSAGE_TYPE__REQUEST_TURN: {
			hal_motors_setSpeed( i16CurrentLineSpeed, i16CurrentAngularSpeed);
			// bis Liniensensoren ausschlagen!
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_MOVE: {
			hal_motors_setSpeed( i16CurrentLineSpeed, i16CurrentAngularSpeed);
			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_SET_SPEED: {

			break;
		}
		case COM_MESSAGE_TYPE__REQUEST_SET_LED: {

			break;
		}
		default: {

		}
	}
	return movementChanged;
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
 * cbHandleRequestMove | cbHandleRequestSetSpeed | cbHandleRequestSetLED
 */
bool subs_movement_cbHandleRequestTurn(
	IN const com_SMessage_t* _lppodMessage
	) {

	bool handledMessage = false;
	
	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_TURN) {
		podCurrentMessage = COM_MESSAGE_TYPE__REQUEST_TURN;
		int8_t i8NumberOfTurns = _lppodMessage->aui8Data[0];
		i16CurrentAngularSpeed = i8NumberOfTurns * NINETY_DEGREE_IN_MOTORSTEPS;
		i16CurrentLineSpeed = 0;
		handledMessage = true;
	}
	return handledMessage;
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
 * cbHandleRequestTurn | cbHandleRequestSetSpeed | cbHandleRequestSetLED
 */
bool subs_movement_cbHandleRequestMove(
	IN const com_SMessage_t* _lppodMessage
	) {
	bool handledMessage = false;

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_MOVE) {
		podCurrentMessage = COM_MESSAGE_TYPE__REQUEST_MOVE;		
		handledMessage = true;
	}
	return handledMessage;
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
bool subs_movement_cbHandleRequestSetSpeed(
	IN com_SMessage_t* const _lppodMessage
	) {
	bool handledMessage = false;

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_SET_SPEED) {
		handledMessage = true;
	}
	return handledMessage;
}

// /*!
//  * \brief
//  * Handles setLED-requests.
//  * 
//  * \param _podMessage
//  * Specifies the message which has to be analyzed.
//  * 
//  * \returns
//  * True, if a message has been handled by this function, false otherwise.
//  *
//  * Computes the type of the message by analyzing the first and second Byte of the message.
//  * If this handler is responsible the movement is performed.
//  * 
//  * \remarks
//  * Handler-functions have to be registered during the reset function.
//  *
//  * \see
//  * cbHandleRequestMove | cbHandleRequestTurn | cbHandleRequestSetSpeed
//  */
// bool cbHandleRequestSetLED( com_SMessage_t* _lppodMessage) {
// 	bool handledMessage = false;
// 
// 	if( _lppodMessage.eType == COM_MESSAGE_TYPE__REQUEST_SET_LED) {
// 
// 	}
// 	return handledMessage;
// }


/*!
 * \brief
 * Resets all movement data.
 * 
 * Registers all handler of this subsumption-layer for the Chain-of-Responsibility pattern.
 */
void subs_movement_reset( void) {
	com_register( subs_movement_cbHandleRequestTurn);
	com_register( subs_movement_cbHandleRequestMove);
	com_register( subs_movement_cbHandleRequestSetSpeed);
	//com_register(cbHandleRequestSetLED);
}
