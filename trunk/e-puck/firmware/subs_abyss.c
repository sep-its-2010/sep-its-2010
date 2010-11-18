#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
//#include "sen_line.h"

#include "subs_abyss.h"

/*!
 * \brief
 * Decides if there is an abyss in front of the robot.
 * 
 * \returns
 * True if an abyss has been detected, false otherwise.
 * 
 * Reads the values of the ground-sensors and stops the motors if the data is below a critical value.
 * Afterwards a message is created and sent to the Smartphone via BlueTooth.
 */
bool subs_abyss_run( void) {

	return false;
}

/*!
 * \brief
 * Resets the abyss-detected-state.
 * 
 * Disables all LEDs, which are used to visualize the different states of the robot behavior.
 * 
 * \see
 * subs_abyss_visualize
 */
void subs_abyss_reset( void) {
}

/*!
 * \brief
 * Visualizes the abyss-detected-state.
 * 
 * Enables the four LEDs, which are located on the front-half of the e-puck. This indicates that an abyss has been detected.
 *
 * \see
 * subs_abyss_reset
 */
void subs_abyss_visualize( void) {
}
