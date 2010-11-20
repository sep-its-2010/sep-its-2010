#ifndef hal_rtc_h__
#define hal_rtc_h__

#include "common.h"

#include "hal_rtc_types.h"

enum {
	HAL_RTC_MAX_EVENTS = 16 ///< Specifies the maximal amount of events.
};

void hal_rtc_init( void);

hal_rtc_handle_t hal_rtc_register(
	IN const hal_rtc_fnEvent_t _fnCallback,
	IN const uint16_t _ui16Period,
	IN const bool _blActive
	);

bool hal_rtc_unregister(
	IN const hal_rtc_handle_t _hEvent
	);

bool hal_rtc_activate(
	IN const hal_rtc_handle_t _hEvent
	);

bool hal_rtc_deactivate(
	IN const hal_rtc_handle_t _hEvent
	);

bool hal_rtc_reset(
	IN const hal_rtc_handle_t _hEvent
	);

uint32_t hal_rtc_getSystemUpTime( void);

#endif /* hal_rtc_h__ */
