
// Required for abs()
#include <stdlib.h>

#include "hal_int.h"

#include "hal_motors.h"


void ISR AUTO_PSV USE_SHADOWING _T5Interrupt( void);
void ISR AUTO_PSV USE_SHADOWING _T4Interrupt( void);


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
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft
 */
void _T5Interrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);

	// Buffer volatile data
	const int16_t i16CurrentSpeed = hal_motors_i16SpeedLeft;
	if( i16CurrentSpeed) {
		if( i16CurrentSpeed > 0) {
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
		} else {
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
		}
		hal_motors_ui16StepsLeft++;
	}
}


/*!
 * \brief
 * Specifies the timer 4 interrupt of the right step motor.
 * 
 * \see
 * hal_motors_setSpeed | hal_motors_setSpeedLeft | hal_motors_getStepsLeft
 */
void _T4Interrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);

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
 * to idle.
 * 
 * \warning
 * This function is not interrupt safe.
 */
void hal_motors_init( void) {

	// Configure timer interrupts
	T4CON = 0;
	T5CON = 0;
	hal_int_enable( HAL_INT_SOURCE__TIMER4);
	hal_int_enable( HAL_INT_SOURCE__TIMER5);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER4);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER5);
	hal_int_setPriority( HAL_INT_SOURCE__TIMER4, HAL_INT_PRIORITY__6);
	hal_int_setPriority( HAL_INT_SOURCE__TIMER5, HAL_INT_PRIORITY__6);

	TRISD &= ~( HAL_MOTORS_LEFT_MASK | HAL_MOTORS_RIGHT_MASK);
	hal_motors_ui16StepsLeft = 0;
	hal_motors_ui16StepsRight = 0;
	hal_motors_i16SpeedLeft = 0;
	hal_motors_i16SpeedRight = 0;
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
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
 * - This function is interrupt safe concerning timer internal interrupts.
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

	if( !_ui16StepsPerSecond) {
		T5CON = 0;
		hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	} else {
		T5CON = 0;

		hal_motors_i16SpeedLeft = _ui16StepsPerSecond;

		// prescaler: / 256
		T5CONbits.TCKPS = 3;
		TMR5 = 0;

#if( FCY > 256 * ( ( 1 << 16) - 1))
		const uint16_t ui16ClearCapture = ( FCY / 512) / abs( _ui16StepsPerSecond);
		if( ui16ClearCapture > ( ( 1 << 15) - 1)) {
			PR5 = 0xFFFF;
		} else {
			PR5 = ui16ClearCapture * 2;
		}
#else
		PR5 = ( FCY / 256) / abs( _ui16StepsPerSecond);
#endif

		T5CONbits.TON = 1;
	}
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
 * - This function is interrupt safe concerning timer internal interrupts.
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

	if( !_ui16StepsPerSecond) {
		T4CON = 0;
		hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
	} else {
		T4CON = 0;

		hal_motors_i16SpeedRight = _ui16StepsPerSecond;

		// prescaler: / 256
		T4CONbits.TCKPS = 3;
		TMR4 = 0;

#if( FCY > 256 * ( ( 1 << 16) - 1))
		const uint16_t ui16ClearCapture = ( FCY / 512) / abs( _ui16StepsPerSecond);
		if( ui16ClearCapture > ( ( 1 << 15) - 1)) {
			PR4 = 0xFFFF;
		} else {
			PR4 = ui16ClearCapture * 2;
		}
#else
		PR4 = ( FCY / 256) / abs( _ui16StepsPerSecond);
#endif

		T4CONbits.TON = 1;
	}
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
 * \remarks
 * - This function is interrupt safe concerning timer internal interrupts.
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
