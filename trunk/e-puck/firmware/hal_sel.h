#ifndef hal_sel_h__
#define hal_sel_h__

#include <p30f6014A.h>

#include "common.h"

#define HAL_SEL_PIN__0 _RG6 ///< e-puck selector pin 0.
#define HAL_SEL_PIN__1 _RG7 ///< e-puck selector pin 1.
#define HAL_SEL_PIN__2 _RG8 ///< e-puck selector pin 2.
#define HAL_SEL_PIN__3 _RG9 ///< e-puck selector pin 3.

#define HAL_SEL_PIN_DIR__0 _TRISG6 ///< e-puck selector pin 0 direction.
#define HAL_SEL_PIN_DIR__1 _TRISG7 ///< e-puck selector pin 1 direction.
#define HAL_SEL_PIN_DIR__2 _TRISG8 ///< e-puck selector pin 2 direction.
#define HAL_SEL_PIN_DIR__3 _TRISG9 ///< e-puck selector pin 3 direction.

enum {
	HAL_SEL_PIN_MASK = ( 1 << 6) | ( 1 << 7) | ( 1 << 8) | ( 1 << 9), ///< Selector port bit mask.
	HAL_SEL_DATA_OFFSET = 6 ///< Data offset within the selector port.
};

static inline void hal_sel_init( void);

static inline uint8_t hal_sel_getPosition( void);

/*!
 * \brief
 * Initializes the selector port pins.
 * 
 * \see
 * hal_sel_getPosition
 */
void hal_sel_init( void) {

	TRISG |= HAL_SEL_PIN_MASK;
}

/*!
 * \brief
 * Gets the current selector position.
 * 
 * \returns
 * A value between 0x00 and 0x0F which represents the e-puck selector position labeling.
 * 
 * \remarks
 * The selector abstraction layer needs to be initialized.
 * 
 * \see
 * hal_sel_init
 */
uint8_t hal_sel_getPosition( void) {

	return ( PORTG & HAL_SEL_PIN_MASK) >> HAL_SEL_DATA_OFFSET;
}

#endif /* hal_sel_h__ */
