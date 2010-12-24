#ifndef sen_prox_h__
#define sen_prox_h__

#include "common.h"

#include "sen_prox_types.h"

void sen_prox_init( void);

void sen_prox_enable( void);

void sen_prox_disable( void);

void sen_prox_getCurrent(
	OUT sen_prox_SData_t* const _lppodData
	);

#endif // sen_prox_h__
