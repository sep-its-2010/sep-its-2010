#include "hal_motors.h"
#include "hal_rtc.h"
#include "hal_led.h"
#include "conquest.h"

#include "subs_initial.h"


enum {
	MIN_EDGES = 4, ///< Specifies the minimal amount of required peak edges. 
	MAX_EDGES = 8, ///< Specifies the maximal amount of peak edges. 
	ALTERNATION_MASK_1 = 0b10101010, ///< Specifies the first LED mask to switch on when a invalid initial node error occurs.
	ALTERNATION_MASK_2 = 0b01010101, ///< Specifies the second LED mask to switch on when a invalid initial node error occurs.
	NUM_ALTERNATIONS = 5, ///< Specifies the amount of LED blink alternations when an invalid initial node error occurs.
	ALTERNATION_INTERVAL = 100 ///< Specifies the blink interval in time units defined by #hal_rtc_init().
};


/*!
 * \brief
 * Specifies the states of the initial node detection FSM.
 * 
 * \see
 * subs_initial_run | s_eState
 */
typedef enum {
	STATE__WAIT, ///< Entry state of the FSM. Wait for a node detection request.
	STATE__SCAN, ///< Node scan state. Turn 380 degrees and scan for peak edges.
	STATE__ANALYZE ///< Analyze peaks and generated node type.
} EState_t;


static void cbDetectionErrorBlinker(
	IN const hal_rtc_handle_t _hEvent
	);


/*!
 * \brief
 * Holds the initial node detection state.
 * 
 * \see
 * subs_initial_run | subs_initial_reset
 */
static EState_t s_eState = STATE__WAIT;


/*!
 * \brief
 * Holds the current toggle count of the initial node detection error visualization.
 * 
 * \see
 * cbDetectionErrorBlinker
 */
static uint16_t s_ui16ToggleCount = 0;


/*!
 * \brief
 * Holds the handle to the initial node detection error visualization event.
 * 
 * \remarks
 * #subs_initial_reset() needs to be called to register the event. Otherwise the handle is invalid.
 * 
 * \see
 * cbDetectionErrorBlinker
 */
static hal_rtc_handle_t s_hBlinkEvent = HAL_RTC_INVALID_HANDLE;


/*!
 * \brief
 * Visualizes a calibration error by blinking LEDs.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * In case the back level and white level calibration values are invalid this event is activated.
 * 
 * \remarks
 * #subs_calibration_reset() needs to be called to register the event.
 *
 * \see
 * sen_line_calibrate | s_hBlinkEvent
 */
void cbDetectionErrorBlinker(
	IN const hal_rtc_handle_t _hEvent
	) {

	if( !s_ui16ToggleCount) {
		hal_led_switchOn( ALTERNATION_MASK_1);
		hal_led_switchOff( ALTERNATION_MASK_2);
		s_ui16ToggleCount++;
	} else if( s_ui16ToggleCount > NUM_ALTERNATIONS) {
		s_ui16ToggleCount = 0;
		hal_rtc_deactivate( _hEvent);
		hal_rtc_reset( _hEvent);
		hal_led_switchOff( ALTERNATION_MASK_1 | ALTERNATION_MASK_2);
	} else {
		hal_led_toggle( ALTERNATION_MASK_1 | ALTERNATION_MASK_2);
		s_ui16ToggleCount++;
	}
}


/*!
 * \brief
 * Subsumption layer for initial node detection.
 * 
 * \returns
 * - \c true: detection active
 * - \c false: detection inactive
 *
 * This layer only triggers in #CONQUEST_STATE__INITIAL and remains active until it has finished.
 * 
 * Detects the node type the e-puck is currently standing.
 * If the detection fails, the error visualization event (#cbDetectionErrorBlinker()) is activated.
 *
 * First, the e-puck turns 360 degrees and performs an edge detection on the middle line sensor values. These values are then analyzed
 * for peaks (values below #SUBS_INITIAL_PEAK_BORDER) which represent the node grid lines. The actual node type is generated from
 * those peaks. If the robot direction faces any grid direction (#SUBS_INITIAL_DIRECTION_THRESHOLD) its direction is
 * centered .
 * 
 * \remarks
 * - #subs_line_reset() should be called to initialize the layer.
 * - Any ongoing higher subsumption actions will be interrupted and resumed after this layer finishes.
 *
 * \warning
 * The motors abstraction layer needs to be initialized (#hal_motors_init()).
 */
bool subs_initial_run( void) {

	static bool s_blInPeak = false;
	static bool s_blInPeakOnStart = false;
	static uint16_t s_ui16NumEdges = 0;
	static uint16_t s_aui16Edges[MAX_EDGES];

	bool blActed = false;

	switch( s_eState) {
		case STATE__WAIT: {
			if( conquest_getState() == CONQUEST_STATE__INITIAL) {
				hal_motors_setSpeed( 0, conquest_getRequestedLineSpeed());
				hal_motors_setSteps( 0);

				// Check whether the e-puck is currently right above a line with its sensors
				s_blInPeak = conquest_getSensorImage()->podCalibratedLineSensors.aui16Reflected[SEN_LINE_SENSOR__MIDDLE] < SUBS_INITIAL_PEAK_BORDER;
				s_blInPeakOnStart = s_blInPeak;
				s_ui16NumEdges = 0;

				s_eState = STATE__SCAN;
				blActed = true;
			}
			break;
		}
		case STATE__SCAN: {
			const uint16_t ui16Steps = hal_motors_getStepsRight();

			// 360 degree turn complete?
			if( ui16Steps >= HAL_MOTORS_FULL_TURN_STEPS) {
				s_eState = STATE__ANALYZE;
				hal_motors_setSpeed( 0, 0);

			// Edge detected?
			} else if( ( s_blInPeak && conquest_getSensorImage()->podCalibratedLineSensors.aui16Reflected[SEN_LINE_SENSOR__MIDDLE] > SUBS_INITIAL_PEAK_BORDER) ||
				( !s_blInPeak && conquest_getSensorImage()->podCalibratedLineSensors.aui16Reflected[SEN_LINE_SENSOR__MIDDLE] < SUBS_INITIAL_PEAK_BORDER)) {

				if( s_ui16NumEdges < MAX_EDGES) {
					s_blInPeak = !s_blInPeak;
					s_aui16Edges[s_ui16NumEdges++] = ui16Steps;
				}
			}
			blActed = true;
			break;
		}
		case STATE__ANALYZE: {

			// Missing falling/rising edge or not enough edges at all?
			if( s_ui16NumEdges & 0x1 || s_ui16NumEdges < MIN_EDGES || s_ui16NumEdges > MAX_EDGES) {
				s_eState = STATE__WAIT;
				conquest_setLastNode( CONQUEST_NODE__INVALID);
				conquest_setState( CONQUEST_STATE__STOP);
				hal_motors_setSpeed( 0, 0);
				hal_rtc_activate( s_hBlinkEvent);

			// Calculate peaks based on the mean of the the rising and falling edge
			} else {
				uint16_t aui16Peaks[MAX_EDGES / 2];

				// Started right above a line? -> first edge is a falling edge (black -> white)
				if( s_blInPeakOnStart) {

					// Need to consider wrap around 360 degree -> 0 degree
					aui16Peaks[0] = ( s_aui16Edges[0] + s_aui16Edges[s_ui16NumEdges - 1] + HAL_MOTORS_FULL_TURN_STEPS) / 2;
					if( aui16Peaks[0] >= HAL_MOTORS_FULL_TURN_STEPS) {
						aui16Peaks[0] -= HAL_MOTORS_FULL_TURN_STEPS;
					}
					for( uint16_t ui16 = 1; ui16 < s_ui16NumEdges - 1; ui16 += 2) {
						aui16Peaks[( ui16 + 1) / 2] = ( s_aui16Edges[ui16] + s_aui16Edges[ui16 + 1]) / 2;
					}
				} else {
					for( uint16_t ui16 = 0; ui16 < s_ui16NumEdges; ui16 += 2) {
						aui16Peaks[ui16 / 2] = ( s_aui16Edges[ui16] + s_aui16Edges[ui16 + 1]) / 2;
					}
				}

				// Calculate the deviation from the current direction to grid based on the first peak
				int16_t i16Deviation = aui16Peaks[0] % ( HAL_MOTORS_FULL_TURN_STEPS / 4);
				if( i16Deviation >= HAL_MOTORS_FULL_TURN_STEPS / 8) {
					i16Deviation -= HAL_MOTORS_FULL_TURN_STEPS / 4;
				}

				// Shift peaks
				for( uint16_t ui16 = 0; ui16 < s_ui16NumEdges / 2; ui16++) {
					const int16_t i16Tmp = (int16_t)aui16Peaks[ui16] - i16Deviation;
					aui16Peaks[ui16] = i16Tmp < 0 ? HAL_MOTORS_FULL_TURN_STEPS + i16Tmp : i16Tmp;
				}

				// Generate node type
				uint16_t ui16DirectionMask = 0;
				for( uint16_t ui16 = 0; ui16 < s_ui16NumEdges / 2; ui16++) {

					// Facing up?
					if( aui16Peaks[ui16] < SUBS_INITIAL_DIRECTION_DELTA || 
						aui16Peaks[ui16] > HAL_MOTORS_FULL_TURN_STEPS - SUBS_INITIAL_DIRECTION_DELTA) {

						ui16DirectionMask |= CONQUEST_DIRECTION__UP;

					// Facing right?
					} else if( aui16Peaks[ui16] < HAL_MOTORS_FULL_TURN_STEPS / 4 + SUBS_INITIAL_DIRECTION_DELTA && 
						aui16Peaks[ui16] > HAL_MOTORS_FULL_TURN_STEPS / 4 - SUBS_INITIAL_DIRECTION_DELTA) {

						ui16DirectionMask |= CONQUEST_DIRECTION__RIGHT;

					// Facing down?
					} else if( aui16Peaks[ui16] < HAL_MOTORS_FULL_TURN_STEPS / 2 + SUBS_INITIAL_DIRECTION_DELTA && 
						aui16Peaks[ui16] > HAL_MOTORS_FULL_TURN_STEPS / 2 - SUBS_INITIAL_DIRECTION_DELTA) {

						ui16DirectionMask |= CONQUEST_DIRECTION__DOWN;

					// Facing left?
					} else if( aui16Peaks[ui16] < HAL_MOTORS_FULL_TURN_STEPS * 3 / 4 + SUBS_INITIAL_DIRECTION_DELTA && 
						aui16Peaks[ui16] > HAL_MOTORS_FULL_TURN_STEPS * 3 / 4 - SUBS_INITIAL_DIRECTION_DELTA) {

						ui16DirectionMask |= CONQUEST_DIRECTION__LEFT;
					}
				}

				conquest_setState( ui16DirectionMask & CONQUEST_DIRECTION__UP ? CONQUEST_STATE__CENTER_LINE : CONQUEST_STATE__STOP);
				conquest_setLastNode( conquest_convertDirMaskToNode( ui16DirectionMask & 0xFF));
				s_eState = STATE__WAIT;
				blActed = true;
			}
			break;
		}
		default: {

		}
	}

	return blActed;
}


/*!
 * \brief
 * Resets the initial node layer.
 * 
 * The current state of the initial node detection layer is reset and the blinker event is registered if this did not already happen.
 * The event is initially inactive and will never be unregistered.
 * 
 * \warning
 * - The LED abstraction layer needs to be initialized (#hal_led_init()).
 * - The real time clock needs to be initialized (#hal_rtc_init()).
 * 
 * \see
 * subs_initial_run
 */
void subs_initial_reset( void) {

	if( s_hBlinkEvent == HAL_RTC_INVALID_HANDLE) {
		s_hBlinkEvent = hal_rtc_register( cbDetectionErrorBlinker, ALTERNATION_INTERVAL, false);
	} else {
		hal_rtc_deactivate( s_hBlinkEvent);
		hal_rtc_reset( s_hBlinkEvent);
	}
	s_eState = STATE__WAIT;
	s_ui16ToggleCount = 0;
	hal_led_switchOff( ALTERNATION_MASK_1 | ALTERNATION_MASK_2);
}
