#ifndef hal_led_h__
#define hal_led_h__

#include <p30f6014A.h>

#include "common.h"

#define HAL_LED_PIN__0 _LATA6 ///< e-puck LED 0 driver pin.
#define HAL_LED_PIN__1 _LATA7 ///< e-puck LED 1 driver pin.
#define HAL_LED_PIN__2 _LATA9 ///< e-puck LED 2 driver pin.
#define HAL_LED_PIN__3 _LATA12 ///< e-puck LED 3 driver pin.
#define HAL_LED_PIN__4 _LATA10 ///< e-puck LED 4 driver pin.
#define HAL_LED_PIN__5 _LATA13 ///< e-puck LED 5 driver pin.
#define HAL_LED_PIN__6 _LATA14 ///< e-puck LED 6 driver pin.
#define HAL_LED_PIN__7 _LATA15 ///< e-puck LED 7 driver pin.
#define HAL_LED_PIN__F _LATC1 ///< e-puck front LED driver pin.
#define HAL_LED_PIN__B _LATC2 ///< e-puck body LED driver pin.

#define HAL_LED_PIN_DIR__0 _TRISA6 ///< e-puck LED 0 pin direction.
#define HAL_LED_PIN_DIR__1 _TRISA7 ///< e-puck LED 1 pin direction.
#define HAL_LED_PIN_DIR__2 _TRISA9 ///< e-puck LED 2 pin direction.
#define HAL_LED_PIN_DIR__3 _TRISA12 ///< e-puck LED 3 pin direction.
#define HAL_LED_PIN_DIR__4 _TRISA10 ///< e-puck LED 4 pin direction.
#define HAL_LED_PIN_DIR__5 _TRISA13 ///< e-puck LED 5 pin direction.
#define HAL_LED_PIN_DIR__6 _TRISA14 ///< e-puck LED 6 pin direction.
#define HAL_LED_PIN_DIR__7 _TRISA15 ///< e-puck LED 7 pin direction.
#define HAL_LED_PIN_DIR__F _TRISC1 ///< e-puck front LED pin direction.
#define HAL_LED_PIN_DIR__B _TRISC2 ///< e-puck body LED pin direction.

enum {
	HAL_LED_PIN_BV__0 = 1 << 0, ///< Bit value of the e-puck LED 0.
	HAL_LED_PIN_BV__1 = 1 << 1, ///< Bit value of the e-puck LED 1.
	HAL_LED_PIN_BV__2 = 1 << 2, ///< Bit value of the e-puck LED 2.
	HAL_LED_PIN_BV__3 = 1 << 3, ///< Bit value of the e-puck LED 3.
	HAL_LED_PIN_BV__4 = 1 << 4, ///< Bit value of the e-puck LED 4.
	HAL_LED_PIN_BV__5 = 1 << 5, ///< Bit value of the e-puck LED 5.
	HAL_LED_PIN_BV__6 = 1 << 6, ///< Bit value of the e-puck LED 6.
	HAL_LED_PIN_BV__7 = 1 << 7, ///< Bit value of the e-puck LED 7.
	HAL_LED_PIN_BV__F = 1 << 8, ///< Bit value of the e-puck front LED.
	HAL_LED_PIN_BV__B = 1 << 9, ///< Bit value of the e-puck body LED .

	HAL_LED_PIN_MASK__0 = 1 << 6, ///< Port bit mask of the e-puck LED 0.
	HAL_LED_PIN_MASK__1 = 1 << 7, ///< Port bit mask of the e-puck LED 1.
	HAL_LED_PIN_MASK__2 = 1 << 9, ///< Port bit mask of the e-puck LED 2.
	HAL_LED_PIN_MASK__3 = 1 << 12, ///< Port bit mask of the e-puck LED 3.
	HAL_LED_PIN_MASK__4 = 1 << 10, ///< Port bit mask of the e-puck LED 4.
	HAL_LED_PIN_MASK__5 = 1 << 13, ///< Port bit mask of the e-puck LED 5.
	HAL_LED_PIN_MASK__6 = 1 << 14, ///< Port bit mask of the e-puck LED 6.
	HAL_LED_PIN_MASK__7 = 1 << 15, ///< Port bit mask of the e-puck LED 7.
	HAL_LED_PIN_MASK__CIRCULAR = HAL_LED_PIN_MASK__0 | HAL_LED_PIN_MASK__1 | HAL_LED_PIN_MASK__2 | HAL_LED_PIN_MASK__3 |
								 HAL_LED_PIN_MASK__4 | HAL_LED_PIN_MASK__5 | HAL_LED_PIN_MASK__6 | HAL_LED_PIN_MASK__7, ///< Port bit mask of the circular e-puck LEDs.

	HAL_LED_PIN_MASK__F = 1 << 1, ///< Port bit mask of the e-puck front LED.
	HAL_LED_PIN_MASK__B = 1 << 2, ///< Port bit mask of the e-puck body LED.
	HAL_LED_PIN_MASK__CHASSIS = HAL_LED_PIN_MASK__F | HAL_LED_PIN_MASK__B ///< Port bit mask of the e-puck front and body LEDs.
};

static inline void hal_led_init( void);
static inline void hal_led_set(
	IN const uint16_t _ui16BitSet
	);
static inline void hal_led_switchOn(
	IN const uint16_t _ui16EnableMask
	);
static inline void hal_led_switchOff(
	IN const uint16_t _ui16ClearMask
	);
void hal_led_toggle(
	IN const uint16_t _ui16ToggleMask
	);


/*!
 * \brief
 * Initializes the e-puck LEDs.
 * 
 * The required port pins are set as output and the LEDs are initially turned off.
 *
 * \warning
 * This function is not interrupt safe.
 */
void hal_led_init( void) {

	LATA &= ~HAL_LED_PIN_MASK__CIRCULAR;
	LATC &= ~HAL_LED_PIN_MASK__CHASSIS;

	hal_led_set( 0);
}


/*!
 * \brief
 * Forces a new state for all LEDs.
 * 
 * \param _ui16BitSet
 * Specifies the new state of all LEDs based on #HAL_LED_PIN_BV__0 to #HAL_LED_PIN_BV__B.
 * 
 * The current state of all LEDs is overridden by the specified bit set.
 * 
 * \remarks
 * - The LED abstraction layer needs to be initialized before being used.
 * - Using constant values is recommended due to function inlining.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_led_init | hal_led_switchOn | hal_led_switchOff | hal_led_toggle
 */
void hal_led_set(
	 IN const uint16_t _ui16BitSet
	 ) {

	uint16_t ui16State = 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__0 ? HAL_LED_PIN_MASK__0 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__1 ? HAL_LED_PIN_MASK__1 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__2 ? HAL_LED_PIN_MASK__2 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__3 ? HAL_LED_PIN_MASK__3 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__4 ? HAL_LED_PIN_MASK__4 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__5 ? HAL_LED_PIN_MASK__5 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__6 ? HAL_LED_PIN_MASK__6 : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__7 ? HAL_LED_PIN_MASK__7 : 0;
	LATA = ( LATA & ~HAL_LED_PIN_MASK__CIRCULAR) | ui16State;

	ui16State = 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__F ? HAL_LED_PIN_MASK__F : 0;
	ui16State |= _ui16BitSet & HAL_LED_PIN_BV__B ? HAL_LED_PIN_MASK__B : 0;
	LATC = ( LATC & ~HAL_LED_PIN_MASK__CHASSIS) | ui16State;
}


/*!
 * \brief
 * Turns the specified LEDs on.
 * 
 * \param _ui16EnableMask
 * Specifies the LED enable mask based on #HAL_LED_PIN_BV__0 to #HAL_LED_PIN_BV__B.
 * 
 * The specified LEDs are enabled; the others remain unchanged.
 * 
 * \remarks
 * - The LED abstraction layer needs to be initialized before being used.
 * - Using constant values is recommended due to function inlining.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_led_init | hal_led_switchOff | hal_led_set | hal_led_toggle
 */
void hal_led_switchOn(
	IN const uint16_t _ui16EnableMask
	) {

	uint16_t ui16State = 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__0 ? HAL_LED_PIN_MASK__0 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__1 ? HAL_LED_PIN_MASK__1 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__2 ? HAL_LED_PIN_MASK__2 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__3 ? HAL_LED_PIN_MASK__3 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__4 ? HAL_LED_PIN_MASK__4 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__5 ? HAL_LED_PIN_MASK__5 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__6 ? HAL_LED_PIN_MASK__6 : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__7 ? HAL_LED_PIN_MASK__7 : 0;
	LATA |= ui16State;

	ui16State = 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__F ? HAL_LED_PIN_MASK__F : 0;
	ui16State |= _ui16EnableMask & HAL_LED_PIN_BV__B ? HAL_LED_PIN_MASK__B : 0;
	LATC |= ui16State;
}


/*!
 * \brief
 * Turns the specified LEDs off.
 * 
 * \param _ui16ClearMask
 * Specifies the LED clear mask based on #HAL_LED_PIN_BV__0 to #HAL_LED_PIN_BV__B.
 * 
 * The specified LEDs are disabled; the others remain unchanged.
 * 
 * \remarks
 * - The LED abstraction layer needs to be initialized before being used.
 * - Using constant values is recommended due to function inlining.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_led_init | hal_led_switchOn | hal_led_set | hal_led_toggle
 */
void hal_led_switchOff(
	IN const uint16_t _ui16ClearMask
	) {

	uint16_t ui16State = 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__0 ? HAL_LED_PIN_MASK__0 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__1 ? HAL_LED_PIN_MASK__1 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__2 ? HAL_LED_PIN_MASK__2 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__3 ? HAL_LED_PIN_MASK__3 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__4 ? HAL_LED_PIN_MASK__4 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__5 ? HAL_LED_PIN_MASK__5 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__6 ? HAL_LED_PIN_MASK__6 : 0;
	ui16State |= _ui16ClearMask & HAL_LED_PIN_BV__7 ? HAL_LED_PIN_MASK__7 : 0;
	LATA &= ~ui16State;

	ui16State = 0;
	ui16State &= _ui16ClearMask & HAL_LED_PIN_BV__F ? HAL_LED_PIN_MASK__F : 0;
	ui16State &= _ui16ClearMask & HAL_LED_PIN_BV__B ? HAL_LED_PIN_MASK__B : 0;
	LATC &= ~ui16State;
}


#endif /* hal_led_h__ */
