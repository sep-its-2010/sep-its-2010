#include <string.h>
#include <p30f6014A.h>

#include "hal_led.h"
#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"

#include "subs_node.h"

enum {
	NODE_DETECTION__REQUIRED_MEASUREMENTS = 3, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
};

/*!
 * \brief
 * Stores the number of ground-sensor-measurements in a row, which provided data below a certain threshold.
 */
static uint8_t s_ui8NodeDetectionCounter = 0;

/*!
 * \brief
 * Holds data of the three ground-sensors.
 */
static sen_line_SData_t s_podSensorData = {{0}};

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
 * The robot will stop with its center above the node and visualize its state.
 */
bool subs_node_run( void) {
	bool nodeHit = false;	
	sen_line_read( &s_podSensorData);

	// node detection-measurement
	// @TODO sollte man diese Multiplikation mit 2 in einer Konstante ablegen und besser dokumentieren?
 	if ((2 * (s_podSensorData.aui16Data[0]) < 1) || // 1 wird ersetzt durch EEPROM-Kalibrierwert für linken Sensor über Linie
 		(2 * (s_podSensorData.aui16Data[2]) < 1)) { // 1 wird ersetzt durch EEPROM-Kalibrierwert für rechten Sensor über Linie
		s_ui16AvgLeft += s_podSensorData.aui16Data[0];
		s_ui16AvgRight += s_podSensorData.aui16Data[2];
		s_ui8NodeDetectionCounter++;
	} else {
		hal_motors_setSteps(0);
		s_ui16AvgLeft = 0;
		s_ui16AvgRight = 0;
		s_ui8NodeDetectionCounter = 0;
	}
	bool hasMoved = (hal_motors_getStepsLeft() > 0) && (hal_motors_getStepsRight() > 0);
	
	// robot is above a node
	if( (s_ui8NodeDetectionCounter >= NODE_DETECTION__REQUIRED_MEASUREMENTS) && hasMoved) {
		hal_motors_setSpeed( 0, 0); // @TODO hier muss man eventuell noch ein bisschen fahren, so dass der e-puck genau über dem Knoten steht
		hal_motors_setSteps( 0);
		nodeHit = true;		
		s_ui16AvgLeft = s_ui16AvgLeft / s_ui8NodeDetectionCounter;
		s_ui16AvgRight = s_ui16AvgRight / s_ui8NodeDetectionCounter;		

		// analyze the shape of the node		
		if( (s_ui16AvgLeft < 1) &&( s_ui16AvgRight < 1) && (2 * s_podSensorData.aui16Data[1] < 1) ) { // 1 wird ersetzt durch den jeweiligen EEPROM-Kalibrierwert
			subs_node_sui8CurrentNodeType = NODE_TYPE__CROSS;
		} else if( (s_ui16AvgLeft < 1) &&( s_ui16AvgRight < 1)) {
			subs_node_sui8CurrentNodeType = NODE_TYPE__TOP_T;
		} else if( (s_ui16AvgLeft < 1) && (2 * s_podSensorData.aui16Data[1] < 1)) {
			subs_node_sui8CurrentNodeType = NODE_TYPE__RIGHT_T;
		} else if( (s_ui16AvgRight < 1) && (2 * s_podSensorData.aui16Data[1] < 1)) {
			subs_node_sui8CurrentNodeType = NODE_TYPE__LEFT_T;
		} else if( s_ui16AvgLeft < 1) {
			subs_node_sui8CurrentNodeType = NODE_TYPE__TOP_RIGHT_EDGE;
		} else if( s_ui16AvgRight < 1) {
			subs_node_sui8CurrentNodeType = NODE_TYPE__TOP_LEFT_EDGE;
		}
		s_ui16AvgLeft = 0;
		s_ui16AvgRight = 0;
		
		// inform smartphone about node-detection
		// @TODO zusätzliches 9. Byte = bool alter Knoten weil nach Kollision?
		com_SMessage_t podHitNodeMessage = { COM_MESSAGE_TYPE__RESPONSE_HIT_NODE, {0}};
		podHitNodeMessage.aui8Data[0] = subs_node_sui8CurrentNodeType;
		com_send( &podHitNodeMessage);
	}
	return nodeHit;
}

/*!
 * \brief
 * Resets all values which are used to detect and analyze a node.
 * 
 * Clears the node-detection-counter and the last ground-sensor-measurement.
 */
void subs_node_reset( void) {
	subs_node_sui8CurrentNodeType = NODE_TYPE__UNKNOWN;
	s_ui8NodeDetectionCounter = 0;
	s_ui16AvgLeft = 0;
	s_ui16AvgRight = 0;
	memset( s_podSensorData.aui16Data, 0, sizeof(s_podSensorData.aui16Data));
}
