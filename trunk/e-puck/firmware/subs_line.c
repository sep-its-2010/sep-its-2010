#include <stdlib.h>

#include "hal_motors.h"
#include "sen_line.h"
#include "conquest.h"

#include "subs_line.h"


/*!
 * \brief
 * Holds the current integration part of the PID feedback controller.
 * 
 * \remarks
 * #subs_line_reset() should be called to clear the integration part when a new line tracing operation starts.
 * 
 * \see
 * subs_line_run | subs_line_reset
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
 * - \c true: adjusted motor speed
 * - \c false: not in #CONQUEST_STATE__MOVE_FOWARD
 *
 * This layer only triggers in #CONQUEST_STATE__MOVE_FOWARD.
 * 
 * Uses the difference of the calibrated left and right line sensors as input to a PID feedback controller.
 * The controller is fix point based and can be configured through #SUBS_LINE_P_FIXPOINT_FACTOR, #SUBS_LINE_I_FIXPOINT_FACTOR and
 * #SUBS_LINE_D_FIXPOINT_FACTOR.
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

	if( conquest_getState() == CONQUEST_STATE__MOVE_FOWARD) {

		// Get delta left-to-right line sensor
		sen_line_SData_t podSensorData;
		sen_line_read( &podSensorData);
		sen_line_rescale( &podSensorData, &podSensorData);
		const int16_t i16DeltaNew = podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] - podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT];
		
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

		uint16_t ui16RequestedLineSpeed = conquest_getRequestedLineSpeed();
		if( ui16RequestedLineSpeed + abs( i16AngularSpeed) > HAL_MOTORS_MAX_ABS_SPEED) {
			ui16RequestedLineSpeed = HAL_MOTORS_MAX_ABS_SPEED - abs( i16AngularSpeed);
		}
		hal_motors_setSpeed( ui16RequestedLineSpeed, i16AngularSpeed);

		blActed = true;
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
}
