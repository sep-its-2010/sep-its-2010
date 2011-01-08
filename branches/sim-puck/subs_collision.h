#ifndef subs_collision_h__
#define subs_collision_h__

#include "common.h"

enum {
	SUBS_COLLISION_THRESHOLD = 64, ///< Specifies the threshold-value above which a collision will be detected.
	SUBS_COLLISION_SURFACE_THRESHOLD = 400, ///< Specifies the threshold-value above which the robot detects that there is no line under a line-sensor.
	SUBS_COLLISION_MEASUREMENTS = 3, ///< Specifies how many positive measurements in a row are necessary to detect a line.
	SUBS_COLLISION_LINE_THRESHOLD = 270 ///< Specifies the threshold for line-detection.
};

bool subs_collision_run( void);

void subs_collision_reset( void);

#endif /* subs_collision_h__ */
