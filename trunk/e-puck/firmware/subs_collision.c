#include <string.h>

#include "com.h"
#include "hal_motors.h"
#include "sen_prox.h"
#include "sen_line.h"
#include "subs_node.h"
#include "conquest_types.h"

#include "subs_collision.h"

static com_SMessage_t s_podCollisionResponse = {COM_MESSAGE_TYPE__RESPONSE_COLLISION, {0}};
static bool s_blPreventionActive = false;
static uint8_t s_ui8DetectionCounter = 0;

/*!
 * \brief
 * Detects collisions between e-puck and obstacles in the near future.
 * 
 * \returns
 * True if a collision has been detected, false otherwise.
 *
 * Reads the values of the eight proximity sensors, which are located around the e-puck.
 * If any of these values is above a critical value a collision is detected and the motors are stopped.
 * After that a message, which contains the direction of the obstacle is created and sent to the Smartphone via bluetooth.
 */
bool subs_collision_run( void) {

	bool blActed = false;
	sen_prox_SData_t podProxSensorData;
	sen_line_SData_t podLineSensorData;
	sen_prox_getCurrent( &podProxSensorData);

	// Check all IR-sensors for collision
	for( uint8_t i = 0; i < sizeof(podProxSensorData.aui8Data); i++) {
		if( podProxSensorData.aui8Data[i] > SUBS_COLLISION__PROX_THRESHOLD) {
			hal_motors_setSteps( 0);
			hal_motors_setSpeed( 0, 0);
			s_podCollisionResponse.aui8Data[i] = true;
			blActed = true;
		}
	}

	// Prevention not active? -> enable prevention and start turning.
	if( !s_blPreventionActive && blActed) {		

		// Decide into which direction the e-puck should turn.
// 		if( (subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__TOP_LEFT_EDGE) ||
// 			(subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__LEFT_T) ||
// 			(subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__CROSS)) {
// 			hal_motors_setSpeed( 0, SUBS_COLLISION__TURNING_SPEED);
// 		} else if( (subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__TOP_RIGHT_EDGE) ||
// 			(subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__RIGHT_T) ||
// 			(subs_node_sui8CurrentNodeType == CONQUEST_NODE_TYPE__TOP_T)) {
// 			hal_motors_setSpeed( 0, -SUBS_COLLISION__TURNING_SPEED);
// 		}
		s_blPreventionActive = true;
	
	// Exit collision-prevention and reset state
	} else if( s_blPreventionActive) {

		// If the robot turned for nearly 180° -> scan the ground for the next line.
		if( (hal_motors_getStepsLeft() >= SUBS_COLLISION__TURN_AROUND) &&
			(hal_motors_getStepsRight() >= SUBS_COLLISION__TURN_AROUND)) {
			sen_line_read( &podLineSensorData);
			sen_line_filter( &podLineSensorData, &podLineSensorData);
			
			if( (podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_COLLISION__LINE_THRESHOLD) || // @TODO Kalibrierwerte einsetzen
				(podLineSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_COLLISION__LINE_THRESHOLD) ||
				(podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_COLLISION__LINE_THRESHOLD)) {
				s_ui8DetectionCounter++;
			} else {
				s_ui8DetectionCounter = 0;
			}

			// Several line-detections in a row, left and/or right ground-sensor detects a white surface? -> Move in straight direction and reset collision-prevention-state.
			if( (s_ui8DetectionCounter >= SUBS_COLLISION__REQUIRED_LINE_MEASUREMENTS) &&
				((podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] > SUBS_COLLSION__SURFACE_THRESHOLD) ||
				(podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] > SUBS_COLLSION__SURFACE_THRESHOLD))) {
				s_ui8DetectionCounter = 0;
				s_blPreventionActive = false;
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( 0, 600); // @TODO Werte für speed (irgendwo global hinterlegt)
			}
			blActed = true;
		}		
	}
	return blActed;
}

/*!
 * \brief
 * Reset all collision-data to default values.
 * 
 * Deletes all values of the IR-sensors concerning the last collision.
 */
void subs_collision_reset( void) {

	s_blPreventionActive = false;
	memset( &s_podCollisionResponse, 0, sizeof( s_podCollisionResponse));
	s_ui8DetectionCounter = 0;
}
