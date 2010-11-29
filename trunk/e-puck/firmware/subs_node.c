#include <string.h>
#include "hal_led.h"
#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"

#include "subs_node.h"

enum {
	NODE_DETECTION__REQUIRED_MEASUREMENTS = 3, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
};

uint8_t ui8NodeDetectionCounter; ///< Number of ground-sensor-measurements in a row, which provided data below a certain threshold.
sen_line_SData_t podSensorData; ///< Holds data of the three ground-sensors.

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
 	sen_line_SData_t* _lppodSensorData = &podSensorData;	
	uint16_t ui16AvgLeft = 0;
	uint16_t ui16AvgRight = 0;
	sen_line_read( _lppodSensorData);

	// node detection
 	if ((2 * (podSensorData.aui16Data[0]) < 0) || // 0 wird ersetzt durch EEPROM-Wert
 		(2 * (podSensorData.aui16Data[2]) < 0)) { // 0 wird ersetzt durch EEPROM_Wert		
		if (ui8NodeDetectionCounter == 0) {
			hal_motors_setSteps(0);
		}
		ui16AvgLeft += podSensorData.aui16Data[0];
		ui16AvgRight += podSensorData.aui16Data[2];
		ui8NodeDetectionCounter += 1;
	}

	// robot is above a node
	bool hasMoved = (hal_motors_getStepsLeft() >= 240) && (hal_motors_getStepsRight() >= 240);
	
	if( (ui8NodeDetectionCounter >= NODE_DETECTION__REQUIRED_MEASUREMENTS) && hasMoved) {
		ui16AvgLeft = ui16AvgLeft / ui8NodeDetectionCounter;
		ui16AvgRight = ui16AvgRight / ui8NodeDetectionCounter;
		nodeHit = true;

		// visualizes the shape of the recently detected node
		if( (ui16AvgLeft > 0) && (ui16AvgRight > 0)) {
			if (podSensorData.aui16Data[1] < NODE_DETECTION__MIDDLE_SENSORVALUE_THRESHOLD) {
				hal_led_set(HAL_LED_PIN_BV__0);
			}
			if (ui16AvgLeft < NODE_DETECTION__LEFT_SENSORVALUE_THRESHOLD) {
				hal_led_set(HAL_LED_PIN_BV__6);
			}
			if (ui16AvgRight < NODE_DETECTION__RIGHT_SENSORVALUE_THRESHOLD) {
				hal_led_set(HAL_LED_PIN_BV__2);
			}
		}

		ui16AvgLeft = 0;
		ui16AvgRight = 0;
		hal_motors_setSpeed(0,0); // hier muss man eventuell noch ein bisschen fahren, so dass der e-puck genau über dem Knoten steht
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
	ui8NodeDetectionCounter = 0;
	memset( podSensorData.aui16Data, 0, sizeof(podSensorData.aui16Data));
}
