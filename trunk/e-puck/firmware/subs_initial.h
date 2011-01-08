#ifndef subs_initial_h__
#define subs_initial_h__

#include "common.h"

enum {
	SUBS_INITIAL_PEAK_BORDER = 400, ///< Specifies the line sensor value at which a peak edge is detected.
//	SUBS_INITIAL_DIRECTION_THRESHOLD = 10, ///< Specifies the minimal amount of turn steps required to perform a direction calibration.
	SUBS_INITIAL_DIRECTION_DELTA = 50 ///< Specifies the +/- delta when comparing peaks against grid directions.
};


bool subs_initial_run( void);

void subs_initial_reset( void);

#endif /* subs_initial_h__ */
