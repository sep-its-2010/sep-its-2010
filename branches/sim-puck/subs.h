#ifndef subs_h__
#define subs_h__

#include "common.h"

#include "subs_types.h"

enum {
	SUBS_MAX_BEHAVIORS = 16 ///< Specifies the maximal amount of behaviors.
};

void subs_init( void);

void subs_run( void);

void subs_reset( void);

bool subs_register(
	IN const subs_fnRun_t _fnRunCallback,
	IN OPT const subs_fnReset_t _fnResetCallback,
	IN const uint16_t _ui16Priority
	);

void subs_unregister(
	IN const subs_fnRun_t _fnRunCallback
	);

#endif /* subs_h__ */
