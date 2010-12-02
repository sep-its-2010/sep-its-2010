#include "common.h"

#include <libpic30.h>
#include <stdio.h>

#include "hal_uart1.h"
#include "hal_led.h"
#include "hal_sel.h"
#include "hal_i2c.h"
#include "sen_line.h"
#include "com.h"

#include "subs.h"
#include "subs_abyss.h"
#include "subs_calibration.h"
#include "subs_collision.h"
#include "subs_line.h"
#include "subs_movement.h"
#include "subs_node.h"

#include "hal_motors.h"
#include "hal_int.h"

int main( void) {

	subs_init();
	subs_register( subs_abyss_run, NULL, 0xFF);
 	subs_register( subs_calibration_run, subs_calibration_reset, 0xEF);
	subs_register( subs_collision_run, subs_collision_reset, 0xDF);
	subs_register( subs_movement_run, subs_movement_reset, 0xCF);
	subs_register( subs_movement_run, subs_movement_reset, 0xBF);
	subs_register( subs_node_run, subs_node_reset, 0xAF);
	subs_register( subs_line_run, subs_line_reset, 0x9F);
	subs_reset();

	hal_sel_init();
	hal_led_init();
	hal_i2c_init( HAL_I2C_MAX_BAUDRATE_DIVISOR);
	com_init();

	volatile int i = 20;
	HAL_INT_ATOMIC_BLOCK() {
		i = 10;
	}

	hal_motors_init();

	hal_uart1_puts( "SEP 2010 ITS e-puck & Android Project\r\n");

	for( ;;) {
		sen_line_SData_t podData;
		sen_line_read( &podData);
		char buffer[50];
		sprintf( buffer, "c[0]:%d  c[1]:%d  c[2]:%d\r\n", podData.aui16Data[0], podData.aui16Data[1], podData.aui16Data[2]);
		hal_uart1_puts( buffer);

		__delay32( FCY);

		/*
		switch( hal_uart1_getch()) {
			case 'w': {
				hal_motors_setSpeed( 1000, 0);
				break;
			}
			case 'a': {
				hal_motors_setSpeed( 0, -1000);
				hal_motors_setSteps( 0);
				while( hal_motors_getStepsLeft() < 329 && hal_motors_getStepsRight() < 329)
					;
				while( hal_uart1_hasRxData()) {
					hal_uart1_getch();
				}
				hal_motors_setSpeed( 0, 0);
				break;
			}
			case 'd': {
				hal_motors_setSpeed( 0, 1000);
				hal_motors_setSteps( 0);
				while( hal_motors_getStepsLeft() < 329 && hal_motors_getStepsRight() < 329)
					;
				while( hal_uart1_hasRxData()) {
					hal_uart1_getch();
				}
				hal_motors_setSpeed( 0, 0);
				break;
			}
			case 's': {
				hal_motors_setSpeed( 0, 0);
				break;
			}
			case 'x': {
				hal_motors_setSpeed( -1000, 0);
				break;
			}
			default: {

			}
		}
		*/
//		subs_run();
	}
}

