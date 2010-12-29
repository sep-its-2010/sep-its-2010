#include <stdlib.h>

#include "hal_int.h"
#include "hal_rtc.h"

#include "hal_motors.h"


enum {
	LEFT_MOTOR = 0, ///< Specifies the local index for data concerning the left motor.
	RIGHT_MOTOR = 1 ///< Specifies the local index for data concerning the right motor.
};


void _ISR NO_AUTO_PSV _T5Interrupt( void);
void _ISR NO_AUTO_PSV _T4Interrupt( void);

static void stepLeft( void);
static void stepRight( void);

static void cbAccalerationEvent(
	IN const hal_rtc_handle_t _hEvent
	);


/*!
 * \brief
 * Holds the current step count of the left and right motors.
 * 
 * \see
 * hal_motors_getStepsLeft | hal_motors_getStepsRight | hal_motors_setSteps | LEFT_MOTOR | RIGHT_MOTOR
 */
volatile uint16_t hal_motors_aui16Steps[2] = { 0 };


/*!
 * \brief
 * Holds the current speed of the left and right motors.
 *
 * Negative values indicate a reversed direction.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_getSpeedLeft | hal_motors_getSpeedRight | LEFT_MOTOR | RIGHT_MOTOR
 */
volatile int16_t hal_motors_ai16Speed[2] = { 0 };


/*!
 * \brief
 * Holds the current phase of the left and right motors.
 * 
 * \see
 * hal_motors_setPhaseLeft | hal_motors_getPhaseLeft | hal_motors_setPhaseRight | hal_motors_getPhaseRight | LEFT_MOTOR | RIGHT_MOTOR
 */
volatile hal_motors_EPhase_t hal_motors_aePhase[2];


/*!
 * \brief
 * Holds the final speed of the left and right motors.
 *
 * The left and right motor will accelerate until they reach their final speed.
 * Negative values indicate a reversed direction.
 * 
 * \see
 * hal_motors_accelerate | hal_motors_getSpeedLeft | hal_motors_getSpeedRight | LEFT_MOTOR | RIGHT_MOTOR
 */
static volatile int16_t s_ai16FinalSpeed[2] = { 0 };


/*!
 * \brief
 * Holds the current acceleration of the left and right motors.
 *
 * These values are added to the current speed on each acceleration event.
 * 
 * \see
 * hal_motors_accelerate | LEFT_MOTOR | RIGHT_MOTOR
 */
static volatile int16_t s_ai16Acceleration[2] = { 0 };


/*!
 * \brief
 * Holds the handle to the acceleration event.
 * 
 * \remarks
 * The event is registered upon initialization (#hal_motors_init()) will never be unregistered.
 * 
 * \see
 * hal_motors_accelerate | cbAccalerationEvent
 */
static hal_rtc_handle_t s_hAccelerationEvent = HAL_RTC_INVALID_HANDLE;


/*!
 * \brief
 * Specifies the timer 5 interrupt of the left step motor.
 *
 * \remarks
 * The module needs to be initialized.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft | stepLeft
 */
void _T5Interrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
	stepLeft();
}


/*!
 * \brief
 * Specifies the timer 4 interrupt of the right step motor.
 *
 * \remarks
 * The module needs to be initialized.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft | stepRight
 */
void _T4Interrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
	stepRight();
}


/*!
 * \brief
 * Advances the left step motor.
 * 
 * Advances the left step motor based on the current direction and phase and increments the associated step counter.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - A negative speed (reverse direction) also increments the step counter.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * stepRight | getStepsLeft
 */
void stepLeft( void) {

	// Buffer volatile data
	const int16_t i16CurrentSpeed = hal_motors_ai16Speed[LEFT_MOTOR];
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_aePhase[LEFT_MOTOR]) {
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
			switch( hal_motors_aePhase[LEFT_MOTOR]) {
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
		hal_motors_aui16Steps[LEFT_MOTOR]++;
	}
}


/*!
 * \brief
 * Advances the right step motor.
 * 
 * Advances the right step motor based on the current direction and phase and increments the associated step counter.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - A negative speed (reverse direction) also increments the step counter.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * stepLeft | getStepsRight
 */
void stepRight( void) {

	// Buffer volatile data
	const int16_t i16CurrentSpeed = hal_motors_ai16Speed[RIGHT_MOTOR];
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_aePhase[RIGHT_MOTOR]) {
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
			switch( hal_motors_aePhase[RIGHT_MOTOR]) {
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
		hal_motors_aui16Steps[RIGHT_MOTOR]++;
	}
}


/*!
 * \brief
 * Performs the acceleration of the left and right motor.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * Checks #s_ai16Acceleration for each motor and adds it to the corresponding speed (#hal_motors_ai16Speed) once per event.
 * As soon as the intended speed (#s_ai16FinalSpeed) is reached the acceleration stops.
 *
 * The event deactivates itself when both motors are no longer accelerating.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - Check #hal_motors_setSpeedLeft() and #hal_motors_setSpeedRight() regarding interrupt safety.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | hal_motors_accelerate
 */
void cbAccalerationEvent(
	IN const hal_rtc_handle_t _hEvent
	) {

	int16_t i16AccelerationLeft = s_ai16Acceleration[LEFT_MOTOR];
	if( i16AccelerationLeft) {
		int16_t i16Speed = hal_motors_ai16Speed[LEFT_MOTOR] + i16AccelerationLeft;
		const int16_t i16FinalSpeed = s_ai16FinalSpeed[LEFT_MOTOR];
		if( i16AccelerationLeft > 0 && i16Speed >= i16FinalSpeed) {
			i16Speed = i16FinalSpeed;
			i16AccelerationLeft = 0;
		} else if( i16AccelerationLeft < 0 && i16Speed <= i16FinalSpeed) {
			i16Speed = i16FinalSpeed;
			i16AccelerationLeft = 0;
		}

		// Do not change the order of the following two statements because setSpeedXXX() always resets the acceleration.
		hal_motors_setSpeedLeft( i16Speed);
		s_ai16Acceleration[LEFT_MOTOR] = i16AccelerationLeft;
	}

	int16_t i16AccelerationRight = s_ai16Acceleration[RIGHT_MOTOR];
	if( i16AccelerationRight) {
		int16_t i16Speed = hal_motors_ai16Speed[RIGHT_MOTOR] + i16AccelerationRight;
		const int16_t i16FinalSpeed = s_ai16FinalSpeed[RIGHT_MOTOR];
		if( i16AccelerationRight > 0 && i16Speed >= i16FinalSpeed) {
			i16Speed = i16FinalSpeed;
			i16AccelerationRight = 0;
		} else if( i16AccelerationRight < 0 && i16Speed <= i16FinalSpeed) {
			i16Speed = i16FinalSpeed;
			i16AccelerationRight = 0;
		}

		// Do not change the order of the following two statements because setSpeedXXX() always resets the acceleration.
		hal_motors_setSpeedRight( i16Speed);
		s_ai16Acceleration[RIGHT_MOTOR] = i16AccelerationRight;
	}

	if( !i16AccelerationLeft && !i16AccelerationRight) {
		hal_rtc_deactivate( _hEvent);
	}
}


/*!
 * \brief
 * Initializes the motor drivers.
 *
 * \param _ui16AccelInterval
 * Specifies the acceleration interval length in real time clock event ticks.
 *
 * \returns
 * - \c true: on success
 * - \c false: the acceleration event could not be registered (e.g. invalid interval length or no free slot).
 * 
 * Besides port and interrupt management, initialization includes resetting the step counters and setting the motor phases
 * to idle. Each motor timer interrupt priority is initially set to #HAL_INT_PRIORITY__6.
 *
 * \remarks
 * - The real time clock needs to be initialized (#hal_rtc_init()).
 * - The initial interrupt priority may be changed by the user.
 * - This function is interrupt safe concerning timer 4 and timer 5 interrupts.
 * - The module requires exclusive access to timer 4 and timer 5.
 *
 * \warning
 * - This function may not be preempted by any function which accesses this module.
 * - The timer 4 and timer 5 need to have the same priority level.
 */
bool hal_motors_init(
	IN const uint16_t _ui16AccelInterval
	) {

	// Configure timer interrupts
	T4CON = 0;
	T5CON = 0;
	hal_int_disable( HAL_INT_SOURCE__TIMER4);
	hal_int_disable( HAL_INT_SOURCE__TIMER5);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);

	if( s_hAccelerationEvent == HAL_RTC_INVALID_HANDLE) {
		s_hAccelerationEvent = hal_rtc_register( cbAccalerationEvent, _ui16AccelInterval, false);
	} else {
		hal_rtc_deactivate( s_hAccelerationEvent);
		hal_rtc_reset( s_hAccelerationEvent);
	}

	TMR4 = 0;
	PR4 = 0;
	T4CONbits.TCKPS = HAL_MOTORS_TIMER_PRESCALER;
	T4CONbits.TON = true;

	TMR5 = 0;
	PR5 = 0;
	T5CONbits.TCKPS = HAL_MOTORS_TIMER_PRESCALER;
	T5CONbits.TON = true;

	hal_motors_aui16Steps[LEFT_MOTOR] = 0;
	hal_motors_aui16Steps[RIGHT_MOTOR] = 0;
	hal_motors_ai16Speed[LEFT_MOTOR] = 0;
	hal_motors_ai16Speed[RIGHT_MOTOR] = 0;
	s_ai16FinalSpeed[LEFT_MOTOR] = 0;
	s_ai16FinalSpeed[RIGHT_MOTOR] = 0;
	s_ai16Acceleration[LEFT_MOTOR] = 0;
	s_ai16Acceleration[RIGHT_MOTOR] = 0;

	TRISD &= ~( HAL_MOTORS_LEFT_MASK | HAL_MOTORS_RIGHT_MASK);
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);

	hal_int_setPriority( HAL_INT_SOURCE__TIMER4, HAL_INT_PRIORITY__6);
	hal_int_setPriority( HAL_INT_SOURCE__TIMER5, HAL_INT_PRIORITY__6);
	hal_int_enable( HAL_INT_SOURCE__TIMER4);
	hal_int_enable( HAL_INT_SOURCE__TIMER5);

	return s_hAccelerationEvent == HAL_RTC_INVALID_HANDLE;
}


/*!
 * \brief
 * Sets the speed of the left motor.
 * 
 * \param _i16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately and any ongoing acceleration of the left motor is stopped.
 * The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - The priority will escalate to the timer 5 interrupt priority during operation which might lead to starvation.
 * - The speed will be constrained to the range [-#HAL_MOTORS_MAX_ABS_SPEED ; #HAL_MOTORS_MAX_ABS_SPEED].
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
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

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER5)) {
		s_ai16Acceleration[LEFT_MOTOR] = 0;
		hal_motors_ai16Speed[LEFT_MOTOR] = i16CorrectedSpeed;

		if( !i16CorrectedSpeed) {
			PR5 = 0;
			hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
			hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
		} else {
			uint16_t ui16Period;

			// Too high frequencies require rescaling for the timer period to fit into 16 bits.
#			if( FCY > 256 * ( ( 1 << 16) - 1))
				// Rounding correction
				const uint16_t ui16ClearCapture = ( FCY / 512) / abs( i16CorrectedSpeed);
				if( ui16ClearCapture > ( (uint16_t)( 1 << 15) - 1)) {
					ui16Period = 0xFFFF;
				} else {
					ui16Period = ui16ClearCapture * 2;
				}
#			else
				ui16Period = ( FCY / 256) / abs( i16CorrectedSpeed);
#			endif

			PR5 = ui16Period;
			TMR5 = 0;

// 			// Prevent cycle skipping
// 			if( TMR5 >= ui16Period) {
// 				TMR5 = 0;
// 				stepLeft();
// 			}
		}
	}
}


/*!
 * \brief
 * Sets the speed of the right motor.
 * 
 * \param _i16StepsPerSecond
 * Specifies the speed in steps per second. A negative tick rate will reverse the motor direction.
 * 
 * The speed changes take effect immediately and any ongoing acceleration of the right motor is stopped.
  * The step motor is controlled by a timer interrupt.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - The priority will escalate to the timer 4 interrupt priority during operation which might lead to starvation.
 * - The speed will be constrained to the range [-#HAL_MOTORS_MAX_ABS_SPEED ; #HAL_MOTORS_MAX_ABS_SPEED].
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
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

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER4)) {
		s_ai16Acceleration[RIGHT_MOTOR] = 0;
		hal_motors_ai16Speed[RIGHT_MOTOR] = i16CorrectedSpeed;

		if( !i16CorrectedSpeed) {
			PR4 = 0;
			hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
			hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
		} else {

			uint16_t ui16Period;

			// Too high frequencies require rescaling for the timer period to fit into 16 bits.
#			if( FCY > 256 * ( ( 1 << 16) - 1))
				// Rounding correction
				const uint16_t ui16ClearCapture = ( FCY / 512) / abs( i16CorrectedSpeed);
				if( ui16ClearCapture > ( (uint16_t)( 1 << 15) - 1)) {
					ui16Period = 0xFFFF;
				} else {
					ui16Period = ui16ClearCapture * 2;
				}
#			else
				ui16Period = ( FCY / 256) / abs( i16CorrectedSpeed);
#			endif

			PR4 = ui16Period;
			TMR4 = 0;

//	 		// Prevent cycle skipping
// 			if( TMR4 >= ui16Period) {
// 				TMR4 = 0;
// 				stepRight();
// 			}
		}
	}
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
 * The actual speed changes are delegated to #hal_motors_setSpeedLeft() and #hal_motors_setSpeedRight().
 * Any ongoing acceleration of any motor is stopped.
 * 
 * \remarks
 * - Check #hal_motors_setSpeedLeft() and #hal_motors_setSpeedRight() regarding interrupt safety.
 * - The module needs to be initialized.
 *
 * \warning
 * - If |_i16StepsPerSecond| + |_i16AngularStepsPerSecond| > #HAL_MOTORS_MAX_ABS_SPEED no action is taken.
 * - This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init
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


/*!
 * \brief
 * Accelerates the speed of the left or right motor until they reach the specified speed.
 * 
 * \param _ui16AccelPerEventLeft
 * Specifies the acceleration of the left motor in steps per second per acceleration event.
 * 
 * \param _ui16AccelPerEventRight
 * Specifies the acceleration of the right motor in steps per second per acceleration event.
 * 
 * \param _i16FinalSpeedLeft
 * Specifies the intended speed of the left motor.
 * 
 * \param _i16FinalSpeedRight
 * Specifies the intended speed of the right motor.
 * 
 * The acceleration is specified absolutely and prefixed with the correct sign internally to avoid invalid configurations
 * (e.g. current speed > final speed but positive acceleration -> final speed can never be reached).
 *
 * The acceleration event is enabled if there is at least one motor which has not yet reached its final speed and the corresponding
 * acceleration parameter is above zero.
 *
 * The acceleration event is disabled as soon as both motors have reached their intended speed.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - The priority will escalate to the timer 1 interrupt priority during operation which might lead to starvation.
 * - The intended speed of each motor will be constrained to the range [-#HAL_MOTORS_MAX_ABS_SPEED ; #HAL_MOTORS_MAX_ABS_SPEED].
 * - The acceleration of each motor will be constrained to the range [\c 0 ; #HAL_MOTORS_MAX_ABS_SPEED].
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | cbAccalerationEvent
 */
void hal_motors_accelerate(
	IN const uint16_t _ui16AccelPerEventLeft,
	IN const uint16_t _ui16AccelPerEventRight,
	IN const int16_t _i16FinalSpeedLeft,
	IN const int16_t _i16FinalSpeedRight
	) {

	bool blActive = false;

	uint16_t aui16AccelPerEvent[2];
	aui16AccelPerEvent[LEFT_MOTOR] = _ui16AccelPerEventLeft;
	aui16AccelPerEvent[RIGHT_MOTOR] = _ui16AccelPerEventRight;
	int16_t ai16FinalSpeed[2];
	ai16FinalSpeed[LEFT_MOTOR] = _i16FinalSpeedLeft;
	ai16FinalSpeed[RIGHT_MOTOR] = _i16FinalSpeedRight;

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {

		// Prevent code duplication
		for( uint16_t ui16Motor = 0; ui16Motor < 2; ui16Motor++) {
			const int16_t i16CurrentSpeed = hal_motors_ai16Speed[ui16Motor];
			if( aui16AccelPerEvent[ui16Motor] && i16CurrentSpeed != ai16FinalSpeed[ui16Motor]) {
				int16_t i16FinalSpeed = ai16FinalSpeed[ui16Motor];
				if( i16FinalSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
					i16FinalSpeed = HAL_MOTORS_MAX_ABS_SPEED;
				} else if( i16FinalSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
					i16FinalSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
				}
				s_ai16FinalSpeed[ui16Motor] = i16FinalSpeed;

				if( i16CurrentSpeed < i16FinalSpeed) {
					s_ai16Acceleration[ui16Motor] = min( aui16AccelPerEvent[ui16Motor], HAL_MOTORS_MAX_ABS_SPEED);
				} else {
					s_ai16Acceleration[ui16Motor] = -min( aui16AccelPerEvent[ui16Motor], HAL_MOTORS_MAX_ABS_SPEED);
				}

				blActive = true;
			}
		}

		if( blActive) {
			hal_rtc_reset( s_hAccelerationEvent);
			hal_rtc_activate( s_hAccelerationEvent);
		}
	}
}
