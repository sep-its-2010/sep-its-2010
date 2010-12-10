#include <p30f6014A.h>

#include <string.h>

#include "hal_int.h"

#include "hal_rtc.h"


void ISR AUTO_PSV _T1Interrupt( void);


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
 * Specifies the timer 1 interrupt of the real time clock.
 *
 * This ISR keeps the system tick counter up to date and triggers events.
 *
 * \warning
 * This ISR may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_rtc_init | hal_rtc_register | hal_rtc_getSystemUpTime
 */
void _T1Interrupt( void) {

	hal_int_clearFlag( HAL_INT_SOURCE__TIMER1);

	for( uint16_t ui16 = 0; ui16 < HAL_RTC_MAX_EVENTS; ui16++) {
		if( s_apodEvents[ui16].ui16Period && ++s_apodEvents[ui16].ui16CurrentTicks >= s_apodEvents[ui16].ui16Period) {
			s_apodEvents[ui16].ui16CurrentTicks = 0;
			s_apodEvents[ui16].fnEvent( ui16);
		}
	}

	s_ui32SystemUpTime++;
}


/*!
 * \brief
 * Initializes the real time clock.
 *
 * \param _ui16Period
 * Specifies the period of timer 1 which has to be above 0.
 * 
 * The list of events is cleared, the system up time and timer 1 are reset.
 * 
 * Timer 1 will only be enabled when a period above 0 is specified.
 * The period is based on a prescaler of /256 (#HAL_RTC_PRESCALER).
 * The initial priority of the associated timer interrupt is #HAL_INT_PRIORITY__5.
 * 
 * \remarks
 * - The initial interrupt priority may be changed by the user.
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 * - The module requires exclusive access to timer 1.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * hal_rtc_register | hal_rtc_getSystemUpTime
 */
void hal_rtc_init(
	IN const uint16_t _ui16Period
	) {

	T1CON = 0;
	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	hal_int_clearFlag( HAL_INT_SOURCE__TIMER1);

	s_ui32SystemUpTime = 0;
	memset( s_apodEvents, 0, sizeof( s_apodEvents));

	if( _ui16Period) {
		TMR1 = 0;
		PR1 = _ui16Period;
		T1CONbits.TCKPS = HAL_RTC_PRESCALER;
		T1CONbits.TON = true;

		hal_int_setPriority( HAL_INT_SOURCE__TIMER1, HAL_INT_PRIORITY__5);
		hal_int_enable( HAL_INT_SOURCE__TIMER1);
	}
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
 * - \c 0 to #HAL_RTC_MAX_EVENTS \c - \c 1 on success.
 * - \c #HAL_RTC_INVALID_HANDLE on error (invalid callback or period, no free slot).
 * 
 * The event is added at the first free position within the event array. The handle is based on this position.
 * An event can safely modify itself and any other events.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * hal_rtc_init | hal_rtc_unregister | hal_rtc_reset | hal_rtc_activate | hal_rtc_deactivate
 */
hal_rtc_handle_t hal_rtc_register(
	IN const hal_rtc_fnEvent_t _fnCallback,
	IN const uint16_t _ui16Period,
	IN const bool _blActive
	) {

	hal_rtc_handle_t hEvent = HAL_RTC_INVALID_HANDLE;

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	if( _fnCallback && _ui16Period) {
		hal_rtc_SEvent_t* lppodEvent = NULL;
		for( uint16_t ui16 = 0; !lppodEvent && ui16 < HAL_RTC_MAX_EVENTS; ui16++) {
			if( !s_apodEvents[ui16].ui16Period) {
				lppodEvent = &s_apodEvents[ui16];
				hEvent = ui16;
			}
		}

		if( lppodEvent) {
			lppodEvent->blActive = _blActive;
			lppodEvent->fnEvent = _fnCallback;
			lppodEvent->ui16Period = _ui16Period;
			lppodEvent->ui16CurrentTicks = 0;
		}
	}
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return hEvent;
}


/*!
 * \brief
 * Gets the current system up time in base ticks.
 * 
 * \returns
 * The system up ticks.
 *
 * To get the actual time in milliseconds, one must multiply the returned value with the timer 1 period and prescaler.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_rtc_init | HAL_RTC_PRESCALER
 */
uint32_t hal_rtc_getSystemUpTime( void) {

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	const uint32_t ui32CurSystemUpTime = s_ui32SystemUpTime;
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return ui32CurSystemUpTime;
}


/*!
 * \brief
 * Removes the specified event.
 * 
 * \param _hEvent
 * The handle of the event to be removed.
 * 
 * \returns
 * Whether or not the event was removed.
 *
 * An event may always unregister itself and any other events.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_rtc_init | hal_rtc_register
 */
bool hal_rtc_unregister(
	IN const hal_rtc_handle_t _hEvent
	) {

	bool blSuccess = false;

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	if( _hEvent < HAL_RTC_MAX_EVENTS) {
		memset( &s_apodEvents[_hEvent], 0, sizeof( *s_apodEvents));
	}
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return blSuccess;
}


/*!
 * \brief
 * Activates a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be activated.
 * 
 * \returns
 * Whether or not the event exists.
 *
 * An event may always activate any other events.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_deactivate | hal_rtc_register
 */
bool hal_rtc_activate(
	IN const hal_rtc_handle_t _hEvent
	) {

	bool blSuccess = false;

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	if( _hEvent < HAL_RTC_MAX_EVENTS) {
		s_apodEvents[_hEvent].blActive = true;
	}
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return blSuccess;
}


/*!
 * \brief
 * Deactivates a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be deactivated.
 * 
 * \returns
 * Whether or not the event exists.
 *
 * An event may always deactivate itself and any other events.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_activate | hal_rtc_register
 */
bool hal_rtc_deactivate(
	IN const hal_rtc_handle_t _hEvent
	) {

	bool blSuccess = false;

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	if( _hEvent < HAL_RTC_MAX_EVENTS) {
		s_apodEvents[_hEvent].blActive = false;
	}
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return blSuccess;
}


/*!
 * \brief
 * Resets a specified event.
 * 
 * \param _hEvent
 * The handle of the event to be reset.
 * 
 * \returns
 * Whether or not the event exists.
 *
 * An event may always reset itself and any other events.
 * Resetting an event only resets the associated tick counter.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * ha_rtc_init | hal_rtc_register
 */
bool hal_rtc_reset(
	IN const hal_rtc_handle_t _hEvent
	) {

	bool blSuccess = false;

	hal_int_disable( HAL_INT_SOURCE__TIMER1);
	if( _hEvent < HAL_RTC_MAX_EVENTS) {
		s_apodEvents[_hEvent].ui16CurrentTicks = 0;
	}
	hal_int_enable( HAL_INT_SOURCE__TIMER1);

	return blSuccess;
}
