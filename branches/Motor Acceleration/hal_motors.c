#include <stdlib.h>

#include "hal_int.h"

#include "hal_motors.h"


void _ISR NO_AUTO_PSV _T5Interrupt( void);

void _ISR NO_AUTO_PSV _T4Interrupt( void);

static void stepLeft( void);

static void stepRight( void);


/*!
 * \brief
 * Holds the current step count of the left motor.
 * 
 * \see
 * hal_motors_getStepsLeft | hal_motors_setSteps | hal_motors_ui16StepsRight
 */
volatile uint16_t hal_motors_ui16StepsLeft;


/*!
 * \brief
 * Holds the current step count of the right motor.
 * 
 * \see
 * hal_motors_setStepsRight | hal_motors_setSteps | hal_motors_ui16StepsLeft
 */
volatile uint16_t hal_motors_ui16StepsRight;


/*!
 * \brief
 * Holds the current speed of the left motor.
 *
 * Negative values indicate a reversed direction.
 * 
 * \see
 * hal_motors_setSpeedLeft | hal_motors_setSpeed | hal_motors_i16SpeedRight | hal_motors_getSpeedLeft
 */
volatile int16_t hal_motors_i16SpeedLeft;


/*!
 * \brief
 * Holds the current speed of the right motor.
 *
 * Negative values indicate a reversed direction.
 * 
 * \see
 * hal_motors_setSpeedRight | hal_motors_setSpeed | hal_motors_i16SpeedLeft | hal_motors_getSpeedRight
 */
volatile int16_t hal_motors_i16SpeedRight;


/*!
 * \brief
 * Holds the current phase of the left motor.
 * 
 * \see
 * hal_motors_setPhaseLeft | hal_motors_getPhaseLeft | hal_motors_ePhaseRight
 */
volatile hal_motors_EPhase_t hal_motors_ePhaseLeft;


/*!
 * \brief
 * Holds the current phase of the left motor.
 * 
 * \see
 * hal_motors_setPhaseRight | hal_motors_getPhaseRight | hal_motors_ePhaseLeft
 */
volatile hal_motors_EPhase_t hal_motors_ePhaseRight;


/*!
 * \brief
 * Specifies the timer 5 interrupt of the left step motor.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft | stepLeft
 */
void _T5Interrupt( void) {

	stepLeft();
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
}


/*!
 * \brief
 * Specifies the timer 4 interrupt of the right step motor.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft | stepRight
 */
void _T4Interrupt( void) {

	stepRight();
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
}


/*!
 * \brief
 * Advances the left step motor.
 * 
 * Advances the left step motor based on the current direction and phase and increments the associated step counter.
 * 
 * \remarks
 * A negative speed (reverse direction) also increments the step counter.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * stepRight | getStepsLeft
 */
void stepLeft( void) {

	// Buffer volatile data
	const int16_t i16CurrentSpeed = hal_motors_i16SpeedLeft;
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_ePhaseLeft) {
				case HAL_MOTORS_PHASE__0: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__3);
					break;
										  }
				case HAL_MOTORS_PHASE__1: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__0);
					break;
										  }
				case HAL_MOTORS_PHASE__2: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__1);
					break;
										  }
				case HAL_MOTORS_PHASE__3:
				default: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__2);
						 }
			}
		} else {
			switch( hal_motors_ePhaseLeft) {
				case HAL_MOTORS_PHASE__0: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__1);
					break;
										  }
				case HAL_MOTORS_PHASE__1: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__2);
					break;
										  }
				case HAL_MOTORS_PHASE__2: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__3);
					break;
										  }
				case HAL_MOTORS_PHASE__3:
				default: {
					hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__0);
						 }
			}
		}
		hal_motors_ui16StepsLeft++;
	}
}


/*!
 * \brief
 * Advances the right step motor.
 * 
 * Advances the right step motor based on the current direction and phase and increments the associated step counter.
 * 
 * \remarks
 * A negative speed (reverse direction) also increments the step counter.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * stepLeft | getStepsRight
 */
void stepRight( void) {

	// Buffer volatile data
	const int16_t i16CurrentSpeed = hal_motors_i16SpeedRight;
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_ePhaseRight) {
				case HAL_MOTORS_PHASE__0: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__1);
					break;
				}
				case HAL_MOTORS_PHASE__1: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__2);
					break;
				}
				case HAL_MOTORS_PHASE__2: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__3);
					break;
				}
				case HAL_MOTORS_PHASE__3:
				default: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__0);
				}
			}
		} else {
			switch( hal_motors_ePhaseRight) {
				case HAL_MOTORS_PHASE__0: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__3);
					break;
				}
				case HAL_MOTORS_PHASE__1: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__0);
					break;
				}
				case HAL_MOTORS_PHASE__2: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__1);
					break;
				}
				case HAL_MOTORS_PHASE__3:
				default: {
					hal_motors_setPhaseRight( HAL_MOTORS_PHASE__2);
				}
			}
		}
		hal_motors_ui16StepsRight++;
	}
}


/*!
 * \brief
 * Initializes the motor drivers.
 * 
 * Besides port and interrupt management, initialization includes resetting the step counters and setting the motor phases
 * to idle. Each motor timer interrupt priority is initially set to #HAL_INT_PRIORITY__6.
 *
 * \remarks
 * - The initial interrupt priority may be changed by the user.
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module requires exclusive access to timer 4 and timer 5.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 */
void hal_motors_init( void) {

	// Configure timer interrupts
	T4CON = 0;
	T5CON = 0;
	hal_int_disable( HAL_INT_SOURCE__TIMER4);
	hal_int_disable( HAL_INT_SOURCE__TIMER5);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);

	TMR4 = 0;
	PR4 = 0;
	T4CONbits.TCKPS = HAL_MOTORS_TIMER_PRESCALER;
	T4CONbits.TON = true;

	TMR5 = 0;
	PR5 = 0;
	T5CONbits.TCKPS = HAL_MOTORS_TIMER_PRESCALER;
	T5CONbits.TON = true;

	TRISD &= ~( HAL_MOTORS_LEFT_MASK | HAL_MOTORS_RIGHT_MASK);
	hal_motors_ui16StepsLeft = 0;
	hal_motors_ui16StepsRight = 0;
	hal_motors_i16SpeedLeft = 0;
	hal_motors_i16SpeedRight = 0;
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);

	hal_int_setPriority( HAL_INT_SOURCE__TIMER4, HAL_INT_PRIORITY__6);
	hal_int_setPriority( HAL_INT_SOURCE__TIMER5, HAL_INT_PRIORITY__6);
	hal_int_enable( HAL_INT_SOURCE__TIMER4);
	hal_int_enable( HAL_INT_SOURCE__TIMER5);
}


/*!
 * \brief
 * Sets the speed of the left motor.
 * 
 * \param _i16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately. The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * - Specifying more than 1000 steps per second can cause motor malfunctioning.
 * - This function may not be preempted by any function which accesses this module.
 *
 * \see
 * hal_motors_init | hal_motors_setSpeedRight | hal_motors_setSpeed
 */
void hal_motors_setSpeedLeft(
	IN const int16_t _i16StepsPerSecond
	) {

	// Constrain speed
	int16_t i16CorrectedSpeed = _i16StepsPerSecond;
	if( i16CorrectedSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
		i16CorrectedSpeed = HAL_MOTORS_MAX_ABS_SPEED;
	} else if( i16CorrectedSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
		i16CorrectedSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
	}

	hal_int_disable( HAL_INT_SOURCE__TIMER5);

	if( !i16CorrectedSpeed) {
		PR5 = 0;
		hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
		hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	} else {
		hal_motors_i16SpeedLeft = i16CorrectedSpeed;

		uint16_t ui16Period;

		// Too high frequencies require rescaling for the timer period to fit into 16 bits.
#if( FCY > 256 * ( ( 1 << 16) - 1))
		// Rounding correction
		const uint16_t ui16ClearCapture = ( FCY / 512) / abs( i16CorrectedSpeed);
		if( ui16ClearCapture > ( (uint16_t)( 1 << 15) - 1)) {
			ui16Period = 0xFFFF;
		} else {
			ui16Period = ui16ClearCapture * 2;
		}
#else
		ui16Period = ( FCY / 256) / abs( i16CorrectedSpeed);
#endif

		PR5 = ui16Period;
		TMR5 = 0;

// 		// Prevent cycle skipping
// 		if( TMR5 >= ui16Period) {
// 			TMR5 = 0;
// 			stepLeft();
// 			hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
// 		}
	}

	hal_int_enable( HAL_INT_SOURCE__TIMER5);
}


/*!
 * \brief
 * Sets the speed of the right motor.
 * 
 * \param _i16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately. The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * - Specifying more than 1000 steps per second can cause motor malfunctioning.
 * - This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | hal_motors_setSpeedLeft | hal_motors_setSpeed
 */
void hal_motors_setSpeedRight(
	IN const int16_t _i16StepsPerSecond
	) {

	// Constrain speed
	int16_t i16CorrectedSpeed = _i16StepsPerSecond;
	if( i16CorrectedSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
		i16CorrectedSpeed = HAL_MOTORS_MAX_ABS_SPEED;
	} else if( i16CorrectedSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
		i16CorrectedSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
	}

	hal_int_disable( HAL_INT_SOURCE__TIMER4);

	if( !i16CorrectedSpeed) {
		PR4 = 0;
		hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
		hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
	} else {
		hal_motors_i16SpeedRight = i16CorrectedSpeed;

		uint16_t ui16Period;

		// Too high frequencies require rescaling for the timer period to fit into 16 bits.
#if( FCY > 256 * ( ( 1 << 16) - 1))
		// Rounding correction
		const uint16_t ui16ClearCapture = ( FCY / 512) / abs( i16CorrectedSpeed);
		if( ui16ClearCapture > ( (uint16_t)( 1 << 15) - 1)) {
			ui16Period = 0xFFFF;
		} else {
			ui16Period = ui16ClearCapture * 2;
		}
#else
		ui16Period = ( FCY / 256) / abs( i16CorrectedSpeed);
#endif

		PR4 = ui16Period;
		TMR4 = 0;
// 
// 		// Prevent cycle skipping
// 		if( TMR4 >= ui16Period) {
// 			TMR4 = 0;
// 			stepRight();
// 			hal_int_enable( HAL_INT_SOURCE__TIMER4);
// 		}
	}

	hal_int_enable( HAL_INT_SOURCE__TIMER4);
}


/*!
 * \brief
 * Sets the line and the angular speed of the motors.
 * 
 * \param _i16StepsPerSecond
 * Specifies the line speed.
 * 
 * \param _i16AngularStepsPerSecond
 * Specifies the angular speed with negative values for turning left and positive values for turning right.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * - If |_i16StepsPerSecond| + |_i16AngularStepsPerSecond| > #HAL_MOTORS_MAX_ABS_SPEED no action is taken.
 * - This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | hal_motors_setSpeedLeft | hal_motors_setSpeedRight
 */
void hal_motors_setSpeed(
	IN const int16_t _i16StepsPerSecond,
	IN const int16_t _i16AngularStepsPerSecond
	) {

	if( abs( _i16AngularStepsPerSecond) + abs( _i16StepsPerSecond) <= HAL_MOTORS_MAX_ABS_SPEED) {
		PR4 = 0;
		PR5 = 0;
		hal_motors_setSpeedRight( _i16StepsPerSecond - _i16AngularStepsPerSecond);
		hal_motors_setSpeedLeft( _i16StepsPerSecond + _i16AngularStepsPerSecond);
	}
}
