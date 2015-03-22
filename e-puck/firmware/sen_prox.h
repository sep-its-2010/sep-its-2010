#ifndef sen_prox_h__
#define sen_prox_h__

#include <p30f6014A.h>

#include "common.h"

#include "sen_prox_types.h"


#define SEN_PROX_PIN__PULSE_IR15 _LATF7 ///< e-puck IR pulse pin for LEDs 1 and 5.
#define SEN_PROX_PIN__PULSE_IR26 _LATF8 ///< e-puck IR pulse pin for LEDs 2 and 6.
#define SEN_PROX_PIN__PULSE_IR37 _LATG0 ///< e-puck IR pulse pin for LEDs 3 and 7.
#define SEN_PROX_PIN__PULSE_IR48 _LATG1 ///< e-puck IR pulse pin for LEDs 4 and 8.

#define SEN_PROX_PIN_DIR__PULSE_IR15 _TRISF7 ///< e-puck IR pulse pin direction for LEDs 1 and 5.
#define SEN_PROX_PIN_DIR__PULSE_IR26 _TRISF8 ///< e-puck IR pulse pin direction for LEDs 2 and 6.
#define SEN_PROX_PIN_DIR__PULSE_IR37 _TRISG0 ///< e-puck IR pulse pin direction for LEDs 3 and 7.
#define SEN_PROX_PIN_DIR__PULSE_IR48 _TRISG1 ///< e-puck IR pulse pin direction for LEDs 4 and 8.


bool sen_prox_init(
	IN const uint16_t _ui16CycleInterval
	);

bool sen_prox_enable( void);

void sen_prox_disable( void);

void sen_prox_getCurrent(
	OUT sen_prox_SData_t* const _lppodData
	);

#endif // sen_prox_h__
