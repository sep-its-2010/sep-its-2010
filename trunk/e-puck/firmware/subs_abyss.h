#ifndef subs_abyss_h__
#define subs_abyss_h__

#include "common.h"


enum {
	SUBS_ABYSS_THRESHOLD = 210, ///< Specifies the line sensor threshold below which an abyss is detected.
	SUBS_ABYSS_REGRESSION = 200, ///< Specifies the amount of steps to drive backwards in case an abyss is detected.
};


bool subs_abyss_run( void);

void subs_abyss_reset( void);

#endif /* subs_abyss_h__ */
