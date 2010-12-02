#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_line.h"
#include "subs_calibration.h"

#include "subs_abyss.h"

enum {
	ABYSS_DETECTION_THRESHOLD = 75 ///< Sensor-values below this threshold indicate an abyss.
};

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
	sen_line_SData_t podSensorData;
	sen_line_read( &podSensorData);

	// Check if one of the ground-sensors detects an abyss
	if( (podSensorData.aui16Data[0] < ABYSS_DETECTION_THRESHOLD) ||
		(podSensorData.aui16Data[1] < ABYSS_DETECTION_THRESHOLD) ||
		(podSensorData.aui16Data[2] < ABYSS_DETECTION_THRESHOLD)) {
		hal_motors_setSpeed( 0, 0);
		hal_motors_setSteps( 0);
		abyssDetected = true;
		
		// inform smartphone about abyss
		com_SMessage_t podAbyssResponse = {COM_MESSAGE_TYPE__RESPONSE_ABYSS, {0}};
		
		if( podSensorData.aui16Data[0] < ABYSS_DETECTION_THRESHOLD) {
			podAbyssResponse.aui8Data[0] = true;
		}
		if( podSensorData.aui16Data[1] < ABYSS_DETECTION_THRESHOLD) {
			podAbyssResponse.aui8Data[1] = true;
		}
		if( podSensorData.aui16Data[2] < ABYSS_DETECTION_THRESHOLD) {
			podAbyssResponse.aui8Data[2] = true;
		}
		com_send( &podAbyssResponse);
	}
	return abyssDetected;
}
