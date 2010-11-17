#include "hal_led.h"
#include "hal_motors.h"
//#include "sen_line.h"

#include "subs_line.h"

/*!
 * \brief
 * Keeps the robot on the line.
 * 
 * \returns
 * True, as this is the last subsumption-behavior and will always be performed.
 * 
 * Collects data from the ground-sensors and uses these values to compute slight movement changes, which shall keep the robot above the line he is currently following.
 * Therefor a PID-controlling-algorithm is deployed.
 */
bool subs_line_run( void) {
	
	return false;
}

/*!
 * \brief
 * Resets  all values of the PID-controller.
 * 
 * Sets all parameter, which are used to compute the movement-adjustments of the robot back to default values.
 */
void subs_line_reset( void) {
}