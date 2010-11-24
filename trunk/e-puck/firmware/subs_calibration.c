#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_calibration.h"

bool subs_calibration_isNotCalibrated = true;		///< The robot is not calibrated by default.
//uint16_t _EEDATA(2) calibrationValues[2][3] = {0};		///< 2-dimensional array in the EEPROM, which saves 3 line-calibration-values in dimension 1
														///< and 3 surface-calibration-values in dimension 2 

/*!
 * \brief
 * Collects calibration values of the ground-sensors.
 *
 * \returns
 * True if the calibration has not been performed yet, false otherwise.
 * 
 * If the e-puck is correctly set up above a black line and its selector is at position '0' sensor data are collected.
 * After driving a few centimeters the robot is supposed to be above a white pane and collects again sensor data for the calibration.
 * These values are stored in the EEPROM.
 *
 * \see
 * subs_calibrate_reset
 */
bool subs_calibration_run( void) {
	uint8_t selector = hal_sel_getPosition();

	if( subs_calibration_isNotCalibrated && (selector == 0)) {

		//perform calibration
//		sen_line_read( calibrationValues);
		hal_motors_setSpeed( 500, 500);
//		sen_line_read( calibrationValues);

		subs_calibration_isNotCalibrated = false;
	}
	return subs_calibration_isNotCalibrated;
}

/*!
 * \brief
 * Resets the calibration-data of the robot.
 *
 * Deletes all former calibration-data from the EEPROM.
 */
void subs_calibration_reset( void) {
		subs_calibration_isNotCalibrated = true;
}
