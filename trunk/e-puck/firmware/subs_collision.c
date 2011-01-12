#include <string.h>

#include "conquest.h"
#include "hal_motors.h"
#include "sen_line.h"
#include "sen_prox.h"

#include "subs_collision.h"


static bool s_blPreventionActive = false;
static uint8_t s_ui8DetectionCounter = 0;


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
 * Reads the values of the eight proximity sensors, which are located around the e-puck.
 * If any of these values is above a critical value a collision is detected and the motors are stopped.
 * After that a message, which contains the direction of the obstacle is created and sent to the Smartphone via bluetooth.
 */
bool subs_collision_run( void) {

	bool blActed = false;

	if( conquest_getState() == CONQUEST_STATE__MOVE_FORWARD) {
	
		// Check all IR-sensors for collision
		const bool* const lpblCollision = conquest_getSensorImage()->ablCollisionMask;
		
		for( uint16_t ui16 = 0; ui16 < SEN_PROX_NUM_SENSORS; ui16++) {
			if( lpblCollision[ui16]) {				
				blActed = true;
			}
		}

		if( blActed) {
			subs_collision_podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION;
			memcpy( subs_collision_podResponse.aui8Data, lpblCollision, SEN_PROX_NUM_SENSORS);
			memset( &subs_collision_podResponse.aui8Data[SEN_PROX_NUM_SENSORS], 0xFF, sizeof( subs_collision_podResponse.aui8Data) - SEN_PROX_NUM_SENSORS);
			hal_motors_setSpeed( 0, 0);
			hal_motors_setSteps( 0);
			conquest_setState( CONQUEST_STATE__COLLISION);
		}

		// Prevention not active? -> enable prevention and start turning.
		if( !s_blPreventionActive && blActed) {		

			// Decide into which direction the e-puck should turn.
			conquest_ENode_t eNode = conquest_getLastNode();
 			if( eNode == CONQUEST_NODE__DOWN_RIGHT ||
 				eNode == CONQUEST_NODE__LEFT_T ||
 				eNode == CONQUEST_NODE__CROSS) {

 				hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
 			} else if( eNode == CONQUEST_NODE__DOWN_LEFT ||
 				eNode == CONQUEST_NODE__RIGHT_T ||
 				eNode == CONQUEST_NODE__UP_T) {

 				hal_motors_setSpeed( 0, -conquest_getRequestedLineSpeed());
 			}
			s_blPreventionActive = true;
		
		// Exit collision-prevention and reset state
		} else if( s_blPreventionActive) {

			// If the robot turned for nearly 180° -> scan the ground for the next line.
			if( hal_motors_getStepsLeft() >= HAL_MOTORS_FULL_TURN_STEPS / 2 &&
				hal_motors_getStepsRight() >= HAL_MOTORS_FULL_TURN_STEPS / 2) {

				sen_line_SData_t podLineSensorData;
				sen_line_read( &podLineSensorData);
				sen_line_rescale( &podLineSensorData, &podLineSensorData);
				
				if( podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_COLLISION_LINE_THRESHOLD ||
					podLineSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_COLLISION_LINE_THRESHOLD ||
					podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_COLLISION_LINE_THRESHOLD) {

					s_ui8DetectionCounter++;
				} else {
					s_ui8DetectionCounter = 0;
				}

				// Several line-detections in a row, left and/or right ground-sensor detects a white surface?
				// -> Move in straight direction and reset collision-prevention-state.
				if( s_ui8DetectionCounter >= SUBS_COLLISION_LINE_MEASUREMENTS &&
					( podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] > SUBS_COLLISION_SURFACE_THRESHOLD ||
					podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] > SUBS_COLLISION_SURFACE_THRESHOLD)) {

					s_ui8DetectionCounter = 0;
					s_blPreventionActive = false;
					hal_motors_setSteps( 0);
					hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);
				}
				blActed = true;
			}		
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

	s_blPreventionActive = false;
	s_ui8DetectionCounter = 0;
}
