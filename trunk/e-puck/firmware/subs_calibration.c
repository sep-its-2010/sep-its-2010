// #include <p30f6014A.h>
// 
// #include "hal_led.h"
// #include "hal_uart1.h"
#include "hal_sel.h"
#include "sen_line.h"

#include "subs_calibration.h"

static bool s_blCalibrationActive = false;
static bool s_blCalibrationRead = false;


/*!
 * \brief
 * Collects calibration values of the ground-sensors smartphone.
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

	bool blActed = false;

	if( !s_blCalibrationRead) {
		s_blCalibrationRead = true;
//		hal_led_set( 2);
	} else if( s_blCalibrationActive && hal_sel_getPosition()) {
		s_blCalibrationActive = false;
//		hal_led_set( 4);
	} else if( !s_blCalibrationActive && !hal_sel_getPosition()) {

		sen_line_SData_t podLineSensors;
		sen_line_read( &podLineSensors);
// 
// 		char buffer[50];
// 		sprintf( buffer, "raw left: %d, mid: %d, right: %d\r\n", podLineSensors.aui16Data[0], podLineSensors.aui16Data[1], podLineSensors.aui16Data[2]);
// 		hal_uart1_puts( buffer);
// 
		sen_line_calibrate( &podLineSensors);
//		sen_line_filter( &podLineSensors, &podLineSensors);
// 		sprintf( buffer, "cal left: %d, mid: %d, right: %d\r\n", podLineSensors.aui16Data[0], podLineSensors.aui16Data[1], podLineSensors.aui16Data[2]);
// 		hal_uart1_puts( buffer);

		s_blCalibrationActive = true;
		blActed = true;
//		hal_led_set( 1);
	}

	return blActed;
}

/*!
 * \brief
 * Resets the calibration-data of the robot.
 *
 * Deletes all former calibration-data from the EEPROM.
 */
void subs_calibration_reset( void) {

	s_blCalibrationActive = false;
	s_blCalibrationRead = false;
}
