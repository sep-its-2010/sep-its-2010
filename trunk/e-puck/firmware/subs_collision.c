#include <string.h>

#include "hal_motors.h"
#include "conquest.h"

#include "subs_collision.h"

typedef enum {
	STATE_DETECTING,
	STATE_TURNING,
	STATE_RETURNING
} EState_t;

static EState_t s_eState = STATE_DETECTING;
static uint16_t s_ui16StepsAfterNodeDetection = 0;


/*!
 * \brief
 * Holds the current collision response message.
 * 
 * \see
 * subs_collision_getResponse
 */
com_SMessage_t subs_collision_podResponse;


/*!
 * \brief
 * Detects collisions between e-puck and obstacles in the near future.
 * 
 * \returns
 * True if a collision has been detected, false otherwise.
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FOWARD.
 *
 * Reads the values of the proximity sensors, which are located around the e-puck.
 * If any of these values is above a critical value a collision is detected and the motors are stopped.
 * After that a message is created and delivered to the Smartphone via bluetooth.
 */
bool subs_collision_run( void) {

	bool blActed = false;

	switch (s_eState) {
		case STATE_DETECTING: {
			if( conquest_getState() == CONQUEST_STATE__MOVE_FORWARD) {		

				// Check front IR-sensors for collision
				const bool* const lpblCollision = conquest_getSensorImage()->ablCollisionMask;

				if( lpblCollision[0] || lpblCollision[7]) {
					blActed = true;
					
					// Is it possible to detect a node after turning by 180°?			
					if( hal_motors_getStepsLeft() < SUBS_COLLISION_MOVE_BACKWARD_THRESHOLD &&
						hal_motors_getStepsRight() < SUBS_COLLISION_MOVE_BACKWARD_THRESHOLD) {

						s_ui16StepsAfterNodeDetection = (hal_motors_getStepsLeft() + hal_motors_getStepsRight()) / 2;
					}

					// fill collision-message
					subs_collision_podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION;
					memcpy( subs_collision_podResponse.aui8Data, lpblCollision, SEN_PROX_NUM_SENSORS);
					memset( &subs_collision_podResponse.aui8Data[SEN_PROX_NUM_SENSORS], 0xFF, sizeof( subs_collision_podResponse.aui8Data) - SEN_PROX_NUM_SENSORS);
					
					hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
					hal_motors_setSteps( 0);
					s_eState = STATE_TURNING;
				}
			}
			break;
		}
		case STATE_TURNING: {
			blActed = true;

			if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 2 &&
				hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 2) {

				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				hal_motors_setSteps( 0);
				s_eState = STATE_RETURNING;
			}
			break;
		}
		case STATE_RETURNING: {
			blActed = true;

			if( s_ui16StepsAfterNodeDetection > 0) {
				if( (hal_motors_getStepsLeft() + hal_motors_getStepsRight()) / 2 >= s_ui16StepsAfterNodeDetection) {
					hal_motors_setSpeed( 0, 0);
					hal_motors_setSteps( 0);
					conquest_setState( CONQUEST_STATE__COLLISION);
					s_eState = STATE_DETECTING;
					blActed = false;
				}
			} else {
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				hal_motors_setSteps( 0);
				conquest_setState( CONQUEST_STATE__MOVE_FORWARD);
				s_eState = STATE_DETECTING;
				blActed = false;
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
 * Resets the collision layer.
 * 
 * \see
 * subs_collision_run
 */
void subs_collision_reset( void) {

	s_eState = STATE_DETECTING;
	s_ui16StepsAfterNodeDetection = 0;	
}
