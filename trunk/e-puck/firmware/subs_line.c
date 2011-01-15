#include <string.h>
#include <stdlib.h>

#include "hal_motors.h"
#include "conquest.h"
#include "ringbuf.h"
#include "sen_line_types.h"

#include "subs_line.h"


/*!
 * \brief
 * Holds the current amount of line centering cycles.
 * 
 * \remarks
 * #subs_line_reset() must be called to clear the counter when the centering was aborted.
 * 
 * \see
 * subs_line_run | SUBS_LINE_CENTERING_CYCLES
 */
static uint16_t s_ui16CenteringCycles = 0;


/*!
 * \brief
 * Holds the current delayed line sensor values.
 * 
 * \remarks
 * #subs_line_reset() must be called to clear the dead time buffer when a new line tracing operation starts.
 * 
 * \see
 * subs_line_run
 */
static ringbuf_SContext_t s_podDelayBuffer;


/*!
 * \brief
 * Holds the current integration part of the PID feedback controller.
 * 
 * \remarks
 * #subs_line_reset() should be called to clear the integration part when a new line tracing operation starts.
 * 
 * \see
 * subs_line_run
 */
static int16_t s_i16DeltaSum = 0;


/*!
 * \brief
 * Holds the delta left-to-right line sensor of the previous cycle.
 * 
 * \remarks
 * #subs_line_reset() should be called to clear the delta when a new line tracing operation starts.
 * 
 * \see
 * subs_line_run
 */
static int16_t s_i16DeltaOld = 0;


/*!
 * \brief
 * Subsumption layer for line following.
 * 
 * \returns
 * - \c true: PID active
 * - \c false: PID inactive
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FORWARD if after the dead time or in #CONQUEST_STATE__CENTER_LINE.
 * 
 * Uses the difference of the calibrated left and right line sensors as input to a (dead time) PID feedback controller.
 * The controller is fix point based and can be configured through #SUBS_LINE_P_FIXPOINT_FACTOR, #SUBS_LINE_I_FIXPOINT_FACTOR,
 * #SUBS_LINE_D_FIXPOINT_FACTOR and #SUBS_LINE_DELAY_CYCLES.
 * 
 * \remarks
 * - The frame time needs to be constant.
 * - The line speed will never exceed #conquest_getRequestedLineSpeed().
 *
 * \warning
 * - The line sensors need to be calibrated before (#sen_line_calibrate()).
 * - The motors abstraction layer needs to be initialized (#hal_motors_init()).
 * 
 * \see
 * subs_line_reset
 */
bool subs_line_run( void) {	

	bool blActed = false;

	sen_line_SData_t podDelayed;
	const conquest_EState_t eState = conquest_getState();
	if( eState == CONQUEST_STATE__MOVE_FORWARD) {

		// Fill delay buffer with the current line sensor data
		ringbuf_pushRange( &s_podDelayBuffer, &conquest_getSensorImage()->podCalibratedLineSensors, sizeof( sen_line_SData_t));

		// Reached dead time?
		if( ringbuf_getUsage( &s_podDelayBuffer) >= SUBS_LINE_DELAY_CYCLES * sizeof( sen_line_SData_t)) {
			ringbuf_popRange( &s_podDelayBuffer, &podDelayed, sizeof( podDelayed));
			blActed = true;
		}
	} else if( eState == CONQUEST_STATE__CENTER_LINE) {
		if( ++s_ui16CenteringCycles > SUBS_LINE_CENTERING_CYCLES) {
			s_ui16CenteringCycles = 0;
			hal_motors_setSpeed( 0, 0);
			conquest_setState( CONQUEST_STATE__STOP);
		} else {
			memcpy( &podDelayed, &conquest_getSensorImage()->podCalibratedLineSensors, sizeof( podDelayed));
			blActed = true;
		}
	}

	if( blActed) {

		// Get delta left-to-right line sensor
		const int16_t i16DeltaNew = podDelayed.aui16Reflected[SEN_LINE_SENSOR__LEFT] - podDelayed.aui16Reflected[SEN_LINE_SENSOR__RIGHT];
		
		// Constrain integration part
		s_i16DeltaSum += s_i16DeltaOld;
		if( s_i16DeltaSum > SUBS_LINE_MAX_ABS_INTEGRATION) {
			s_i16DeltaSum = SUBS_LINE_MAX_ABS_INTEGRATION;
		} else if( s_i16DeltaSum < -SUBS_LINE_MAX_ABS_INTEGRATION) {
			s_i16DeltaSum = -SUBS_LINE_MAX_ABS_INTEGRATION;
		}

		// PID algorithm
		const int32_t i32FixpointP = SUBS_LINE_P_FIXPOINT_FACTOR * (int32_t)i16DeltaNew;
		const int32_t i32FixpointI = SUBS_LINE_I_FIXPOINT_FACTOR * (int32_t)s_i16DeltaSum;
		const int32_t i32FixpointD = SUBS_LINE_D_FIXPOINT_FACTOR * (int32_t)( i16DeltaNew - s_i16DeltaOld);
		const int32_t i32PID = ( i32FixpointP + i32FixpointI + i32FixpointD) / SUBS_LINE_PID_FIXPOINT_OFFSET;
		s_i16DeltaOld = i16DeltaNew;

		// Constrain & set speed
		int16_t i16AngularSpeed;
		if( i32PID / 2 > HAL_MOTORS_MAX_ABS_SPEED) {
			i16AngularSpeed = HAL_MOTORS_MAX_ABS_SPEED;
		} else if( i32PID / 2 < -HAL_MOTORS_MAX_ABS_SPEED) {
			i16AngularSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
		} else {
			i16AngularSpeed = (int16_t)( i32PID / 2);
		}

		if( eState == CONQUEST_STATE__MOVE_FORWARD) {
			uint16_t ui16RequestedLineSpeed = conquest_getRequestedLineSpeed();
			if( ui16RequestedLineSpeed + abs( i16AngularSpeed) > HAL_MOTORS_MAX_ABS_SPEED) {
				ui16RequestedLineSpeed = HAL_MOTORS_MAX_ABS_SPEED - abs( i16AngularSpeed);
			}
			hal_motors_setSpeed( ui16RequestedLineSpeed, i16AngularSpeed);
		} else {
			hal_motors_setSpeed( 0, i16AngularSpeed);
		}
	}

	return blActed;
}


/*!
 * \brief
 * Resets the line following layer.
 * 
 * The internal state of the PID feedback controller is reset.
 * 
 * \remarks
 * This function should be called whenever a new line tracing operation starts.
 * 
 * \see
 * subs_line_run
 */
void subs_line_reset( void) {

	s_i16DeltaSum = 0;
	s_i16DeltaOld = 0;
	s_ui16CenteringCycles = 0;

	static uint8_t s_aui8DelayBufferSpace[SUBS_LINE_MAX_DELAY_CYCLES * sizeof( sen_line_SData_t)];
	ringbuf_init( &s_podDelayBuffer, s_aui8DelayBufferSpace, sizeof( s_aui8DelayBufferSpace));
}
