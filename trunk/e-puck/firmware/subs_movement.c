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
 * Checks if the BlueTooth-message-queue contains a message for moving or turning.
 * If there is one the robot starts to perform the demanded movement and sends an acknowledgment to the smartphone and deletes this message.
 */
bool subs_movement_run( void) {
	bool movementChanged = false;

	// Check if a movement-change message exists



	return movementChanged;
}

/*!
 * \brief
 * Resets all movement data.
 * 
 * Sets the speed of the right and left motor to zero.
 */
void subs_movement_reset( void) {

}
