#include "common.h"

#include <libpic30.h>

#include "hal_uart1.h"
#include "hal_led.h"
#include "hal_sel.h"
#include "com.h"

#include "subs.h"
#include "subs_abyss.h"
#include "subs_calibration.h"
#include "subs_collision.h"
#include "subs_line.h"
#include "subs_movement.h"
#include "subs_node.h"

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
	com_init();

//	hal_uart1_puts( "SEP 2010 ITS e-puck & Android Project\n\r");

	for( ;;) {
		subs_run();
	}
}

