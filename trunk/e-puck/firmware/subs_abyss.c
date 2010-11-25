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
	sen_line_SData_t podSensorData;
	sen_line_SData_t* _lppodSensorData = &podSensorData;
	sen_line_read( _lppodSensorData);

	if( podSensorData.aui16Data[1] < 75) {
		hal_motors_setSpeed( 0, 0);
		hal_motors_setSteps( 0);
		abyssDetected = true;
		
		// inform smartphone about abyss
		com_SMessage_t podAbyssResponse = {COM_MESSAGE_TYPE__RESPONSE_ABYSS, {0}};
		com_EMessageType_t* _lppodAbyssResponse = &podAbyssResponse;
		com_send( _lppodAbyssResponse);
	}

	return abyssDetected;
}
