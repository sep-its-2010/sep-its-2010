#include "hal_rtc.h"

/*!
 * \brief
 * Holds the current system up time in ticks.
 * 
 * \see
 * hal_rtc_init | hal_rtc_getSystemUpTime
 */
volatile uint32_t s_ui32SystemUpTime = 0;


/*!
 * \brief
 * Holds the event slots.
 * 
 * \see
 * hal_rtc_init | hal_rtc_register
 */
static hal_rtc_SEvent_t s_apodEvents[HAL_RTC_MAX_EVENTS];


/*!
 * \brief
 * Initializes the real time clock.
 * 
 * The list of events is cleared, the system up time is reset and timer 1 is activated.
 * The time base is 10ms. Thus, one can trigger events on multiples of 10ms.
 * 
 * \remarks
 * - This function is only interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized before being used.
 */
void hal_rtc_init( void) {

}


/*!
 * \brief
 * Registers a new event.
 * 
 * \param _fnCallback
 * Specifies the event callback.
 * 
 * \param _ui16Period
 * Specifies the event period which must be above 0.
 * 
 * \param _blActive
 * Specifies whether or not the event is initially active.
 * 
 * \returns
 * A valid handle or #HAL_RTC_INVALID_HANDLE if an error occurred.
 * 
 * The event is added at the first free position within event array. The handle is constructed from this position.
 * An event can safely modify itself but modifying different events from within an event may cause race conditions.
 * 
 * \remarks
 * - This function is only interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized before being used.
 *
 * \see
 * hal_rtc_init | hal_rtc_unregister | hal_rtc_reset | hal_rtc_activate | hal_rtc_deactivate
 */
hal_rtc_handle_t hal_rtc_register(
	IN const hal_rtc_fnEvent_t _fnCallback,
	IN const uint16_t _ui16Period,
	IN const bool _blActive
	) {

}


/*!
 * \brief
 * Gets the current system up time in base ticks.
 * 
 * \returns
 * The system up ticks.
 *
 * To get the actual time in milliseconds, one must multiply the returned value with the timer 1 interval.
 * 
 * \remarks
 * This function is interrupt safe concerning interrupts from this module.
 * 
 * \see
 * hal_rtc_init
 */
uint32_t hal_rtc_getSystemUpTime( void) {

	// TODO: int fix
	return s_ui32SystemUpTime;
}


/*!
 * \brief
 * Removes the specified event.
 * 
 * \param _hEvent
 * The handle of the event to be removed.
 * 
 * \returns
 * - \c true: Success
 * - \c false: Invalid handle
 *
 * A event may always unregister itself. Modifying other events from within an event may cause race conditions.
 * 
 * \remarks
 * This function is interrupt safe concerning interrupts from this module.
 * 
 * \see
 * hal_rtc_init | hal_rtc_register
 */
bool hal_rtc_unregister(
	IN const hal_rtc_handle_t _hEvent
	) {

}


/*!
 * \brief
 * Activates a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be activated.
 * 
 * \returns
 * - \c true: Success
 * - \c false: Invalid handle
 *
 * Modifying other events from within an event may cause race conditions.
 * 
 * \remarks
 * This function is interrupt safe concerning interrupts from this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_deactivate | hal_rtc_register
 */
bool hal_rtc_activate(
	IN const hal_rtc_handle_t _hEvent
	) {

}


/*!
 * \brief
 * Deactivates a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be deactivated.
 * 
 * \returns
 * - \c true: Success
 * - \c false: Invalid handle
 *
 * An event may always deactivate itself. Modifying other events from within an event may cause race conditions.
 * 
 * \remarks
 * This function is interrupt safe concerning interrupts from this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_activate | hal_rtc_register
 */
bool hal_rtc_deactivate(
	IN const hal_rtc_handle_t _hEvent
	) {

}


/*!
 * \brief
 * Resets a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be reset.
 * 
 * \returns
 * - \c true: Success
 * - \c false: Invalid handle
 *
 * An event may always reset itself. Modifying other events from within an event may cause race conditions.
 * Resetting an event only resets the associated tick counter.
 * 
 * \remarks
 * This function is interrupt safe concerning interrupts from this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_register
 */
bool hal_rtc_reset(
	IN const hal_rtc_handle_t _hEvent
	) {

}
