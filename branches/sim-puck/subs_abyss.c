#include <string.h>

#include "hal_motors.h"
#include "conquest.h"

#include "subs_abyss.h"


/*!
 * \brief
 * Specifies the subsumption abyss layer FSM states.
 * 
 * \see
 * s_eState | subs_abyss_run
 */
typedef enum {
	STATE__ABYSS_SCAN, ///< Specifies the normal operation state when no abyss is or was detected.
	STATE__ABYSS_PREVENTION ///< Specifies the prevention state in which the e-puck drives backwards.
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
static EState_t s_eState = STATE__ABYSS_SCAN;


/*!
 * \brief
 * Holds the current abyss response message.
 * 
 * \see
 * subs_collision_getResponse
 */
com_SMessage_t subs_abyss_podResponse;


/*!
 * \brief
 * Subsumption layer for abyss detection and prevention.
 * 
 * \returns
 * - \c true: abyss detected or prevention active
 * - \c false: layer inactive
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FOWARD and remains active until it has finished.
 *
 * In case an abyss is detected on any calibrated line sensor (#SUBS_ABYSS_THRESHOLD) the abyss mask is saved and the e-puck
 * reverts a specified amount of steps (#SUBS_ABYSS_REGRESSION) but at least as long as the line sensors still detect an abyss.
 * Afterwards this layer enters a blocking state (#CONQUEST_STATE__ABYSS).
 * 
 * \remarks
 * This layer will not allow any further layers to execute until #subs_abyss_reset() is called after the abyss prevention state.
 *
 * \warning
 * - The line sensors need to be calibrated before (#sen_line_calibarte()).
 * - The motors abstraction layer needs to be initialized (#hal_motors_init()).
 * - The communication layer needs to be initialized (#com_init()).
 *
 * \see
 * subs_abyss_reset | subs_abyss_getResponse
 */
bool subs_abyss_run( void) {

	bool blActed = false;

	switch( s_eState) {
		case STATE__ABYSS_SCAN: {
			if( conquest_getState() == CONQUEST_STATE__MOVE_FOWARD) {

				// Drive backwards when an abyss is detected on any line sensor
				const bool* const lpblAbyss = conquest_getSensorImage()->ablAbyssMask;
				if( lpblAbyss[SEN_LINE_SENSOR__LEFT] || lpblAbyss[SEN_LINE_SENSOR__MIDDLE] || lpblAbyss[SEN_LINE_SENSOR__RIGHT]) {
					hal_motors_setSteps( 0);
					hal_motors_setSpeed( -conquest_getRequestedLineSpeed(), 0);
					subs_abyss_podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_ABYSS;
					memcpy( subs_abyss_podResponse.aui8Data, lpblAbyss, SEN_LINE_NUM_SENSORS);
					memset( &subs_abyss_podResponse.aui8Data[SEN_LINE_NUM_SENSORS], 0xFF, sizeof( subs_abyss_podResponse.aui8Data) - SEN_LINE_NUM_SENSORS);
					s_eState = STATE__ABYSS_PREVENTION;
					blActed = true;
				}
			}
			break;
		}
		case STATE__ABYSS_PREVENTION: {

			// Stop motors when no abyss detected & the regression distance is at least SUBS_ABYSS_REGRESSION.
			const bool* const lpblAbyss = conquest_getSensorImage()->ablAbyssMask;
			if( !lpblAbyss[SEN_LINE_SENSOR__LEFT] && 
				!lpblAbyss[SEN_LINE_SENSOR__MIDDLE] && 
				!lpblAbyss[SEN_LINE_SENSOR__RIGHT] &&
				hal_motors_getStepsLeft() >= SUBS_ABYSS_REGRESSION && 
				hal_motors_getStepsRight() >= SUBS_ABYSS_REGRESSION) {

				hal_motors_setSpeed( 0, 0);

				s_eState = STATE__ABYSS_SCAN;
				conquest_setState( CONQUEST_STATE__ABYSS);
			}
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
 * \see
 * subs_abyss_run
 */
void subs_abyss_reset( void) {

	s_eState = STATE__ABYSS_SCAN;
}
