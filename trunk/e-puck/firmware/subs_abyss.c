#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_abyss.h"

/*!
 * \brief
 * Decides if there is an abyss in front of the robot.
 * 
 * \returns
 * True if an abyss has been detected, false otherwise.
 * 
 * Reads the values of the ground-sensors and stops the motors if the data is below a critical value.
 * Afterwards a message is created and sent to the Smartphone via Bluetooth.
 */
bool subs_abyss_run( void) {
	bool abyssDetected = false;
	

	return false;
}
