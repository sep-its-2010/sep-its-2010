#include <string.h>
#include <stdlib.h>

#include "hal_int.h"
#include "hal_rtc.h"

#include "hal_motors.h"


void _ISR NO_AUTO_PSV _T5Interrupt( void);
void _ISR NO_AUTO_PSV _T4Interrupt( void);

static inline uint16_t convertSpeedToPeriod(
	IN const int16_t _i16Speed
	);

static void stepLeft( void);
static void stepRight( void);

static void cbAccalerationEvent(
	IN const hal_rtc_handle_t _hEvent
	);


/*!
 * \brief
 * Holds the current settings of each motor.
 * 
 * These include motor speed, phase, step counters and acceleration variables.
 * 
 * \see
 * hal_motors_init | hal_motors_restoreSettings | hal_motors_backupSettings
 */
hal_motors_SSettings_t hal_motors_podSettings;


/*!
 * \brief
 * Holds the handle to the acceleration event.
 * 
 * \remarks
 * The event is registered upon initialization (#hal_motors_init()) and will never be unregistered.
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
 * Calculates the 16 bit timer period with a /256 prescaler based on the requested steps per second.
 * 
 * \param _i16Speed
 * Specifies the speed in steps per second. No range check is applied.
 * 
 * \returns
 * The timer period.
 * 
 * The calculation is based on FCY. Frequencies above 16.77696MHz can be handled but with less precision.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - FCY must be defined.
 * 
 * \see
 * hal_motors_setSpeedLeft | hal_motors_setSpeedRight
 */
uint16_t convertSpeedToPeriod(
	IN const int16_t _i16Speed
	) {

	uint16_t ui16Period;

	// Too high frequencies require rescaling for the timer period to fit into 16 bits.
#	if( FCY > 256 * ( ( 1 << 16) - 1))
		// Rounding correction
		const uint16_t ui16ClearCapture = ( FCY / 512) / abs( _i16Speed);
		if( ui16ClearCapture > ( (uint16_t)( 1 << 15) - 1)) {
			ui16Period = 0xFFFF;
		} else {
			ui16Period = ui16ClearCapture * 2;
		}
#	else
		ui16Period = ( FCY / 256) / abs( _i16Speed);
#	endif

	return ui16Period;
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
	const int16_t i16CurrentSpeed = hal_motors_podSettings.ai16Speed[HAL_MOTORS_LEFT];
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_podSettings.aePhase[HAL_MOTORS_LEFT]) {
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
			switch( hal_motors_podSettings.aePhase[HAL_MOTORS_LEFT]) {
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
		hal_motors_podSettings.aui16Steps[HAL_MOTORS_LEFT]++;
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
	const int16_t i16CurrentSpeed = hal_motors_podSettings.ai16Speed[HAL_MOTORS_RIGHT];
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
			switch( hal_motors_podSettings.aePhase[HAL_MOTORS_RIGHT]) {
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
			switch( hal_motors_podSettings.aePhase[HAL_MOTORS_RIGHT]) {
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
		hal_motors_podSettings.aui16Steps[HAL_MOTORS_RIGHT]++;
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

	bool blDisableEvent = true;

	// Preventing code duplication
	for( uint16_t ui16 = 0; ui16 < 2; ui16++) {
		int16_t i16Acceleration = hal_motors_podSettings.ai16Acceleration[ui16];
		if( i16Acceleration) {
			int16_t i16Speed = hal_motors_podSettings.ai16Speed[ui16] + i16Acceleration;
			const int16_t i16FinalSpeed = hal_motors_podSettings.ai16FinalSpeed[ui16];

			// Reached final speed?
			if( ( i16Acceleration > 0 && i16Speed >= i16FinalSpeed) ||
				( i16Acceleration < 0 && i16Speed <= i16FinalSpeed)) {

				i16Speed = i16FinalSpeed;
				i16Acceleration = 0;
			} else {
				blDisableEvent = false;
			}

			// Do not change the order of the following two statements because setSpeedXXX() always resets the acceleration.
			if( ui16 == HAL_MOTORS_LEFT) {
				hal_motors_setSpeedLeft( i16Speed);
			} else {
				hal_motors_setSpeedRight( i16Speed);
			}
			hal_motors_podSettings.ai16Acceleration[ui16] = i16Acceleration;
		}
	}

	if( blDisableEvent) {
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
 * to idle. Each motor timer interrupt priority is initially set to #HAL_INT_PRIORITY__5.
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

	memset( &hal_motors_podSettings, 0, sizeof( hal_motors_podSettings));

	TRISD &= ~( HAL_MOTORS_LEFT_MASK | HAL_MOTORS_RIGHT_MASK);
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);

	hal_int_setPriority( HAL_INT_SOURCE__TIMER4, HAL_INT_PRIORITY__5);
	hal_int_setPriority( HAL_INT_SOURCE__TIMER5, HAL_INT_PRIORITY__5);
	hal_int_enable( HAL_INT_SOURCE__TIMER4);
	hal_int_enable( HAL_INT_SOURCE__TIMER5);

	return s_hAccelerationEvent != HAL_RTC_INVALID_HANDLE;
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
 * - The priority will escalate to the timer 5 interrupt priority during operation.
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
		hal_motors_podSettings.ai16Acceleration[HAL_MOTORS_LEFT] = 0;
		hal_motors_podSettings.ai16Speed[HAL_MOTORS_LEFT] = i16CorrectedSpeed;

		if( !i16CorrectedSpeed) {
			PR5 = 0;
			hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
			hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
		} else {
			const uint16_t ui16Period = convertSpeedToPeriod( i16CorrectedSpeed);
			const uint16_t ui16Counter = TMR5;
			PR5 = ui16Period;
			if( ui16Counter >= ui16Period) {
				TMR5 = 0;
				stepRight();
			}
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
 * - The priority will escalate to the timer 4 interrupt priority during operation.
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
		hal_motors_podSettings.ai16Acceleration[HAL_MOTORS_RIGHT] = 0;
		hal_motors_podSettings.ai16Speed[HAL_MOTORS_RIGHT] = i16CorrectedSpeed;

		if( !i16CorrectedSpeed) {
			PR4 = 0;
			hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
			hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
		} else {
			const uint16_t ui16Period = convertSpeedToPeriod( i16CorrectedSpeed);
			const uint16_t ui16Counter = TMR4;
			PR4 = ui16Period;
			if( ui16Counter >= ui16Period) {
				TMR4 = 0;
				stepRight();
			}
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
 * Ongoing acceleration of any motor is stopped.
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
 * - The priority will escalate to the timer 1 interrupt priority during operation.
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
	aui16AccelPerEvent[HAL_MOTORS_LEFT] = _ui16AccelPerEventLeft;
	aui16AccelPerEvent[HAL_MOTORS_RIGHT] = _ui16AccelPerEventRight;
	int16_t ai16FinalSpeed[2];
	ai16FinalSpeed[HAL_MOTORS_LEFT] = _i16FinalSpeedLeft;
	ai16FinalSpeed[HAL_MOTORS_RIGHT] = _i16FinalSpeedRight;

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {

		// Prevent code duplication
		for( uint16_t ui16Motor = 0; ui16Motor < 2; ui16Motor++) {
			const int16_t i16CurrentSpeed = hal_motors_podSettings.ai16Speed[ui16Motor];
			if( aui16AccelPerEvent[ui16Motor] && i16CurrentSpeed != ai16FinalSpeed[ui16Motor]) {
				int16_t i16FinalSpeed = ai16FinalSpeed[ui16Motor];
				if( i16FinalSpeed > HAL_MOTORS_MAX_ABS_SPEED) {
					i16FinalSpeed = HAL_MOTORS_MAX_ABS_SPEED;
				} else if( i16FinalSpeed < -HAL_MOTORS_MAX_ABS_SPEED) {
					i16FinalSpeed = -HAL_MOTORS_MAX_ABS_SPEED;
				}
				hal_motors_podSettings.ai16FinalSpeed[ui16Motor] = i16FinalSpeed;

				if( i16CurrentSpeed < i16FinalSpeed) {
					hal_motors_podSettings.ai16Acceleration[ui16Motor] = min( aui16AccelPerEvent[ui16Motor], HAL_MOTORS_MAX_ABS_SPEED);
				} else {
					hal_motors_podSettings.ai16Acceleration[ui16Motor] = -min( aui16AccelPerEvent[ui16Motor], HAL_MOTORS_MAX_ABS_SPEED);
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


/*!
 * \brief
 * Restores a previous backup of the motor settings.
 * 
 * \param _lppodSettings
 * Specifies the backup source.
 * 
 * Write detailed description for hal_motors_restoreSettings here.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - The priority will escalate to either timer 1, timer 4 or timer 5 interrupt priority during operation
 *   depending on which one is the highest.
 * - The backup is not checked for correctness.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | hal_motors_backupSettings
 */
void hal_motors_restoreSettings(
	IN const hal_motors_SSettings_t* const _lppodSettings
	) {

	// Type safe max()
	const hal_int_EPriority_t aeTimerPriorities[] = {
		hal_int_getPriority( HAL_INT_SOURCE__TIMER1),
		hal_int_getPriority( HAL_INT_SOURCE__TIMER4),
		hal_int_getPriority( HAL_INT_SOURCE__TIMER5)
	};
	uint16_t ui16Highest = 0;
	if( aeTimerPriorities[1] > aeTimerPriorities[0]) {
		ui16Highest = 1;
	}
	if( aeTimerPriorities[2] > aeTimerPriorities[1]) {
		ui16Highest = 2;
	}

	HAL_INT_ATOMIC_BLOCK( aeTimerPriorities[ui16Highest]) {

		// The order of the following instructions is crucial (hal_motors_setSpeedXXX() resets the acceleration)
		hal_motors_setSpeedLeft( _lppodSettings->ai16Speed[HAL_MOTORS_LEFT]);
		hal_motors_setSpeedRight( _lppodSettings->ai16Speed[HAL_MOTORS_RIGHT]);
		memcpy( &hal_motors_podSettings, _lppodSettings, sizeof( *_lppodSettings));

		if( _lppodSettings->ai16Acceleration[HAL_MOTORS_LEFT] || _lppodSettings->ai16Acceleration[HAL_MOTORS_RIGHT]) {
			hal_rtc_reset( s_hAccelerationEvent);
			hal_rtc_activate( s_hAccelerationEvent);
		} else {
			hal_rtc_deactivate( s_hAccelerationEvent);
		}
	}
}


/*!
 * \brief
 * Creates a backup of the current motor settings.
 * 
 * \param _lppodSettings
 * Specifies the backup destination.
 * 
 * A snapshot of the current settings is created which includes all variables of the left and right motor.
 * 
 * \remarks
 * - The module needs to be initialized.
 * - The priority will escalate to either timer 1, timer 4 or timer 5 interrupt priority during operation
 *   depending on which one is the highest.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_motors_init | hal_motors_restoreSettings
 */
void hal_motors_backupSettings(
	OUT hal_motors_SSettings_t* const _lppodSettings
	) {

	// Type safe max()
	const hal_int_EPriority_t aeTimerPriorities[] = {
		hal_int_getPriority( HAL_INT_SOURCE__TIMER1),
		hal_int_getPriority( HAL_INT_SOURCE__TIMER4),
		hal_int_getPriority( HAL_INT_SOURCE__TIMER5)
	};
	uint16_t ui16Highest = 0;
	if( aeTimerPriorities[1] > aeTimerPriorities[0]) {
		ui16Highest = 1;
	}
	if( aeTimerPriorities[2] > aeTimerPriorities[1]) {
		ui16Highest = 2;
	}

	HAL_INT_ATOMIC_BLOCK( aeTimerPriorities[ui16Highest]) {
		memcpy( _lppodSettings, &hal_motors_podSettings, sizeof( *_lppodSettings));
	}
}
