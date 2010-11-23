#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
#include "sen_line.h"
#define _EEDATA(N) __attribute__((section(".eedata,r"),aligned(N)))

#include "subs_calibration.h"

bool isNotCalibrated;
int l_buffer[3];						// saves calibration-values temporary

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
	uint8_t selector = hal_sel_getPosition( void);

	if( isNotCalibrated && (selector == 0)) {
		char buffer[50];

		//perform calibration
			//sen_line_read etc
		
		// To declare an initialized array in data EEPROM without special alignment (C30 manual page 81)
		int _EEDATA(2) calibrationValues[];
		calibrationValues[0] = l_buffer[0];
		calibrationValues[1] = l_buffer[1];
		calibrationValues[2] = l_buffer[2];

		isNotCalibrated = false;
	}
	return isNotCalibrated;
}

/*!
 * \brief
 * Resets the calibration-data of the robot.
 *
 * Deletes all former calibration-data from the EEPROM.
 */
void subs_calibration_reset( void) {
		isNotCalibrated = true;

		for( int i = 0; i < sizeof(l_buffer); i++) {
			l_buffer[i] = 0;
		}
}
