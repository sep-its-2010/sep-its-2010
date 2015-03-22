#ifndef hal_rtc_types_h__
#define hal_rtc_types_h__

#include "common.h"

enum {
	HAL_RTC_INVALID_HANDLE = 0xFFFF ///< Specifies an invalid handle value.
};


/*!
 * \brief
 * Specifies the handle type which identifies events.
 * 
 * \see
 * HAL_RTC_INVALID_HANDLE
 */
typedef uint16_t hal_rtc_handle_t;


/*!
 * \brief
 * Specifies the event callback.
 */
typedef void (*hal_rtc_fnEvent_t)( IN const hal_rtc_handle_t);


/*!
 * \brief
 * Specifies the event container.
 */
typedef struct {
	hal_rtc_fnEvent_t fnEvent; ///< Holds the event callback.
	uint16_t ui16Period; ///< Holds the event period in ticks.
	uint16_t ui16CurrentTicks; ///< Holds the current tick count.
	bool blActive; ///< Marks whether or not the event is active.
	uint8_t : 8; // Padding
} hal_rtc_SEvent_t;


#endif /* hal_rtc_types_h__ */
