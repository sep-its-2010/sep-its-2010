#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_calibration.h"

//#define _EEDATA(N) __attribute__((section(".eedata,r"),aligned(N)))

bool subs_calibration_isNotCalibrated = true;			///< The robot is not calibrated by default.
uint16_t _EEDATA(2) aui16LineCalibrationValues[3];		///< Stores calibration-values for line-detection
uint16_t _EEDATA(2) aui16SurfaceCalibrationValues[3];	///< Stores calibration-values for line-detection

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
		sen_line_SData_t podLineValueBuffer;
		sen_line_SData_t podSurfaceValueBuffer;
		sen_line_SData_t* _lppodLineValues = &podLineValueBuffer;
		sen_line_SData_t* _lppodSurfaceValues = &podSurfaceValueBuffer;
		sen_line_read( _lppodLineValues);
		hal_motors_setSpeed( 800, 800);
		sen_line_read( _lppodSurfaceValues);

		// copy values from buffer to EEPROM
		for( uint8_t i = 0; sizeof( podLineValueBuffer.aui16Data); i++) {
			aui16LineCalibrationValues[i] = podLineValueBuffer.aui16Data[i];
		}

		for( uint8_t i = 0; sizeof( podSurfaceValueBuffer.aui16Data); i++) {
			aui16SurfaceCalibrationValues[i] = podSurfaceValueBuffer.aui16Data[i];
		}
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
