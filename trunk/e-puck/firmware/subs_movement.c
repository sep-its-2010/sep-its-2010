#include "hal_motors.h"
#include "conquest.h"

#include "subs_movement.h"


static bool s_blActive = false;

static uint16_t s_ui16CenterSteps = 0;

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

	switch( conquest_getState()) {
		case CONQUEST_STATE__TURN_RIGHT: {
			if( s_blActive) {
				if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 4 &&
					hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 4) {

					hal_motors_setSpeed( 0, 0);
					conquest_setState( CONQUEST_STATE__STOP);
					s_blActive = false;
				}
			} else {
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
				s_blActive = true;
			}
			blActed = true;
			break;
		}
		case CONQUEST_STATE__TURN_LEFT: {
			if( s_blActive) {
				if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 4 &&
					hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 4) {

					hal_motors_setSpeed( 0, 0);
					conquest_setState( CONQUEST_STATE__STOP);
					s_blActive = false;
				}
			} else {
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( 0, -conquest_getRequestedLineSpeed());
				s_blActive = true;
			}
			blActed = true;
			break;
		}
		case CONQUEST_STATE__CENTER_AND_MOVE: {
			if( !s_ui16CenterSteps) {
				s_ui16CenterSteps++;
			} else if( s_ui16CenterSteps > 20) {
				s_ui16CenterSteps = 0;
				conquest_setState( CONQUEST_STATE__MOVE_FOWARD);
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
			} else {
				s_ui16CenterSteps++;
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
