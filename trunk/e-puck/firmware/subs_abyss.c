#include "hal_motors.h"
#include "sen_line.h"
#include "com.h"

#include "subs_abyss.h"


typedef enum {
	STATE__NO_ABYSS = 0, ///< Specifies the normal operation state when no abyss is or was detected.
	STATE__ABYSS_PREVENTION, ///< Specifies the prevention state in which the e-puck drives backwards.
	STATE__ABYSS_DETECTED ///< Specifies the blocking state after an abyss was prevented.
} EState_t;


/*!
 * \brief
 * Holds the current state of the abyss prevention layer.
 * 
 * \remarks
 * #subs_abyss_reset() needs to be called to recover from the blocking state after an abyss was prevented.
 * 
 * \see
 * subs_abyss_run
 */
static EState_t s_eState = STATE__NO_ABYSS;


/*!
 * \brief
 * Holds the response message which identifies the line sensors causing the abyss prevention.
 * 
 * \see
 * subs_abyss_run
 */
static com_SMessage_t s_podAbyssResponse;


/*!
 * \brief
 * Subsumption layer for abyss detection and prevention.
 * 
 * \returns
 * - \c true: abyss detected
 * - \c false: layer inactive
 * 
 * In case an abyss is detected on any calibrated line sensor (#SUBS_ABYSS_THRESHOLD) the e-puck reverts a specified amount of
 * steps (#SUBS_ABYSS_REGRESSION) but at least as long as the line sensors still detect an abyss. Afterwards this layer enters
 * a blocking state which will prevent any following subsumption layers from executing.
 * 
 * \remarks
 * This layer will not allow any further layers to execute until #subs_abyss_reset() is called after the abyss prevention state.
 *
 * \warning
 * - The line sensors need to be calibrated before (#sen_line_calibarte()).
 * - The motors abstraction layer needs to be initialized (#hal_motors_init()).
 * - The communication layer needs to be initialized (#com_init()).
 */
bool subs_abyss_run( void) {

	bool blActed = false;

	switch( s_eState) {
		case STATE__NO_ABYSS: {
			sen_line_SData_t podLineSensors;
			sen_line_read( &podLineSensors);
			sen_line_rescale( &podLineSensors, &podLineSensors);

			// Drive backwards when an abyss is detected on any line sensor
			if( podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD ||
				podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD ||
				podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD) {

				s_podAbyssResponse.eType = COM_MESSAGE_TYPE__RESPONSE_ABYSS;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__LEFT] = podLineSensors.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__MIDDLE] = podLineSensors.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD;
				s_podAbyssResponse.aui8Data[SEN_LINE_SENSOR__RIGHT] = podLineSensors.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD;

				hal_motors_setSteps( 0);
				hal_motors_setSpeed( -SUBS_ABYSS_REGRESSION_SPEED, 0);
				s_eState = STATE__ABYSS_PREVENTION;
				blActed = true;
			}
			break;
		}
		case STATE__ABYSS_PREVENTION: {
			sen_line_SData_t podLineSensors;
			sen_line_read( &podLineSensors);
			sen_line_rescale( &podLineSensors, &podLineSensors);

			// Stop motors when no abyss detected & the regression distance is at least SUBS_ABYSS_REGRESSION.
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


/*!
 * \brief
 * Resets the abyss layer.
 * 
 * \remarks
 * This function needs to be called to recover from the blocking state after the abyss prevention.
 * 
 * \see
 * subs_abyss_run
 */
void subs_abyss_reset( void) {

	s_eState = STATE__NO_ABYSS;
}
