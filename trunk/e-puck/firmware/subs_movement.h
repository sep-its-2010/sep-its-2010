#ifndef subs_movement_h__
#define subs_movement_h__

#include "common.h"

enum {
	SUBS_MOVEMENT__LINE_THRESHOLD = 200, ///< Specifies the threshold below which a line is detected by the groundsensors.
	SUBS_MOVEMENT__INITIAL_LINE_SPEED = 600 ///< Specifies the default line speed.
};

bool subs_movement_run( void);

void subs_movement_reset( void);

static inline int16_t subs_movement_getCurrentLineSpeed( void);

static inline int16_t subs_movement_getCurrentAngularSpeed( void);

/*!
 * \brief
 * Gets the current line-speed.
 * 
 * \returns
 * The current movement speed of the robot in steps-per-second.
 *
 * \see
 * subs_movement_getCurrentAngularSpeed
 */
int16_t subs_movement_getCurrentLineSpeed( void) {

	extern volatile int16_t subs_movement_i16CurrentLineSpeed;

	return subs_movement_i16CurrentLineSpeed;
}

/*!
 * \brief
 * Gets the current angular-speed.
 * 
 * \returns
 * The current turning speed of the robot in steps-per-second.
 *
 * \see
 * subs_movement_getCurrentLineSpeed
 */
int16_t subs_movement_getCurrentAngularSpeed( void) {

	extern volatile int16_t subs_movement_i16CurrentAngularSpeed;

	return subs_movement_i16CurrentAngularSpeed;
}

#endif /* subs_movement_h__ */
