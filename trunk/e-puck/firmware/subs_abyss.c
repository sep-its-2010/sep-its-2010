#include <string.h>

#include "hal_motors.h"
#include "sen_line.h"
#include "com.h"

#include "subs_abyss.h"


static bool s_blPreventionActive = false;


static com_SMessage_t s_podAbyssResponse;


/*!
 * \brief
 * Stops the robot when an abyss is detected in front of it.
 * 
 * \returns
 * Whether or not an abyss detected or prevention occurred.
 * 
 * Reads the values of the ground-sensors and stops the motors if the data is below a critical value.
 * Afterwards a message is created and sent to the smart phone via bluetooth.
 */
bool subs_abyss_run( void) {

	bool blActed = false;

	sen_line_SData_t podLineSensors;
	sen_line_read( &podLineSensors);
	sen_line_filter( &podLineSensors, &podLineSensors);

	// Abyss detected?
	if( ( podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD) ||
		( podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD) ||
		( podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD)) {

		// Prevention not active? -> enable it and record line sensor data
		if( !s_blPreventionActive) {
			s_podAbyssResponse.eType = COM_MESSAGE_TYPE__RESPONSE_ABYSS;
			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD) {
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__LEFT] = true;
			}
			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD) {
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__MIDDLE] = true;
			}
			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD) {
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__RIGHT] = true;
			}

			hal_motors_setSteps( 0);
			hal_motors_setSpeed( -1000, 0);
			s_blPreventionActive = true;
		}

		blActed = true;

	// Exit prevention when still active & driven far enough; send previously recorded line sensor data
	} else if( s_blPreventionActive &&
		hal_motors_getStepsLeft() >= SUBS_ABYSS_REGRESSION && 
		hal_motors_getStepsRight() >= SUBS_ABYSS_REGRESSION) {

		hal_motors_setSpeed( 0, 0);
		com_send( &s_podAbyssResponse);
		s_blPreventionActive = false;

		blActed = true;
	}

	return blActed;
}

void subs_abyss_reset( void) {

	s_blPreventionActive = false;
	memset( &s_podAbyssResponse, 0, sizeof( s_podAbyssResponse));
}
