#ifndef hal_led_h__
#define hal_led_h__

#include <p30f6014A.h>

#include "common.h"

#define HAL_LED_PIN__0 _LATA6
#define HAL_LED_PIN__1 _LATA7
#define HAL_LED_PIN__2 _LATA9
#define HAL_LED_PIN__3 _LATA12
#define HAL_LED_PIN__4 _LATA10
#define HAL_LED_PIN__5 _LATA13
#define HAL_LED_PIN__6 _LATA14
#define HAL_LED_PIN__7 _LATA15
#define HAL_LED_PIN__F _LATC1
#define HAL_LED_PIN__B _LATC2

#define HAL_LED_PIN_DIR__0 _TRISA6
#define HAL_LED_PIN_DIR__1 _TRISA7
#define HAL_LED_PIN_DIR__2 _TRISA9
#define HAL_LED_PIN_DIR__3 _TRISA12
#define HAL_LED_PIN_DIR__4 _TRISA10
#define HAL_LED_PIN_DIR__5 _TRISA13
#define HAL_LED_PIN_DIR__6 _TRISA14
#define HAL_LED_PIN_DIR__7 _TRISA15
#define HAL_LED_PIN_DIR__F _TRISC1
#define HAL_LED_PIN_DIR__B _TRISC2

enum {
	HAL_LED_PIN_BV__0 = 1 << 0,
	HAL_LED_PIN_BV__1 = 1 << 1,
	HAL_LED_PIN_BV__2 = 1 << 2,
	HAL_LED_PIN_BV__3 = 1 << 3,
	HAL_LED_PIN_BV__4 = 1 << 4,
	HAL_LED_PIN_BV__5 = 1 << 5,
	HAL_LED_PIN_BV__6 = 1 << 6,
	HAL_LED_PIN_BV__7 = 1 << 7,
	HAL_LED_PIN_BV__F = 1 << 8,
	HAL_LED_PIN_BV__B = 1 << 9,

	HAL_LED_PIN_MASK__0 = 1 << 6,
	HAL_LED_PIN_MASK__1 = 1 << 7,
	HAL_LED_PIN_MASK__2 = 1 << 9,
	HAL_LED_PIN_MASK__3 = 1 << 12,
	HAL_LED_PIN_MASK__4 = 1 << 10,
	HAL_LED_PIN_MASK__5 = 1 << 13,
	HAL_LED_PIN_MASK__6 = 1 << 14,
	HAL_LED_PIN_MASK__7 = 1 << 15,
	HAL_LED_PIN_MASK__CIRCULAR = HAL_LED_PIN_MASK__0 | HAL_LED_PIN_MASK__1 | HAL_LED_PIN_MASK__2 | HAL_LED_PIN_MASK__3 |
								 HAL_LED_PIN_MASK__4 | HAL_LED_PIN_MASK__5 | HAL_LED_PIN_MASK__6 | HAL_LED_PIN_MASK__7,

	HAL_LED_PIN_MASK__F = 1 << 1,
	HAL_LED_PIN_MASK__B = 1 << 2,
	HAL_LED_PIN_MASK__CHASSIS = HAL_LED_PIN_MASK__F | HAL_LED_PIN_MASK__B
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


void hal_led_init( void) {

	LATA &= ~HAL_LED_PIN_MASK__CIRCULAR;
	LATC &= ~HAL_LED_PIN_MASK__CHASSIS;

	hal_led_set( 0);
}


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
