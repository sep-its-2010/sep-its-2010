#include <string.h>

#include "hal_led.h"
#include "hal_motors.h"
#include "sen_line.h"

#include "subs_line.h"

#define T 20.0				// Interrupt interval for pid-controller
#define KRC 2.5	//2.5		// stability limit
#define TC 0.4	//0.2		// loop time
#define IMAX 1500.0			// max integration sum
#define INITIAL_SPEED 600	// initial speed // @TODO Geschwindigkeit muss global hinterlegt werden
#define SPEED_MAX 1000		// maximum speed
#define SPEED_MIN 0			// minimum speed

//IFS0bits.T1IF = 0; // clear interrupt flag
const float t = (T / 1000.0); // timer interval in ms

// constants for pid-controller (ziegler-nichols rules)
const float kr = 0.6 * KRC;
const float tn = 0.5 * TC;
const float tv = 0.12 * TC;

// constants for pid-controller (PID-adjust-algorithm)
const float yp = 1.0;
// const float yi = (t / tn);
// const float yd = (tv / t);

static float y_old = 0.0;
static float diff_new = 0.0;
static float diff_old = 0.0;
static float diff_sum = 0.0;

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

	float y_new = 0;
	sen_line_SData_t podSensorData = {{0}};
	sen_line_read( &podSensorData);

	// calculate integration-part
	diff_sum += diff_new;
	if (diff_sum > IMAX) diff_sum = IMAX;
	if (diff_sum < -IMAX) diff_sum = -IMAX;

	// get new difference of sensor[0] to sensor[2]
	diff_new = podSensorData.aui16Data[2] - podSensorData.aui16Data[0];// - (l_calibrate[0][2] - l_calibrate[0][0]); // @TODO Kalibrierwerte ausm EEPROM

	// calculate integration-part
	diff_sum += diff_new;
	if (diff_sum > IMAX) diff_sum = IMAX;
	if (diff_sum < -IMAX) diff_sum = -IMAX;

	// get new difference of sensor[0] to sensor[2]
	diff_new = podSensorData.aui16Data[2] - podSensorData.aui16Data[0];// - (l_calibrate[0][2] - l_calibrate[0][0]); // @TODO EEPROM-Kalibrierwerte

	// PID-adjust-algorithm
//	y_new = kr * (yp * diff_new + yi * diff_sum + yd * (diff_new - diff_old));
	y_new = kr * yp * diff_new;

	// set motor speed
	if (INITIAL_SPEED + (int)(y_new / 2) > SPEED_MAX) {
		y_new = 2 * (SPEED_MAX - INITIAL_SPEED) - 1;
	}else if (INITIAL_SPEED - (int)(y_new / 2) < SPEED_MIN) {
		y_new = 2 * (INITIAL_SPEED - SPEED_MIN) + 1;
	}
	hal_motors_setSpeed(INITIAL_SPEED, (int)(y_new / 2));

	// store static values
	diff_old = diff_new;
	y_old = y_new;

	return true;
}

/*!
 * \brief
 * Resets  all values of the PID-controller.
 * 
 * Sets all parameter, which are used to compute the movement-adjustments of the robot back to default values.
 */
void subs_line_reset( void) {

}
