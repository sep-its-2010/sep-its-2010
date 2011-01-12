#include "hal_motors.h"
#include "conquest.h"

#include "subs_movement.h"


static bool s_blActive = false;


/*!
 * \brief
 * Subsumption layer for basic move instructions.
 *
 * \returns
 * - \c true: adjusted motor speed
 * - \c false: no action taken
 *
 * The layer handles #CONQUEST_STATE__TURN_RIGHT, #CONQUEST_STATE__TURN_LEFT and #CONQUEST_STATE__MOVE_FORWARD states.
 * When a turn is finished the line centering (#CONQUEST_STATE__CENTER_LINE) state is entered if there is a line in front of the e-puck.
 * If there is no line #CONQUEST_STATE__STOP is entered.
 *
 * \remarks
 * The layer needs to be reset after #CONQUEST_STATE__MOVE_FORWARD is left before a new move forward instruction can be handled.
 *
 * \see
 * subs_movement_reset
 */

bool subs_movement_run( void) {

	bool blActed = false;

	const conquest_EState_t eState = conquest_getState();
	switch( eState) {
		case CONQUEST_STATE__TURN_RIGHT:
		case CONQUEST_STATE__TURN_LEFT: {
			if( s_blActive) {
				if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 4 &&
					hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 4) {

					hal_motors_setSpeed( 0, 0);

					// Turn current node
					uint16_t ui16RawDirections = conquest_getLastNode() & 0xFF;
					if( eState == CONQUEST_STATE__TURN_RIGHT) {
						ui16RawDirections <<= 6;
						ui16RawDirections |= ui16RawDirections >> 8;
					} else {
						ui16RawDirections <<= 2;
						ui16RawDirections |= ui16RawDirections >> 8;
					}

					conquest_setState( ui16RawDirections & CONQUEST_DIRECTION__UP ? CONQUEST_STATE__CENTER_LINE : CONQUEST_STATE__STOP);
					conquest_setLastNode( conquest_convertDirMaskToNode( ui16RawDirections & 0xFF));
					s_blActive = false;
				}
			} else {
				hal_motors_setSteps( 0);
				if( eState == CONQUEST_STATE__TURN_RIGHT) {
					hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
				} else {
					hal_motors_setSpeed( 0, -conquest_getRequestedLineSpeed());
				}
				s_blActive = true;
			}
			blActed = true;
			break;
		}
		case CONQUEST_STATE__MOVE_FORWARD: {
			if( !s_blActive) {
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				s_blActive = true;
				blActed = true;
			}
			break;
		}
		default: {

		}
	}

	return blActed;
}


/*!
 * \brief
 * Resets the subsumption movement layer.
 *
 * \see
 * subs_movement_run
 */

void subs_movement_reset( void) {

	s_blActive = false;
}
