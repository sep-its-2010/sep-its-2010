#include <p30f6014A.h>
#include <libpic30.h>

#include "common.h"

#include "hal_led.h"


void _ISR NO_AUTO_PSV _DefaultInterrupt( void);
void _ISR NO_AUTO_PSV _OscillatorFail( void);
void _ISR NO_AUTO_PSV _AddressError( void);
void _ISR NO_AUTO_PSV _StackError( void);
void _ISR NO_AUTO_PSV _MathError( void);


/*!
 * \brief
 * Catches any enabled interrupt without service routine and toggles the front ring LED continuously.
 * 
 * This function is intended as visual info only.
 * 
 * \see
 * _OscillatorFail | _AddressError | _StackError | _MathError
 */
void _DefaultInterrupt( void) {

	for( ;;) {
		hal_led_toggle( 0x1);
		__delay32( FCY / 4);
	}
}


/*!
 * \brief
 * Catches the oscillator error trap and toggles the top left ring LED continuously.
 * 
 * This function is intended as visual info only.
 * 
 * \see
 * _DefaultInterrupt | _AddressError | _StackError | _MathError
 */
void _OscillatorFail( void) {

	INTCON1bits.OSCFAIL = 0;
	for( ;;) {
		hal_led_toggle( 0x2);
		__delay32( FCY / 4);
	}
}


/*!
 * \brief
 * Catches the address error trap and toggles the top right ring LED continuously.
 * 
 * This function is intended as visual info only.
 * 
 * \see
 * _DefaultInterrupt | _OscillatorFail | _StackError | _MathError
 */
void _AddressError( void) {

	INTCON1bits.ADDRERR = 0;
	for( ;;) {
		hal_led_toggle( 0x8);
		__delay32( FCY / 4);
	}
}


/*!
 * \brief
 * Catches the stack error trap and toggles the bottom right ring LED continuously.
 * 
 * This function is intended as visual info only.
 * 
 * \see
 * _DefaultInterrupt | _OscillatorFail | _AddressError | _MathError
 */
void _StackError( void) {

	INTCON1bits.STKERR = 0;
	for( ;;) {
		hal_led_toggle( 0x20);
		__delay32( FCY / 4);
	}
}


/*!
 * \brief
 * Catches the math error trap and toggles the bottom left ring LED continuously.
 * 
 * This function is intended as visual info only.
 * 
 * \see
 * _DefaultInterrupt | _OscillatorFail | _AddressError | _StackError
 */
void _MathError( void) {

	INTCON1bits.MATHERR = 0;
	for( ;;) {
		hal_led_toggle( 0x80);
		__delay32( FCY / 4);
	}
}
