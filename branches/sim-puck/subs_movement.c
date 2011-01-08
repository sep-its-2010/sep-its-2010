#include "hal_motors.h"
#include "conquest.h"

#include "subs_movement.h"


static bool s_blActive = false;


/*!
 * \brief
 * Executes movement-commands sent by the smartphone.
 * 
 * \returns
 * True if a movement-command is going to be performed, false otherwise.
 *
 * Checks if the bluetooth-message-queue contains a message for moving or turning.
 * If there is one the robot starts to perform the demanded movement and sends an acknowledgment to the smartphone and deletes this message.
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
					conquest_setState( CONQUEST_STATE__STOP);

					// Turn current node
					uint16_t ui16RawDirections = conquest_getLastNode() & 0xFF;
					if( eState == CONQUEST_STATE__TURN_RIGHT) {
						ui16RawDirections <<= 6;
						ui16RawDirections |= ui16RawDirections >> 8;
					} else {
						ui16RawDirections <<= 2;
						ui16RawDirections |= ui16RawDirections >> 8;
					}

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
		case CONQUEST_STATE__MOVE_FOWARD: {
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
 * Resets all movement data.
 * 
 * Registers all handler of this subsumption-layer for the Chain-of-Responsibility pattern.
 */
void subs_movement_reset( void) {

	s_blActive = false;
}
