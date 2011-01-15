#include "hal_motors.h"
#include "conquest.h"
#include "sen_line.h"
#include "hal_led.h"
#include "com.h"

#include "subs_node.h"


enum {
	LEFT_DATA = 0,
	RIGHT_DATA = 1
};


/*!
 * \brief
 * Holds the node detection state.
 * 
 * \remarks
 * #subs_node_reset() must be called when a previous detection was aborted.
 * 
 * \see
 * subs_node_run
 */
static bool s_blDetectionActive = false;


/*!
 * \brief
 * Subsumption layer for node detection.
 * 
 * \returns
 * - \c true: driving above the white node marker
 * - \c false: otherwise
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FORWARD and remains active until it has finished.
 * 
 * Triggers when driving above a white node marker (#SUBS_NODE_WHITE_THRESHOLD) with the middle line sensor.
 * Then advances to the middle of the node (#SUBS_NODE_CENTER_STEPS) while scanning for left or right crossing lines.
 * When reaching the center a check for a line in face direction is performed (#SUBS_NODE_BLACK_THRESHOLD).
 * Finally, the conquest state is updated with the detected node type.
 *
 * \see
 * subs_node_reset
 */
 bool subs_node_run( void) {

	static uint16_t s_aui32Sum[2] = { 0 };
	static uint16_t s_ui16Counter = 0;

	bool blActed = false;

	if( conquest_getState() == CONQUEST_STATE__MOVE_FORWARD) {
		const uint16_t* const lpui16SenLine = conquest_getSensorImage()->podCalibratedLineSensors.aui16Reflected;

		// Trigger a new detection if idle & middle sensor is on white underground (cross in the middle of a node)
		if( !s_blDetectionActive) {
			if( lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > SUBS_NODE_WHITE_THRESHOLD) {
 				s_aui32Sum[LEFT_DATA] = 0;
 				s_aui32Sum[RIGHT_DATA] = 0;
				s_ui16Counter = 0;

				hal_motors_setSteps( 0);
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);

 				s_blDetectionActive = true;
				blActed = true;
			}
		} else {

 			// Robot reached center of node?
 			if( hal_motors_getStepsLeft() >= SUBS_NODE_CENTER_STEPS &&
				hal_motors_getStepsRight() >= SUBS_NODE_CENTER_STEPS) {

				uint16_t ui16Direction = CONQUEST_DIRECTION__DOWN;
				if( lpui16SenLine[SEN_LINE_SENSOR__LEFT] < SUBS_NODE_BLACK_THRESHOLD ||
					lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] < SUBS_NODE_BLACK_THRESHOLD ||
					lpui16SenLine[SEN_LINE_SENSOR__RIGHT] < SUBS_NODE_BLACK_THRESHOLD) {

					ui16Direction |= CONQUEST_DIRECTION__UP;
				}
				if( s_aui32Sum[LEFT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_ui16Counter) {
					ui16Direction |= CONQUEST_DIRECTION__LEFT;
				}
				if( s_aui32Sum[RIGHT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_ui16Counter) {
					ui16Direction |= CONQUEST_DIRECTION__RIGHT;
				}
				hal_led_set( ui16Direction);

				conquest_setLastNode( conquest_convertDirMaskToNode( ui16Direction));
				conquest_setState( CONQUEST_STATE__HIT_NODE);

 				hal_motors_setSpeed( 0, 0);
 				hal_motors_setSteps( 0);

 				s_blDetectionActive = false;
				blActed = true;

			// Only scan in white marker area
			} else if( lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > SUBS_NODE_WHITE_THRESHOLD) {
				s_aui32Sum[LEFT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__LEFT];
				s_aui32Sum[RIGHT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__RIGHT];
				s_ui16Counter++;
				blActed = true;
			}
		}
	}
 
 	return blActed;
 }


/*!
 * \brief
 * Resets the node detection layer.
 *
 * \remarks
 * This function should be called whenever a new node detection operation starts.
 * 
 * \see
 * subs_node_run
 */
void subs_node_reset( void) {

	s_blDetectionActive = false;
}
