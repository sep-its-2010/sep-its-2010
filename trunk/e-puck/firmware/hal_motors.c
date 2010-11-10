#include "hal_motors.h"

volatile uint16_t hal_motors_ui16StepsLeft;
volatile uint16_t hal_motors_ui16StepsRight;

void hal_motors_init( void) {

	TRISD &= 0xFF00;
	hal_motors_setPhaseLeft( HAL_MOTORS_PHASE__IDLE);
	hal_motors_setPhaseRight( HAL_MOTORS_PHASE__IDLE);
	hal_motors_ui16StepsLeft = 0;
	hal_motors_ui16StepsRight = 0;
}

void hal_motors_setSpeedLeft(
	IN const int16_t _ui16StepsPerSecond
	) {

}

void hal_motors_setSpeedRight(
	IN const int16_t _ui16StepsPerSecond
	) {

}

void hal_motors_setSpeed(
	IN const int16_t _ui16StepsPerSecond,
	IN const int16_t _i16AngularStepsPerSecond
	) {

}
