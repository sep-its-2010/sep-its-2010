#ifndef subs_movement_h__
#define subs_movement_h__

#include "common.h"

enum {
	SUBS_MOVEMENT_LINE_THRESHOLD = 300, ///< Specifies the threshold below which a line is detected by the ground sensors.
	SUBS_MOVEMENT_INITIAL_LINE_SPEED = 600 ///< Specifies the default line speed.
};

bool subs_movement_run( void);

void subs_movement_reset( void);


#endif /* subs_movement_h__ */
