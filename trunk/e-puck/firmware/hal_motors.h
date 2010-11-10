#ifndef hal_motors_h__
#define hal_motors_h__

#include <p30f6014A.h>

#include "common.h"

#include "hal_motors_types.h"

#define HAL_MOTORS_PIN__LEFT_A _LATD0
#define HAL_MOTORS_PIN__LEFT_B _LATD1
#define HAL_MOTORS_PIN__LEFT_C _LATD2
#define HAL_MOTORS_PIN__LEFT_D _LATD3
#define HAL_MOTORS_PIN__RIGHT_A _LATD4
#define HAL_MOTORS_PIN__RIGHT_B _LATD5
#define HAL_MOTORS_PIN__RIGHT_C _LATD6
#define HAL_MOTORS_PIN__RIGHT_D _LATD7

#define HAL_MOTORS_PIN_DIR__LEFT_A _TRISD0
#define HAL_MOTORS_PIN_DIR__LEFT_B _TRISD1
#define HAL_MOTORS_PIN_DIR__LEFT_C _TRISD2
#define HAL_MOTORS_PIN_DIR__LEFT_D _TRISD3
#define HAL_MOTORS_PIN_DIR__RIGHT_A _TRISD4
#define HAL_MOTORS_PIN_DIR__RIGHT_B _TRISD5
#define HAL_MOTORS_PIN_DIR__RIGHT_C _TRISD6
#define HAL_MOTORS_PIN_DIR__RIGHT_D _TRISD7

enum {
	HAL_MOTORS_LEFT_MASK = ( 1 << 0) | ( 1 << 1) | ( 1 << 2) | ( 1 << 3),
	HAL_MOTORS_LEFT_DATA_OFFSET = 0,
	HAL_MOTORS_RIGHT_MASK = ( 1 << 4) | ( 1 << 5) | ( 1 << 6) | ( 1 << 7),
	HAL_MOTORS_RIGHT_DATA_OFFSET = 4
};


void hal_motors_init( void);

void hal_motors_setSpeedLeft(
	IN const int16_t _ui16StepsPerSecond
	);
void hal_motors_setSpeedRight(
	IN const int16_t _ui16StepsPerSecond
	);
void hal_motors_setSpeed(
	IN const int16_t _ui16StepsPerSecond,
	IN const int16_t _i16AngularStepsPerSecond
	);

static inline void hal_motors_setPhaseLeft(
	IN const hal_motors_EPhase_t _ePhase
	);
static inline void hal_motors_setPhaseRight(
	IN const hal_motors_EPhase_t _ePhase
	);

static inline uint16_t hal_motors_getStepsLeft( void);
static inline uint16_t hal_motors_getStepsRight( void);

static inline void hal_motors_setStepsLeft(
	IN const uint16_t _ui16Steps
	);
static inline void hal_motors_setStepsRight(
	IN const uint16_t _ui16Steps
	);
static inline void hal_motors_setSteps(
	IN const uint16_t _ui16StepsLeft,
	IN const uint16_t _ui16StepsRight
	);


void hal_motors_setPhaseLeft(
	IN const hal_motors_EPhase_t _ePhase
	) {

	LATD = ( LATD & ~HAL_MOTORS_LEFT_MASK) | ( _ePhase << HAL_MOTORS_LEFT_DATA_OFFSET);
}

void hal_motors_setPhaseRight(
	IN const hal_motors_EPhase_t _ePhase
	) {

	LATD = ( LATD & ~HAL_MOTORS_RIGHT_MASK) | ( _ePhase << HAL_MOTORS_RIGHT_DATA_OFFSET);
}


uint16_t hal_motors_getStepsLeft( void) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;

	return hal_motors_ui16StepsLeft;
}


uint16_t hal_motors_getStepsRight( void) {

	extern volatile uint16_t hal_motors_ui16StepsRight;

	return hal_motors_ui16StepsRight;
}


void hal_motors_setStepsLeft(
	IN const uint16_t _ui16Steps
	) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;

	hal_motors_ui16StepsLeft = _ui16Steps;
}


void hal_motors_setStepsRight(
	IN const uint16_t _ui16Steps
	) {

	extern volatile uint16_t hal_motors_ui16StepsRight;

	hal_motors_ui16StepsRight = _ui16Steps;
}


void hal_motors_setSteps(
	IN const uint16_t _ui16StepsLeft,
	IN const uint16_t _ui16StepsRight
	) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;
	extern volatile uint16_t hal_motors_ui16StepsRight;

	hal_motors_ui16StepsLeft = _ui16StepsLeft;
	hal_motors_ui16StepsRight = _ui16StepsRight;
}


#endif /* hal_motors_h__ */
