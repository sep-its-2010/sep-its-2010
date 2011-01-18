#include <string.h>

#include "hal_motors.h"
#include "conquest.h"

#include "subs_collision.h"


/*!
 * \brief
 * Specifies the states of the collision FSM.
 * 
 * \see
 * subs_collision_run | s_eState
 */
typedef enum {
	STATE__DETECTING, ///< Collision scan active.
	STATE__TURNING, ///< Turn by 180 degree after collision detected.
	STATE__RETURNING ///< Return to last node.
} EState_t;


/*!
 * \brief
 * Holds the current collision detection & avoidance state.
 * 
 * \see
 * subs_collision_reset
 */
static EState_t s_eState = STATE__DETECTING;


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
 * Subsumption layer for collision detection and prevention.
 * 
 * \returns
 * - \c true: collision detected or prevention active
 * - \c false: layer inactive
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FORWARD and remains active until it has finished.
 *
 * In case a collision is detected on on the two front IR proximity sensors (#CONQUEST_COLLISION_THRESHOLD) the collision mask is saved.
 * Next, the e-puck turns by 180 degree and returns to the last node. Line following and node detection is activated (#CONQUEST_STATE__RETURN_NODE)
 * if the e-puck is far enough from the last node. Otherwise, it just returns based on the step counters, updates the node type
 * and enters #CONQUEST_STATE__COLLISION.
 *
 * \warning
 * The motors abstraction layer needs to be initialized (#hal_motors_init()).
 *
 * \see
 * subs_collision_run | subs_collision_getResponse
 */
bool subs_collision_run( void) {

	static uint16_t s_ui16StepsSinceLastNode = 0;

	bool blActed = false;

	switch( s_eState) {
		case STATE__DETECTING: {
			if( conquest_getState() == CONQUEST_STATE__MOVE_FORWARD) {		

				// Check for front left or front right IR collision detected
				const bool* const lpblCollision = conquest_getSensorImage()->ablCollisionMask;
				if( lpblCollision[0] || lpblCollision[7]) {

					// Fill collision message
					subs_collision_podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION;
					memcpy( subs_collision_podResponse.aui8Data, lpblCollision, SEN_PROX_NUM_SENSORS);
					memset( &subs_collision_podResponse.aui8Data[SEN_PROX_NUM_SENSORS], 0xFF, sizeof( subs_collision_podResponse.aui8Data) - SEN_PROX_NUM_SENSORS);

					s_ui16StepsSinceLastNode = ( hal_motors_getStepsLeft() + hal_motors_getStepsRight()) / 2;
					hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
					hal_motors_setSteps( 0);
					s_eState = STATE__TURNING;
					blActed = true;
				}
			}
			break;
		}
		case STATE__TURNING: {
			if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 2 &&
				hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 2) {

				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				hal_motors_setSteps( 0);
				s_eState = STATE__RETURNING;
			}
			blActed = true;
			break;
		}
		case STATE__RETURNING: {

			// Return without line following?
			if( s_ui16StepsSinceLastNode < SUBS_COLLISION_MOVE_BACKWARD_THRESHOLD) {

				// Reached last node?
				if( ( hal_motors_getStepsLeft() + hal_motors_getStepsRight()) / 2 >= s_ui16StepsSinceLastNode) {
					hal_motors_setSpeed( 0, 0);

					// Turn last node by 180 degree
					const uint16_t ui16DirMask = ( conquest_getLastNode() & 0xFF) << 4;
					conquest_setLastNode( conquest_convertDirMaskToNode( ui16DirMask | ( ui16DirMask >> 8)));

					conquest_setState( CONQUEST_STATE__COLLISION);
				}

				// Block higher level subsumption layers
				blActed = true;
			} else {
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				conquest_setState( CONQUEST_STATE__RETURN_NODE);
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
 * \remarks
 * This function must be called to prepare a new collision detection & prevention operation.
 * 
 * \see
 * subs_collision_run
 */
void subs_collision_reset( void) {

	s_eState = STATE__DETECTING;
}
