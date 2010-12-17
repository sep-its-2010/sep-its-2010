#include <string.h>

#include "hal_motors.h"
#include "sen_line.h"
#include "conquest.h"

#include "subs_line.h"

#define T 20.0f				// Interrupt interval for pid-controller
#define KRC 2.5f	//2.5		// stability limit
#define TC 0.4f	//0.2		// loop time
#define IMAX 1500			// max integration sum

const float t = (T / 1000.0); // timer interval in ms

// constants for pid-controller (ziegler-nichols rules)
const float kr = 0.6f * KRC;
const float tn = 0.5f * TC;
const float tv = 0.12f * TC;

// constants for pid-controller (PID-adjust-algorithm)
const float yp = 1.0;
#define yi ( t / tn)
#define yd ( tv / t)

static int16_t s_i16DeltaSum = 0;
static int16_t s_i16DeltaOld = 0;

/*!
 * \brief
 * Keeps the robot on the line.
 * 
 * \returns
 * True, as this is the last subsumption-behavior and will always be performed and start a new cycle afterwards.
 * 
 * Collects data from the ground-sensors and uses these values to compute slight movement changes,
 * which shall keep the robot above the line he is currently following.
 * Therefor a PID-controlling-algorithm is deployed.
 */
bool subs_line_run( void) {	

	uint16_t ui16RequestedLineSpeed = 500;//conquest_getRequestedLineSpeed();
	if( ui16RequestedLineSpeed) {

		// calculate integration-part
		s_i16DeltaSum += s_i16DeltaOld;
		if( s_i16DeltaSum > IMAX) {
			s_i16DeltaSum = IMAX;
		} else if( s_i16DeltaSum < -IMAX) {
			s_i16DeltaSum = -IMAX;
		}

		// Get delta left to right
		sen_line_SData_t podSensorData;
		sen_line_read( &podSensorData);
		sen_line_rescale( &podSensorData, &podSensorData);
		const int16_t i16DeltaNew = podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] - podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT];

		// PID-adjust-algorithm
		float y_new = kr * ( yp * i16DeltaNew + yi * s_i16DeltaSum + yd * ( i16DeltaNew - s_i16DeltaOld));
		s_i16DeltaOld = i16DeltaNew;

		// Constrain & set speed
		int16_t i16AngularSpeed = y_new / 2;
		if( i16AngularSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
			i16AngularSpeed = HAL_MOTORS_MAX_ABS_SPEED;
		} else if( i16AngularSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
			i16AngularSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
		}
		if( (int16_t)ui16RequestedLineSpeed + i16AngularSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
			ui16RequestedLineSpeed = HAL_MOTORS_MAX_ABS_SPEED - i16AngularSpeed;
		} else if( (int16_t)ui16RequestedLineSpeed + i16AngularSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
			ui16RequestedLineSpeed = i16AngularSpeed - HAL_MOTORS_MAX_ABS_SPEED;
		}
		hal_motors_setSpeed( ui16RequestedLineSpeed, i16AngularSpeed);
	}

	return ui16RequestedLineSpeed;
}

/*!
 * \brief
 * Resets  all values of the PID-controller.
 * 
 * Sets all parameter, which are used to compute the movement-adjustments of the robot back to default values.
 */
void subs_line_reset( void) {

	s_i16DeltaSum = 0;
	s_i16DeltaOld = 0;
}
