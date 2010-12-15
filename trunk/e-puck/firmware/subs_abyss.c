#include <string.h>

#include "hal_motors.h"
#include "sen_line.h"
#include "com.h"

#include "subs_abyss.h"


typedef enum {
	STATE__NO_ABYSS = 0,
	STATE__ABYSS_PREVENTION,
	STATE__ABYSS_DETECTED
} EState_t;


static EState_t s_eState = STATE__NO_ABYSS;


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

	switch( s_eState) {
		case STATE__NO_ABYSS: {
			sen_line_SData_t podLineSensors;
			sen_line_read( &podLineSensors);
			sen_line_rescale( &podLineSensors, &podLineSensors);

			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD ||
				podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD ||
				podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD) {

				s_podAbyssResponse.eType = COM_MESSAGE_TYPE__RESPONSE_ABYSS;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__LEFT] = podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__MIDDLE] = podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__RIGHT] = podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD;

				hal_motors_setSteps( 0);
				hal_motors_setSpeed( -1000, 0);
				s_eState = STATE__ABYSS_PREVENTION;
				blActed = true;
			}
			break;
		}
		case STATE__ABYSS_PREVENTION: {
			sen_line_SData_t podLineSensors;
			sen_line_read( &podLineSensors);
			sen_line_rescale( &podLineSensors, &podLineSensors);

			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] >= SUBS_ABYSS_THRESHOLD &&
				podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] >= SUBS_ABYSS_THRESHOLD &&
				podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] >= SUBS_ABYSS_THRESHOLD &&
				hal_motors_getStepsLeft() >= SUBS_ABYSS_REGRESSION && 
				hal_motors_getStepsRight() >= SUBS_ABYSS_REGRESSION) {

				hal_motors_setSpeed( 0, 0);
				com_send( &s_podAbyssResponse);

				s_eState = STATE__ABYSS_DETECTED;
			}
			blActed = true;
			break;
		}
		case STATE__ABYSS_DETECTED: {
			hal_motors_setSpeed( 0, 0);
			blActed = true;
			break;
		}
		default: {

		}
	}

	return blActed;
}

void subs_abyss_reset( void) {

	s_eState = STATE__NO_ABYSS;
	memset( &s_podAbyssResponse, 0, sizeof( s_podAbyssResponse));
}
