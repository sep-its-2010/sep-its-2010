#ifndef hal_motors_types_h__
#define hal_motors_types_h__

#include "common.h"

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

#endif /* hal_motors_types_h__ */
