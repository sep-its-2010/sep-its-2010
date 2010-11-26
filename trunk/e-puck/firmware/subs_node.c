#include "hal_led.h"
#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"
#include "subs_calibration.h"

#include "subs_node.h"

enum {
	NODE_DETECTION__REQUIRED_MEASUREMENTS = 3, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
	
	NODE_DETECTION__LEFT_SENSORVALUE_THRESHOLD = 0,//0.6 * "Wert im EEPROM für linken Sensor"
	NODE_DETECTION__MIDDLE_SENSORVALUE_THRESHOLD = 0, //0.6 * ...
	NODE_DETECTION__RIGHT_SENSORVALUE_THRESHOLD = 0 // 0.6 * ...
};

uint8_t ui8NodeDetectionCounter; ///< Number of measurements in a row, which provided data above a certain threshold.
sen_line_SData_t podSensorData; ///< Holds data of the three ground-sensors.
// uint16_t ui16CurrentSpeed = hal_motors_getSpeedLeft();

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
// 	if ((2 * podSensorData.aui16Data[0] < l_calibrate[1][0]) ||
// 		(2 * podSensorData.aui16Data[2] < l_calibrate[1][2])) {
	if (true) { // <- wird entfernt sobald Auslesen der Kalibrierungsdaten klar geregelt ist

			if (ui8NodeDetectionCounter == 0) {
				hal_motors_setSteps(0);
			}

			ui16AvgLeft += podSensorData.aui16Data[0];
			ui16AvgRight += podSensorData.aui16Data[2];
			ui8NodeDetectionCounter += 1;
			// hal_motors_setSpeed(INITIAL_SPEED, 0); ??
	}

	// robot is above a node
	bool isMoving = (hal_motors_getStepsLeft() >= 240) && (hal_motors_getStepsRight() >= 240);
	if( (ui8NodeDetectionCounter >= NODE_DETECTION__REQUIRED_MEASUREMENTS) && isMoving) {
		ui16AvgLeft = ui16AvgLeft / ui8NodeDetectionCounter;
		ui16AvgRight = ui16AvgRight / ui8NodeDetectionCounter;

		// visualizes the shape of the recently detected node
// 		if( (ui16AvgLeft > 0) && (ui16AvgRight > 0)) {
// 			if (2 * l_buffer[1] < l_calibrate[1][1]) {
// 				LED0 = 1; 
// 			}
// 			if (2 * avg_left < l_calibrate[1][0]) {
// 				LED6 = 1; 
// 			}
// 			if (2 * avg_right < l_calibrate[1][2]) {
// 				LED2 = 1; 
// 			}
// 		}

		ui16AvgLeft = 0;
		ui16AvgRight = 0;
		hal_motors_setSpeed(0,0);
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
	
	for( uint8_t i = 0; sizeof( podSensorData.aui16Data); i++) {
		podSensorData.aui16Data[i] = 0;
	}
}
