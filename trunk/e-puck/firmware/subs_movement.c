#include "hal_motors.h"
#include "hal_led.h"
#include "com.h"

#include "subs_movement.h"

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
bool subs_movement_run( void){
	bool movementChanged = false;

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
 * 
 * \remarks
 * This handler-function has to be registered during the reset function.
 *
 * \see
 * cbHandleRequestMove | cbHandleRequestSetSpeed
 */
bool cbHandleRequestTurn( com_SMessage_t _podMessage) {
	bool handledMessage = false;

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
 * 
 * \remarks
 * This handler-function has to be registered during the reset function.
 *
 * \see
 * cbHandleRequestTurn | cbHandleRequestSetSpeed
 */
bool cbHandleRequestMove( com_SMessage_t _podMessage) {
	bool handledMessage = false;

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
 * 
 * \remarks
 * This handler-function has to be registered during the reset function.
 *
 * \see
 * cbHandleRequestMove | cbHandleRequestTurn
 */
bool cbHandleRequestSetSpeed( com_SMessage_t _podMessage) {
	bool handledMessage = false;

	return handledMessage;
}

/*!
 * \brief
 * Resets all movement data.
 * 
 * Registers all handler of this subsumption-layer for the Chain-of-Responsibility pattern.
 */
void subs_movement_reset( void) {
	bool (*RequestTurn)( com_SMessage_t) = cbHandleRequestTurn;
	com_register(RequestTurn);

	bool (*RequestMove)( com_SMessage_t) = cbHandleRequestMove;
	com_register(RequestMove);

	bool (*RequestSetSpeed)( com_SMessage_t) = cbHandleRequestSetSpeed;
	com_register(RequestSetSpeed);
}
