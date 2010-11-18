#ifndef hal_sel_h__
#define hal_sel_h__

#define HAL_SEL_PIN__0 _RG6
#define HAL_SEL_PIN__1 _RG7
#define HAL_SEL_PIN__2 _RG8
#define HAL_SEL_PIN__3 _RG9

#define HAL_SEL_PIN_DIR__0 _TRISG6
#define HAL_SEL_PIN_DIR__1 _TRISG7
#define HAL_SEL_PIN_DIR__2 _TRISG8
#define HAL_SEL_PIN_DIR__3 _TRISG9

enum {
	HAL_SEL_PIN_MASK = ( 1 << 6) | ( 1 << 7) | ( 1 << 8) | ( 1 << 9),
	HAL_SEL_DATA_OFFSET = 6
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
 * A value between 0x00 and 0x0F which represents the epuck selector position labeling.
 * 
 * \remarks
 * The selector abstraction layer needs to be initialized before being used.
 * 
 * \see
 * hal_sel_init
 */
uint8_t hal_sel_getPosition( void) {

	return ( PORTG & HAL_SEL_PIN_MASK) >> HAL_SEL_DATA_OFFSET;
}

#endif /* hal_sel_h__ */
