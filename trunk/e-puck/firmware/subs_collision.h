#ifndef subs_collision_h__
#define subs_collision_h__

#include "common.h"

enum {
	SUBS_COLLISION__PROX_THRESHOLD = 64, ///< Specifies the threshold-value above which a collision will be detected.
	SUBS_COLLISION__TURNING_SPEED = 250, ///< Specifies the angular-speed with which the robot shall turn after a collision has been detected.
	SUBS_COLLISION__TURN_AROUND = 657, ///< Specifies the number of steps which each motor must have performed to turn the robot by nearly 180°.
	SUBS_COLLSION__SURFACE_THRESHOLD = 400, ///< Specifies the threshold-value above which the robot detects that there is no line under a line-sensor.
	SUBS_COLLISION__REQUIRED_LINE_MEASUREMENTS = 3, ///< Specifies how many positive measurements in a row are necessary to detect a line.
	SUBS_COLLISION__LINE_THRESHOLD = 270 ///< Specifies the threshold for line-detection.
};

bool subs_collision_run( void);

void subs_collision_reset( void);

#endif /* subs_collision_h__ */
