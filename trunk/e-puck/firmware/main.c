#include "common.h"

#include <libpic30.h>

#include "hal_uart1.h"
#include "hal_led.h"
#include "hal_sel.h"
#include "com.h"

volatile uint16_t ui16Bla;

int main( void) {

	hal_led_init();
	hal_sel_init();
	com_init();

//	hal_uart1_puts( "SEP 2010 ITS ePuck & Android Project\n\r");

	for( uint16_t ui16 = 0; ; ui16++) {

 		__delay32( FCY / 10);
 
// 		hal_led_toggle( 0x80);
// 		if( ui16 & 0x1) {
// 			hal_led_toggle( 0x8);
// 		}
	}
}

