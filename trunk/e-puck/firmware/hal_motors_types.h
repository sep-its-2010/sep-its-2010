#ifndef hal_motors_types_h__
#define hal_motors_types_h__

#include "common.h"


enum {
	HAL_MOTORS_LEFT = 0, ///< Specifies the array index for data concerning the left motor.
	HAL_MOTORS_RIGHT = 1 ///< Specifies the array index for data concerning the right motor.
};


/*!
 * \brief
 * Specifies the available step motor phases.
 * 
 * \see
 * hal_motors_setPhaseLeft | hal_motors_setPhaseRight
 */
typedef enum {
	HAL_MOTORS_PHASE__IDLE = 0, ///< Step motor idle phase.
	HAL_MOTORS_PHASE__0 = ( 1 << 1) | ( 1 << 3), ///< Step motor phase 0.
	HAL_MOTORS_PHASE__1 = ( 1 << 1) | ( 1 << 2), ///< Step motor phase 1.
	HAL_MOTORS_PHASE__2 = ( 1 << 0) | ( 1 << 2), ///< Step motor phase 2.
	HAL_MOTORS_PHASE__3 = ( 1 << 0) | ( 1 << 3) ///< Step motor phase 3.
} hal_motors_EPhase_t;


/*!
 * \brief
 * Specifies the settings of the left and right motor.
 *
 * This structure is used to backup, to restore and to hold the current motor settings.
 *
 * \remarks
 * - The data should not be modified by the user himself.
 * - Use #HAL_MOTORS_LEFT and #HAL_MOTORS_RIGHT for the correct field index of each motor.
 * 
 * \see
 * Shal_motors_restoreSettings | hal_motors_backupSettings
 */
typedef struct {
	volatile uint16_t aui16Steps[2]; ///< Holds the speed of each motor.
	volatile int16_t ai16Speed[2]; ///< Holds the step count of each motor.
	volatile int16_t ai16Acceleration[2]; ///< Holds the acceleration of each motor.
	volatile int16_t ai16FinalSpeed[2]; ///< Holds the final speed to accelerate to of each motor.
	volatile hal_motors_EPhase_t aePhase[2]; ///< Holds the phase of each motor.
} hal_motors_SSettings_t;


#endif /* hal_motors_types_h__ */
