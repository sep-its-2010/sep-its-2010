#include <string.h>

#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_prox.h"
#include "sen_line.h"
#include "subs_node.h"

#include "subs_collision.h"

enum {
	SUBS_COLLISION_THRESHOLD = 64 ///< Specifies the threshold-value above which a collision will be detected.
};

//static com_SMessage_t s_podCollisionMessage = {COM_MESSAGE_TYPE__RESPONSE_COLLISION, {0}};
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
	bool blCollisionDetected = false;
	sen_prox_SData_t podProxSensorData;
	sen_line_SData_t podLineSensorData;
	sen_prox_getCurrent( &podProxSensorData);

	// Check all IR-sensors for collision
	for( uint8_t i = 0; i < sizeof(podProxSensorData.aui8Data); i++) {
		if( podProxSensorData.aui8Data[i] > SUBS_COLLISION_THRESHOLD) {				
			//s_podCollisionMessage.aui8Data[i] = true;
			blCollisionDetected = true;
		}
	}

	// Prevention not active? -> enable prevention and start turning.
	if( !s_blPreventionActive && blCollisionDetected) {
		hal_motors_setSteps( 0);

		// Decide into which direction the e-puck should turn.
		if( (subs_node_sui8CurrentNodeType == NODE_TYPE__TOP_LEFT_EDGE) ||
			(subs_node_sui8CurrentNodeType == NODE_TYPE__LEFT_T) ||
			(subs_node_sui8CurrentNodeType == NODE_TYPE__CROSS)) {
			hal_motors_setSpeed( 0, 250);
		} else if( (subs_node_sui8CurrentNodeType == NODE_TYPE__TOP_RIGHT_EDGE) ||
			(subs_node_sui8CurrentNodeType == NODE_TYPE__RIGHT_T) ||
			(subs_node_sui8CurrentNodeType == NODE_TYPE__TOP_T)) {
			hal_motors_setSpeed( 0, -250);
		}
		s_blPreventionActive = true;
	
	// Exit collision-prevention and reset state
	} else if( s_blPreventionActive) {
		blCollisionDetected = true;
		

		// If the robot turned for nearly 180° -> scan the ground for the next line.
		if( (hal_motors_getStepsRight() >= 240) &&
			(hal_motors_getStepsRight() >= 240)) {
			sen_line_read( &podLineSensorData);
			
			if( (2 * podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < 270) || // @TODO Kalibrierwerte einsetzen
				(2 * podLineSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < 270) ||
				(2 * podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < 270)) {
				s_ui8DetectionCounter++;
			} else {
				s_ui8DetectionCounter = 0;
			}

			// Three line-detections in a row, left and/or right ground-sensor detects a white surface? -> Move in straight direction and reset collision-prevention-state.
			if( (s_ui8DetectionCounter >= 3) &&
				((podLineSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] > 400) ||
				(podLineSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] > 400))) {
				s_ui8DetectionCounter = 0;
				s_blPreventionActive = false;
				hal_motors_setSteps( 0);
				hal_motors_setSpeed( 0, 600); // @TODO Werte für speed und untergrund-threshold (irgendwo global hinterlegt)
			}
		}		
	}
	return blCollisionDetected;
}

/*!
 * \brief
 * Reset all collision-data to default values.
 * 
 * Deletes all values of the IR-sensors concerning the last collision.
 */
void subs_collision_reset( void) {
	s_blPreventionActive = false;
	s_ui8DetectionCounter = 0;
	//s_podCollisionMessage = { 0, {0}};
	//memset( s_podCollisionMessage.aui8Data, 0, sizeof(s_podCollisionMessage.aui8Data));
}
