#include "hal_motors.h"
#include "conquest.h"
#include "sen_line.h"
#include "hal_led.h"
#include "com.h"

#include "subs_node.h"


enum {
	LEFT_DATA = 0, ///< Specifies the local index of left motor specific data.
	RIGHT_DATA = 1, ///< Specifies the local index of right motor specific data.
	FRONT_DATA = 1, ///< Specifies the local index of the front marker sub field.
	BACK_DATA = 0, ///< Specifies the local index of the back marker sub field.
	UPPER_MARKER_BOUND = SUBS_NODE_MARKER_LENGTH / 2 + SUBS_NODE_MARKER_WIDTH / 2, ///< Specifies the amount of steps within the node marker whether the line sensors are before the horizontal line.
	LOWER_MARKER_BOUND = SUBS_NODE_MARKER_LENGTH / 2 - SUBS_NODE_MARKER_WIDTH / 2 ///< Specifies the amount of steps within the node marker whether the line sensors are after the horizontal line.
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
 * This layer only triggers in #CONQUEST_STATE__MOVE_FORWARD and #CONQUEST_STATE__RETURN_NODE and remains active until it has finished.
 * 
 * Triggers when driving above a white node marker (#CONQUEST_WHITE_THRESHOLD) with the middle line sensor.
 * Then advances to the middle of the node (#SUBS_NODE_CENTER_STEPS) while scanning for left or right crossing lines.
 * When reaching the center a check for a line in face direction is performed (#CONQUEST_BLACK_THRESHOLD).
 * Finally, the conquest state is updated with the detected node type.
 *
 * \warning
 * The motors abstraction layer needs to be initialized (#hal_motors_init()).
 *
 * \see
 * subs_node_reset
 */
 bool subs_node_run( void) {

	static uint16_t s_aaui32Sum[2][2] = { { 0 }, { 0 } };
	static uint16_t s_aui16StepsOnHit[2] = { 0 };
	static uint16_t s_aui16Counter[2] = { 0 };

	bool blActed = false;

	const conquest_EState_t eState = conquest_getState();
	if( eState == CONQUEST_STATE__MOVE_FORWARD ||
		eState == CONQUEST_STATE__RETURN_NODE) {

		const uint16_t* const lpui16SenLine = conquest_getSensorImage()->podCalibratedLineSensors.aui16Reflected;

		// Trigger a new detection if idle & middle sensor is on white underground (cross in the middle of a node)
		if( !s_blDetectionActive) {
			if( lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > CONQUEST_WHITE_THRESHOLD) {
 				s_aaui32Sum[FRONT_DATA][LEFT_DATA] = 0;
				s_aaui32Sum[FRONT_DATA][RIGHT_DATA] = 0;
				s_aaui32Sum[BACK_DATA][LEFT_DATA] = 0;
				s_aaui32Sum[BACK_DATA][RIGHT_DATA] = 0;
				s_aui16Counter[FRONT_DATA] = 0;
				s_aui16Counter[BACK_DATA] = 0;

				s_aui16StepsOnHit[LEFT_DATA] = hal_motors_getStepsLeft();
				s_aui16StepsOnHit[RIGHT_DATA] = hal_motors_getStepsRight();
				hal_motors_setSpeed( conquest_getRequestedLineSpeed(), 0);

 				s_blDetectionActive = true;
				blActed = true;
			}
		} else {

 			// Robot reached center of node?
 			if( hal_motors_getStepsLeft() - s_aui16StepsOnHit[LEFT_DATA] >= SUBS_NODE_CENTER_STEPS &&
				hal_motors_getStepsRight() - s_aui16StepsOnHit[RIGHT_DATA] >= SUBS_NODE_CENTER_STEPS) {

				// One could actually extract the up direction from s_aaui32Sum because these values are rather accurate:
				// ~280 when up exists and ~520 otherwise
				uint16_t ui16Direction = CONQUEST_DIRECTION__DOWN;
				if( lpui16SenLine[SEN_LINE_SENSOR__LEFT] < CONQUEST_BLACK_THRESHOLD ||
					lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] < CONQUEST_BLACK_THRESHOLD ||
					lpui16SenLine[SEN_LINE_SENSOR__RIGHT] < CONQUEST_BLACK_THRESHOLD) {

					ui16Direction |= CONQUEST_DIRECTION__UP;
				}
				if( s_aaui32Sum[BACK_DATA][LEFT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_aui16Counter[BACK_DATA] ||
					s_aaui32Sum[FRONT_DATA][LEFT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_aui16Counter[FRONT_DATA]) {

					ui16Direction |= CONQUEST_DIRECTION__LEFT;
				}
				if( s_aaui32Sum[BACK_DATA][RIGHT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_aui16Counter[BACK_DATA] ||
					s_aaui32Sum[FRONT_DATA][RIGHT_DATA] < SUBS_NODE_CROSSING_LINE_THRESHOLD * s_aui16Counter[FRONT_DATA]) {

					ui16Direction |= CONQUEST_DIRECTION__RIGHT;
				}
				hal_led_set( ui16Direction);

				conquest_setLastNode( conquest_convertDirMaskToNode( ui16Direction));
				if( eState == CONQUEST_STATE__RETURN_NODE) {
					conquest_setState( CONQUEST_STATE__COLLISION);
				} else {
					conquest_setState( CONQUEST_STATE__HIT_NODE);
				}

 				hal_motors_setSpeed( 0, 0);
 				hal_motors_setSteps( 0);

 				s_blDetectionActive = false;
				blActed = true;

			// Only scan in white marker area but not the horizontal line.
			} else if( lpui16SenLine[SEN_LINE_SENSOR__MIDDLE] > CONQUEST_WHITE_THRESHOLD) {
				const uint16_t ui16StepsLeft = hal_motors_getStepsLeft() - s_aui16StepsOnHit[LEFT_DATA];
				const uint16_t ui16StepsRight = hal_motors_getStepsRight() - s_aui16StepsOnHit[RIGHT_DATA];
				if( ui16StepsLeft < LOWER_MARKER_BOUND  && ui16StepsRight < LOWER_MARKER_BOUND) {
					s_aaui32Sum[BACK_DATA][LEFT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__LEFT];
					s_aaui32Sum[BACK_DATA][RIGHT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__RIGHT];
					s_aui16Counter[BACK_DATA]++;
				} else if( ui16StepsRight > UPPER_MARKER_BOUND && ui16StepsRight > UPPER_MARKER_BOUND) {
					s_aaui32Sum[FRONT_DATA][LEFT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__LEFT];
					s_aaui32Sum[FRONT_DATA][RIGHT_DATA] += lpui16SenLine[SEN_LINE_SENSOR__RIGHT];
					s_aui16Counter[FRONT_DATA]++;
				}
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
