#include "hal_motors.h"
#include "hal_led.h"
#include "com.h"

#include "subs_movement.h"

/*!
 * \brief
 * Executes movement-commands sent by the Smartphone.
 * 
 * \returns
 * True if a movement-command is going to be performed, false otherwise.
 *
 * Checks if the BlueTooth-message-queue contains a message for moving or turning. If there is one the robot starts to perform the demanded movement and sends
 * an acknowledgement to the Smartphone and deletes this message.
 *
 * \see
 * Separate items with the '|' character.
 */
bool subs_movement_execute( void) {

	return false;
}