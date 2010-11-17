#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
//#include "sen_line.h"

#include "subs_calibration.h"

/*!
 * \brief
 * Collects calibration values of the ground-sensors.
 *
 * \returns
 * True if the calibration has been performed, false otherwise.
 * 
 * If the e-puck is correctly set up above a black line and its selector is at position '0' sensordata are collected. After driving a few cm's the robot
 * is supposed to be above a white pane and collects again sensordata for the calibration. These values are stored in the EEPROM.
 *
 * \remarks
 * 
 * \see
 * subs_calibrate_reset
 */
bool subs_calibrate_run( void) {
	//TODO remarks in den Dokumentation von init und execute, Kalibrierung sollte nur 1mal bei Systemstart und nach Reset erfolgen

	return false;
}

/*!
 * \brief
 * Initializes the calibration of the robot.
 *
 * \remarks
 * Write remarks for subs_calibrate_init here.
 */
void subs_calibrate_reset( void) {
}
