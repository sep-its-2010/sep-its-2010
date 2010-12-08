#include <p30f6014A.h>
#include <string.h>

#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_calibration.h"

bool subs_calibration_isNotCalibrated = true; ///< The robot is not calibrated by default.
uint16_t aui16LineCalibrationValues[3] = {0, 0, 0}; ///< Stores calibration-values for line-detection.
uint16_t aui16SurfaceCalibrationValues[3] = {0, 0, 0}; ///< Stores calibration-values for line-detection.

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
	uint8_t ui8selector = hal_sel_getPosition();

	if( subs_calibration_isNotCalibrated && (ui8selector == 0)) {
		
		// If there are no calibration-values for the line: collect and store some.
		if( aui16LineCalibrationValues[0] == 0){ 
			sen_line_SData_t podLineValueBuffer;
			sen_line_read( &podLineValueBuffer);

			for( uint8_t i = 0; sizeof( podLineValueBuffer.aui16Data); i++) {
				aui16LineCalibrationValues[i] = podLineValueBuffer.aui16Data[i];
			}
		}		
		hal_motors_setSpeed( hal_motors_si16CurrentLineSpeed, hal_motors_si16CurrentAngularSpeed);

		// If there are no calibration-values for the surface: collect and store.
		if( (aui16SurfaceCalibrationValues[0] == 0) && (hal_motors_getStepsLeft() >= 500)) {
			sen_line_SData_t podSurfaceValueBuffer;
			sen_line_read( &podSurfaceValueBuffer);
			
			for( uint8_t i = 0; sizeof( podSurfaceValueBuffer.aui16Data); i++) {
				aui16SurfaceCalibrationValues[i] = podSurfaceValueBuffer.aui16Data[i];
			}
			subs_calibration_isNotCalibrated = false;
			hal_motors_setSpeed( 0, 0);
			hal_motors_setSteps( 0);
		}		
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
		memset( aui16LineCalibrationValues, 0, sizeof(aui16LineCalibrationValues));
		memset( aui16SurfaceCalibrationValues, 0, sizeof(aui16SurfaceCalibrationValues));
}

/*!
//  * \brief
//  * Write the given data into the EEPROM.
//  * 
//  * \param _ui16Value
//  * Specifies the data which will be stored in the EEPROM.
//  * 
//  * \param _aui16EEPROM
//  * Specifies the destination buffer.
//  * 
//  * Saves the given value in the EEPROM permanently. These data will not be lost after resets or power-offs.
//  */
// void writeToEEPROM( 
// 			IN const uint16_t _ui16Value,
// 			OUT uint16_t* const _lpui16EEPROM
// 			) {
// 	uint16_t* lpui16EEPROMWritePointer;
// 	// Sry Moadl i hab keine Ahnung x.x ...
// }
