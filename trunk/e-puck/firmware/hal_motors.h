#ifndef hal_motors_h__
#define hal_motors_h__

#include <p30f6014A.h>

#include "common.h"

#include "hal_motors_types.h"

#define HAL_MOTORS_PIN__LEFT_A _LATD0 ///< e-puck left motor driver pin A.
#define HAL_MOTORS_PIN__LEFT_B _LATD1 ///< e-puck left motor driver pin B.
#define HAL_MOTORS_PIN__LEFT_C _LATD2 ///< e-puck left motor driver pin C.
#define HAL_MOTORS_PIN__LEFT_D _LATD3 ///< e-puck left motor driver pin D.
#define HAL_MOTORS_PIN__RIGHT_A _LATD4 ///< e-puck right motor driver pin A.
#define HAL_MOTORS_PIN__RIGHT_B _LATD5 ///< e-puck right motor driver pin B.
#define HAL_MOTORS_PIN__RIGHT_C _LATD6 ///< e-puck right motor driver pin C.
#define HAL_MOTORS_PIN__RIGHT_D _LATD7 ///< e-puck right motor driver pin D.

#define HAL_MOTORS_PIN_DIR__LEFT_A _TRISD0 ///< e-puck left motor pin A direction.
#define HAL_MOTORS_PIN_DIR__LEFT_B _TRISD1 ///< e-puck left motor pin B direction.
#define HAL_MOTORS_PIN_DIR__LEFT_C _TRISD2 ///< e-puck left motor pin C direction.
#define HAL_MOTORS_PIN_DIR__LEFT_D _TRISD3 ///< e-puck left motor pin D direction.
#define HAL_MOTORS_PIN_DIR__RIGHT_A _TRISD4 ///< e-puck right motor pin A direction.
#define HAL_MOTORS_PIN_DIR__RIGHT_B _TRISD5 ///< e-puck right motor pin B direction.
#define HAL_MOTORS_PIN_DIR__RIGHT_C _TRISD6 ///< e-puck right motor pin C direction.
#define HAL_MOTORS_PIN_DIR__RIGHT_D _TRISD7 ///< e-puck right motor pin D direction.

enum {
	HAL_MOTORS_LEFT_MASK = ( 1 << 0) | ( 1 << 1) | ( 1 << 2) | ( 1 << 3), ///< Left motor port bit mask.
	HAL_MOTORS_LEFT_DATA_OFFSET = 0, ///< Port data offset of the right motor.
	HAL_MOTORS_RIGHT_MASK = ( 1 << 4) | ( 1 << 5) | ( 1 << 6) | ( 1 << 7), ///< Right motor port bit mask.
	HAL_MOTORS_RIGHT_DATA_OFFSET = 4, ///< Port data offset of the right motor.
	HAL_MOTORS_MAX_ABS_SPEED = 1000 ///< Maximal absolute step motor speed in steps per second.
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

static inline int16_t hal_motors_getSpeedLeft( void);
static inline int16_t hal_motors_getSpeedRight( void);

static inline void hal_motors_setPhaseLeft(
	IN const hal_motors_EPhase_t _ePhase
	);
static inline void hal_motors_setPhaseRight(
	IN const hal_motors_EPhase_t _ePhase
	);

static inline hal_motors_EPhase_t hal_motors_getPhaseLeft( void);
static inline hal_motors_EPhase_t hal_motors_getPhaseRight( void);

static inline uint16_t hal_motors_getStepsLeft( void);
static inline uint16_t hal_motors_getStepsRight( void);

static inline void hal_motors_setStepsLeft(
	IN const uint16_t _ui16Steps
	);
static inline void hal_motors_setStepsRight(
	IN const uint16_t _ui16Steps
	);
static inline void hal_motors_setSteps(
	IN const uint16_t _ui16Steps
	);


/*!
 * \brief
 * Gets the current speed of the left motor.
 * 
 * \returns
 * The current speed in steps per second. Negative values indicate a reversed direction.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_getSpeedRight | hal_motors_setSpeed | hal_motors_setSpeedLeft
 */
int16_t hal_motors_getSpeedLeft( void) {

	extern volatile int16_t hal_motors_i16SpeedLeft;

	return hal_motors_i16SpeedLeft;
}


/*!
 * \brief
 * Gets the current speed of the right motor.
 * 
 * \returns
 * The current speed in steps per second. Negative values indicate a reversed direction.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_getSpeedLeft | hal_motors_setSpeed | hal_motors_setSpeedRight
 */
int16_t hal_motors_getSpeedRight( void) {

	extern volatile int16_t hal_motors_i16SpeedRight;

	return hal_motors_i16SpeedRight;
}


/*!
 * \brief
 * Sets the phase of the left motor.
 * 
 * \param _ePhase
 * Specifies the phase to be set.
 * 
 * \remarks
 * - Changing the phase takes effect immediately.
 * - The user must assure the correctness of the phase sequences.
 * - The driver needs to be initialized.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_motors_init | hal_motors_setPhaseRight
 */
void hal_motors_setPhaseLeft(
	IN const hal_motors_EPhase_t _ePhase
	) {

	extern volatile hal_motors_EPhase_t hal_motors_ePhaseLeft;

	hal_motors_ePhaseLeft = _ePhase;
	LATD = ( LATD & ~HAL_MOTORS_LEFT_MASK) | ( _ePhase << HAL_MOTORS_LEFT_DATA_OFFSET);
}

/*!
 * \brief
 * Sets the phase of the right motor.
 * 
 * \param _ePhase
 * Specifies the phase to be set.
 * 
 * \remarks
 * - Changing the phase takes effect immediately.
 * - The user must assure the correctness of the phase sequences.
 * - The driver needs to be initialized.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_motors_init | hal_motors_setPhaseLeft
 */
void hal_motors_setPhaseRight(
	IN const hal_motors_EPhase_t _ePhase
	) {

	extern volatile hal_motors_EPhase_t hal_motors_ePhaseRight;

	hal_motors_ePhaseRight = _ePhase;
	LATD = ( LATD & ~HAL_MOTORS_RIGHT_MASK) | ( _ePhase << HAL_MOTORS_RIGHT_DATA_OFFSET);
}


/*!
 * \brief
 * Gets the phase of the left motor.
 * 
 * \returns
 * The phase of the right left motor.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_getPhaseRight | hal_motors_setPhaseLeft
 */
hal_motors_EPhase_t hal_motors_getPhaseLeft( void) {

	extern volatile hal_motors_EPhase_t hal_motors_ePhaseLeft;

	return hal_motors_ePhaseLeft;
}


/*!
 * \brief
 * Gets the phase of the right motor.
 * 
 * \returns
 * The phase of the right step motor.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_getPhaseLeft | hal_motors_setPhaseRight
 */
hal_motors_EPhase_t hal_motors_getPhaseRight( void) {

	extern volatile hal_motors_EPhase_t hal_motors_ePhaseRight;

	return hal_motors_ePhaseRight;
}


/*!
 * \brief
 * Gets the amount of steps of the left motor.
 * 
 * \returns
 * The current step counter of the left motor.
 * 
 * \remarks
 * - The step counter is incremented by the associated interrupt and can be changed with #hal_motors_setStepsLeft()
 * or #hal_motors_setSteps().
 * - This function is interrupt safe.
 * 
 * \see
 * hal_motors_getStepsRight | hal_motors_setStepsLeft
 */
uint16_t hal_motors_getStepsLeft( void) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;

	return hal_motors_ui16StepsLeft;
}


/*!
 * \brief
 * Gets the amount of steps of the right motor.
 * 
 * \returns
 * The current step counter of the right motor.
 * 
 * \remarks
 * - The step counter is incremented by the associated interrupt and can be changed with #hal_motors_setStepsRight()
 * or #hal_motors_setSteps().
 * - This function is interrupt safe.
 * 
 * \see
 * hal_motors_getStepsLeft | hal_motors_setStepsRight
 */
uint16_t hal_motors_getStepsRight( void) {

	extern volatile uint16_t hal_motors_ui16StepsRight;

	return hal_motors_ui16StepsRight;
}


/*!
 * \brief
 * Sets the step counter of the left motor.
 * 
 * \param _ui16Steps
 * Specifies the new counter value.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_setStepsRight | hal_motors_getStepsLeft | hal_motors_setSteps
 */
void hal_motors_setStepsLeft(
	IN const uint16_t _ui16Steps
	) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;

	hal_motors_ui16StepsLeft = _ui16Steps;
}


/*!
 * \brief
 * Sets the step counter of the right motor.
 * 
 * \param _ui16Steps
 * Specifies the new counter value.
 * 
 * \remarks
 * This function is interrupt safe.
 * 
 * \see
 * hal_motors_setStepsLeft | hal_motors_getStepsRight | hal_motors_setSteps
 */
void hal_motors_setStepsRight(
	IN const uint16_t _ui16Steps
	) {

	extern volatile uint16_t hal_motors_ui16StepsRight;

	hal_motors_ui16StepsRight = _ui16Steps;
}


/*!
 * \brief
 * Sets the step counters of the left and right motors.
 * 
 * \param _ui16Steps
 * Specifies the new value of the left and right step counters.
 * 
 * Write detailed description for hal_motors_setSteps here.
 * 
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_motors_setStepsRight | hal_motors_setStepsLeft | hal_motors_getStepsLeft | hal_motors_getStepsRight
 */
void hal_motors_setSteps(
	IN const uint16_t _ui16Steps
	) {

	extern volatile uint16_t hal_motors_ui16StepsLeft;
	extern volatile uint16_t hal_motors_ui16StepsRight;

	hal_motors_ui16StepsLeft = _ui16Steps;
	hal_motors_ui16StepsRight = _ui16Steps;
}


#endif /* hal_motors_h__ */
