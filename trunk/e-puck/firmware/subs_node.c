#include <stdio.h>

#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"
#include "conquest_types.h"
#include "hal_led.h"
#include "hal_uart1.h"

#include "subs_node.h"

/*!
 * Stores the type of the last visited node.
 * 
 * \remarks
 * The node-type is UNKNOWN by default and will be set after a node has been detected.
 */
volatile uint8_t subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__UNKNOWN;

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
 * Checks the ground-sensors for data. If the robot is currently moving and more than one sensor delivers several times
 * critical values the robot is supposed to be above a node and computes the nodes shape.
 * After that a message with the shape of the node is created and sent to the Smartphone via BlueTooth.
 * The robot will stop with its center above the node.
 */
bool subs_node_run( void) {

	bool blActed = false;
	sen_line_SData_t s_podSensorData = {{0}};
	sen_line_read( &s_podSensorData);
	sen_line_rescale( &s_podSensorData, &s_podSensorData);

	// node detection-measurement
 	if (((s_podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_NODE__LINE_THRESHOLD) ||
 		(s_podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_NODE__LINE_THRESHOLD)) &&
		!s_blDetectionActive) {
		s_ui16AvgLeft += s_podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT];
		s_ui16AvgRight += s_podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT];
		s_ui8NodeDetectionCounter++;
	} else if( (s_podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] > SUBS_NODE__LINE_THRESHOLD) &&
 		(s_podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] > SUBS_NODE__LINE_THRESHOLD) &&
		!s_blDetectionActive) {		
		s_ui16AvgLeft = 0;
		s_ui16AvgRight = 0;
		s_ui8NodeDetectionCounter = 0;
	}
	
	// robot is above a node -> enable node-detection-state
	if( (s_ui8NodeDetectionCounter >= SUBS_NODE__REQUIRED_MEASUREMENTS) &&
		!s_blDetectionActive) {		
	
		hal_motors_setSteps(0);
		s_blDetectionActive = true;
		blActed = true;		

	// Exit detection-state when still active & driven far enough; send previously analyzed node-type.
	} else if( s_blDetectionActive &&
		((hal_motors_getStepsLeft() >= SUBS_NODE__MOVE_ABOVE_CENTER) && 
		(hal_motors_getStepsRight() >= SUBS_NODE__MOVE_ABOVE_CENTER))) {
		
		s_ui16AvgLeft = s_ui16AvgLeft / s_ui8NodeDetectionCounter;
		s_ui16AvgRight = s_ui16AvgRight / s_ui8NodeDetectionCounter;

		// analyze the shape of the node		
		if( (s_ui16AvgLeft < SUBS_NODE__LINE_THRESHOLD) &&( s_ui16AvgRight < SUBS_NODE__LINE_THRESHOLD) && (s_podSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_NODE__LINE_THRESHOLD) ) { // 1 wird ersetzt durch den jeweiligen EEPROM-Kalibrierwert
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__CROSS;
			hal_led_switchOn(HAL_LED_PIN_BV__0);
			hal_led_switchOn(HAL_LED_PIN_BV__2);
			hal_led_switchOn(HAL_LED_PIN_BV__4);
			hal_led_switchOn(HAL_LED_PIN_BV__6);
		} else if( (s_ui16AvgLeft < SUBS_NODE__LINE_THRESHOLD) &&( s_ui16AvgRight < SUBS_NODE__LINE_THRESHOLD)) {
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__TOP_T;
			hal_led_switchOn(HAL_LED_PIN_BV__2);
			hal_led_switchOn(HAL_LED_PIN_BV__4);
			hal_led_switchOn(HAL_LED_PIN_BV__6);
		} else if( (s_ui16AvgLeft < SUBS_NODE__LINE_THRESHOLD) && (s_podSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_NODE__LINE_THRESHOLD)) {
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__RIGHT_T;
			hal_led_switchOn(HAL_LED_PIN_BV__0);
			hal_led_switchOn(HAL_LED_PIN_BV__4);
			hal_led_switchOn(HAL_LED_PIN_BV__6);
		} else if( (s_ui16AvgRight < SUBS_NODE__LINE_THRESHOLD) && (s_podSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_NODE__LINE_THRESHOLD)) {
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__LEFT_T;
			hal_led_switchOn(HAL_LED_PIN_BV__0);
			hal_led_switchOn(HAL_LED_PIN_BV__2);
			hal_led_switchOn(HAL_LED_PIN_BV__4);
		} else if( s_ui16AvgLeft < SUBS_NODE__LINE_THRESHOLD) {
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__TOP_RIGHT_EDGE;
			hal_led_switchOn(HAL_LED_PIN_BV__4);
			hal_led_switchOn(HAL_LED_PIN_BV__6);
		} else if( s_ui16AvgRight < SUBS_NODE__LINE_THRESHOLD) {
			subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__TOP_LEFT_EDGE;
			hal_led_switchOn(HAL_LED_PIN_BV__2);
			hal_led_switchOn(HAL_LED_PIN_BV__4);
		}
		
		hal_motors_setSpeed( 0, 0);
		hal_motors_setSteps( 0);
		s_ui16AvgLeft = 0;
		s_ui16AvgRight = 0;
		s_ui8NodeDetectionCounter = 0;

		com_SMessage_t podHitNodeMessage = { COM_MESSAGE_TYPE__RESPONSE_HIT_NODE, {0}};
		podHitNodeMessage.aui8Data[0] = subs_node_ui8CurrentNodeType;
		com_send( &podHitNodeMessage);

		s_blDetectionActive = false;
		blActed = true;
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

	subs_node_ui8CurrentNodeType = CONQUEST_NODE_TYPE__UNKNOWN;
	s_blDetectionActive = false;
	s_ui8NodeDetectionCounter = 0;
	s_ui16AvgLeft = 0;
	s_ui16AvgRight = 0;
}
