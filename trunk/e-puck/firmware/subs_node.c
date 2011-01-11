#include "hal_motors.h"
#include "conquest.h"
#include "sen_line.h"
#include "hal_led.h"
#include "com.h"

#include "subs_node.h"


/*!
 * \brief
 * Stores the number of ground-sensor-measurements in a row, which provided data below a certain threshold.
 */
static uint8_t s_ui8NodeDetectionCounter = 0;

/*!
 * \brief
 * Indicates if a node has been detected and therefor the robot has to drive some steps to be with its center above the node.
 */
static bool s_blDetectionActive = false;

/*!
 * \brief
 * Stores values of the left ground-sensor, which are several times below the threshold for line-detection.
 */
static uint16_t s_ui16AvgLeft = 0;

/*!
 * \brief
 * Stores values of the right ground-sensor, which are several times below the threshold for line-detection.
 */
static uint16_t s_ui16AvgRight = 0;


/*!
 * \brief
 * Analyzes if the robot is above a node.
 * 
 * \returns
 * True if a node has been detected, false otherwise.
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FORWARD and remains active until it has finished.
 * 
 * Checks the ground-sensors for data. If the robot is currently moving and more than one sensor delivers several times
 * critical values the robot is supposed to be above a node and computes the nodes shape.
 * After that a message with the shape of the node is created and sent to the Smartphone via BlueTooth.
 * The robot will stop with its center above the node.
 */
 bool subs_node_run( void) {

	bool blActed = false;

	if( conquest_getState() == CONQUEST_STATE__MOVE_FORWARD) {

		// Node detection measurement
		const uint16_t* const lpui16SenLine = conquest_getSensorImage()->podCalibratedLineSensors.aui16Data;
		if( !s_blDetectionActive &&
			(lpui16SenLine[SEN_LINE_SENSOR__RIGHT] < SUBS_NODE_LINE_THRESHOLD ||
  			lpui16SenLine[SEN_LINE_SENSOR__LEFT] < SUBS_NODE_LINE_THRESHOLD) &&
			lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > SUBS_NODE_SURFACE_THRESHOLD) {

 			s_ui16AvgLeft += lpui16SenLine[SEN_LINE_SENSOR__LEFT];
 			s_ui16AvgRight += lpui16SenLine[SEN_LINE_SENSOR__RIGHT];
 			s_ui8NodeDetectionCounter++;

			hal_motors_setSteps( 0);
			hal_motors_setSpeed(conquest_getRequestedLineSpeed(), 0);
 			s_blDetectionActive = true;
			blActed = true;
 		}
	 	
 		//robot is above a node -> enable node-detection-state
 		if( s_blDetectionActive &&
 			hal_motors_getStepsLeft() >= SUBS_NODE_MOVE_ABOVE_CENTER && 
 			hal_motors_getStepsRight() >= SUBS_NODE_MOVE_ABOVE_CENTER) {
	 		
 			s_ui16AvgLeft = s_ui16AvgLeft / s_ui8NodeDetectionCounter;
 			s_ui16AvgRight = s_ui16AvgRight / s_ui8NodeDetectionCounter;

			bool blFrontLineDetected = ( lpui16SenLine[SEN_LINE_SENSOR__LEFT] < SUBS_NODE_LINE_THRESHOLD) ||
				( lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] < SUBS_NODE_LINE_THRESHOLD) ||
				( lpui16SenLine[SEN_LINE_SENSOR__RIGHT] < SUBS_NODE_LINE_THRESHOLD);
	 
 			//analyze the shape of the node		
 			if( ( s_ui16AvgLeft < SUBS_NODE_LINE_THRESHOLD_PLUS) &&
 				( s_ui16AvgRight < SUBS_NODE_LINE_THRESHOLD_PLUS) &&
 				blFrontLineDetected) {
	 
 				conquest_setLastNode( CONQUEST_NODE__CROSS);
 				hal_led_set( HAL_LED_PIN_BV__0 | HAL_LED_PIN_BV__2 | HAL_LED_PIN_BV__4 | HAL_LED_PIN_BV__6);
 			} else if( ( s_ui16AvgLeft < SUBS_NODE_LINE_THRESHOLD_PLUS) &&
 				( s_ui16AvgRight < SUBS_NODE_LINE_THRESHOLD_PLUS)) {
	 
 				conquest_setLastNode( CONQUEST_NODE__UP_T);
 				hal_led_set( HAL_LED_PIN_BV__2 | HAL_LED_PIN_BV__4 | HAL_LED_PIN_BV__6);
 			} else if( ( s_ui16AvgLeft < SUBS_NODE_LINE_THRESHOLD_PLUS) &&
 				blFrontLineDetected) {
	 
 				conquest_setLastNode( CONQUEST_NODE__RIGHT_T);
 				hal_led_set( HAL_LED_PIN_BV__0 | HAL_LED_PIN_BV__4 | HAL_LED_PIN_BV__6);
 			} else if( ( s_ui16AvgRight < SUBS_NODE_LINE_THRESHOLD_PLUS) &&
 				blFrontLineDetected) {
	 
 				conquest_setLastNode( CONQUEST_NODE__LEFT_T);
 				hal_led_set( HAL_LED_PIN_BV__0 | HAL_LED_PIN_BV__2 | HAL_LED_PIN_BV__4);
 			} else if( s_ui16AvgLeft < SUBS_NODE_LINE_THRESHOLD_PLUS) {
 				conquest_setLastNode( CONQUEST_NODE__DOWN_LEFT);
 				hal_led_set( HAL_LED_PIN_BV__4 | HAL_LED_PIN_BV__6);
 			} else if( s_ui16AvgRight < SUBS_NODE_LINE_THRESHOLD_PLUS) {
 				conquest_setLastNode( CONQUEST_NODE__DOWN_RIGHT);
 				hal_led_set( HAL_LED_PIN_BV__2 | HAL_LED_PIN_BV__4);
 			}
	 		
 			hal_motors_setSpeed( 0, 0);
 			hal_motors_setSteps( 0);
 			s_ui16AvgLeft = 0;
 			s_ui16AvgRight = 0;
 			s_ui8NodeDetectionCounter = 0;
			
			conquest_setState( CONQUEST_STATE__HIT_NODE);

 			s_blDetectionActive = false;
			blActed = true;
		
		} else if( s_blDetectionActive) {
			
			if ( lpui16SenLine[SEN_LINE_SENSOR__RIGHT] < SUBS_NODE_LINE_THRESHOLD_PLUS ||
				lpui16SenLine[SEN_LINE_SENSOR__LEFT] < SUBS_NODE_LINE_THRESHOLD_PLUS) {

				s_ui16AvgLeft += lpui16SenLine[SEN_LINE_SENSOR__LEFT];
				s_ui16AvgRight += lpui16SenLine[SEN_LINE_SENSOR__RIGHT];
				s_ui8NodeDetectionCounter++;
			}

			if (lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > SUBS_NODE_SURFACE_THRESHOLD) {
				blActed = true;
			}

		} 
	}
 
 	return blActed;
 }

/*!
 * \brief
 * Resets all values which are used to detect and analyze a node.
 * 
 * Clears the node-detection-counter and the last ground-sensor-measurement.
 */
void subs_node_reset( void) {

	s_blDetectionActive = false;
	s_ui8NodeDetectionCounter = 0;
	s_ui16AvgLeft = 0;
	s_ui16AvgRight = 0;
}

