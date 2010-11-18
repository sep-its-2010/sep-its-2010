#include "hal_motors.h"


/*!
 * \brief
 * Holds the current step count of the left motor.
 * 
 * \see
 * hal_motors_setStepsLeft | hal_motors_getStepsLeft | hal_motors_setSteps
 */
volatile uint16_t hal_motors_ui16StepsLeft;


/*!
 * \brief
 * Holds the current step count of the right motor.
 * 
 * \see
 * hal_motors_setStepsRight | hal_motors_setStepsRight | hal_motors_setSteps
 */
volatile uint16_t hal_motors_ui16StepsRight;

/*!
 * \brief
 * Initializes the motor drivers.
 * 
 * Besides port and interrupt management, initialization includes resetting the step counters and setting the motor phases
 * to idle.
 * 
 * \warning
 * This function is not interrupt safe.
 */
void hal_motors_init( void) {

	TRISD &= 0xFF00;
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
	hal_motors_ui16StepsLeft = 0;
	hal_motors_ui16StepsRight = 0;
}


/*!
 * \brief
 * Sets the speed of the left motor.
 * 
 * \param _ui16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately. The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The driver needs to be initialized.
 *
 * \warning
 * Specifying more than 1000 steps per second can cause motor malfunctioning.
 * 
 * \see
 * hal_motors_init | hal_motors_setSpeedRight | hal_motors_setSpeed
 */
void hal_motors_setSpeedLeft(
	IN const int16_t _ui16StepsPerSecond
	) {

}


/*!
 * \brief
 * Sets the speed of the right motor.
 * 
 * \param _ui16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately. The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The driver needs to be initialized.
 *
 * \warning
 * Specifying more than 1000 steps per second can cause motor malfunctioning.
 * 
 * \see
 * hal_motors_init | hal_motors_setSpeedLeft | hal_motors_setSpeed
 */
void hal_motors_setSpeedRight(
	IN const int16_t _ui16StepsPerSecond
	) {

}

/*!
 * \brief
 * Sets the line and the angular speed of the motors.
 * 
 * \param _ui16StepsPerSecond
 * Specifies the line speed.
 * 
 * \param _i16AngularStepsPerSecond
 * Specifies the angular speed.
 * 
 * todo:
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The driver needs to be initialized.
 *
 * \warning
 * Specifying more than 1000 steps per second can cause motor malfunctioning.
 * 
 * \see
 * hal_motors_init | hal_motors_setSpeedLeft | hal_motors_setSpeedRight
 */
void hal_motors_setSpeed(
	IN const int16_t _ui16StepsPerSecond,
	IN const int16_t _i16AngularStepsPerSecond
	) {

}
