#include "hal_led.h"
#include "hal_motors.h"
#include "com.h"
#include "sen_line.h"

#include "subs_node.h"

/*!
 * \brief
 * Analyzes if the robot is above a node.
 * 
 * \returns
 * True if a node has been detected, false otherwise.
 * 
 * Checks the ground-sensors for data. If the robot is currently moving and more than one sensor delivers several times
 * critical values the robot is supposed to be above a node and computes the nodes shape.
 * After that a message with the shape of the node is created and sent to the Smartphone via bluetooth.
 * The robot will stop with its center above the node and visualize its state.
 */
bool subs_node_run( void) {

	return false;
}

/*!
 * \brief
 * Resets all values which are used to detect and analyze a node.
 * 
 * Write detailed description for subs_node_reset here.
 */
void subs_node_reset( void) {

}
