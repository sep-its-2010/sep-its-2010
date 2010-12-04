#include "hal_led.h"


/*!
 * \brief
 * Toggles the specified LEDs.
 * 
 * \param _ui16ToggleMask
 * Specifies the LED toggle mask based on #HAL_LED_PIN_BV__0 to #HAL_LED_PIN_BV__B.
 * 
 * The specified LEDs are toggled; the others remain unchanged.
 * 
 * \remarks
 * The LED abstraction layer needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_led_init | hal_led_switchOn | hal_led_switchOff | hal_led_set
 */
void hal_led_toggle(
	IN const uint16_t _ui16ToggleMask
	) {

	uint16_t ui16OldState = LATA;
	uint16_t ui16State = 0;
	if( _ui16ToggleMask & HAL_LED_PIN_BV__0) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__0)) {
			ui16State |= HAL_LED_PIN_MASK__0;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__1) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__1)) {
			ui16State |= HAL_LED_PIN_MASK__1;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__2) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__2)) {
			ui16State |= HAL_LED_PIN_MASK__2;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__3) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__3)) {
			ui16State |= HAL_LED_PIN_MASK__3;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__4) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__4)) {
			ui16State |= HAL_LED_PIN_MASK__4;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__5) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__5)) {
			ui16State |= HAL_LED_PIN_MASK__5;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__6) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__6)) {
			ui16State |= HAL_LED_PIN_MASK__6;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__7) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__7)) {
			ui16State |= HAL_LED_PIN_MASK__7;
		}
	}
	LATA = ( ui16OldState & ~HAL_LED_PIN_MASK__CIRCULAR) | ui16State;

	ui16OldState = LATC;
	ui16State = 0;
	if( _ui16ToggleMask & HAL_LED_PIN_BV__F) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__F)) {
			ui16State |= HAL_LED_PIN_MASK__F;
		}
	}
	if( _ui16ToggleMask & HAL_LED_PIN_BV__B) {
		if( !( ui16OldState & HAL_LED_PIN_MASK__B)) {
			ui16State |= HAL_LED_PIN_MASK__B;
		}
	}
	LATC = ( ui16OldState & ~HAL_LED_PIN_MASK__CHASSIS) | ui16State;
}
