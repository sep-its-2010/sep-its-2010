#include "hal_led.h"
#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"

#include "subs_node.h"

enum {
	NODE_DETECTION__REQUIRED_MEASUREMENTS = 3, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
	
	NODE_DETECTION__LEFT_SENSORVALUE_THRESHOLD = 0,//0.7 * "Wert im EEPROM für linken Sensor"
	NODE_DETECTION__MIDDLE_SENSORVALUE_THRESHOLD = 0, //0.7 * ...
	NODE_DETECTION__RIGHT_SENSORVALUE_THRESHOLD = 0 // 0.7 * ...
};

uint8_t ui8NodeDetectionCounter; ///< Number of measurements in a row, which provided data above a certain threshold.
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
	bool isMoving = (hal_motors_getSpeedLeft() == 0) && (hal_motors_getSpeedRight() == 0);
	sen_line_SData_t* _lppodSensorData = &podSensorData;

	if( isMoving) {
		if( ui8NodeDetectionCounter < NODE_DETECTION__REQUIRED_MEASUREMENTS) {
			sen_line_read( _lppodSensorData);
			// Fallunterscheidung
		}
	}
		
	return nodeHit;
}

/*!
 * \brief
 * Resets all values which are used to detect and analyze a node.
 * 
 * Write detailed description for subs_node_reset here.
 */
void subs_node_reset( void) {
	ui8NodeDetectionCounter = 0;
	
	for( uint8_t i = 0; sizeof( podSensorData.aui16Data); i++) {
		podSensorData.aui16Data[i] = 0;
	}
}
