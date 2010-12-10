#include "common.h"

#include "hal_led.h"
#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_rtc.h"
#include "hal_i2c.h"
#include "hal_uart1.h"
#include "com.h"

#include "hal_nvm.h"

#include "subs.h"
#include "subs_abyss.h"
#include "subs_calibration.h"
#include "subs_collision.h"
#include "subs_line.h"
#include "subs_movement.h"
#include "subs_node.h"

#include "conquest.h"


/*!
 * \brief
 * Holds the receiver ring buffer storage.
 * 
 * \see
 * s_aui8TxBufferSpace
 */
static uint8_t s_aui8RxBufferSpace[UART1_RX_BUFFER_SIZE];


/*!
 * \brief
 * Holds the transmitter ring buffer storage.
 * 
 * \see
 * s_aui8RxBufferSpace
 */
static uint8_t s_aui8TxBufferSpace[UART1_TX_BUFFER_SIZE];



void cbSubsumptionEvent(
	IN const hal_rtc_handle_t _hEvent
	);
void cbSubsumptionEvent(
	IN const hal_rtc_handle_t UNUSED _hEvent
	) {

	subs_run();
}


void cbBlinker(
	IN const hal_rtc_handle_t _hEvent
	);
void cbBlinker(
	IN const hal_rtc_handle_t UNUSED _hEvent
	) {

	hal_led_toggle( HAL_LED_PIN_BV__B);
}


int main( void) {

	subs_init();
	subs_register( subs_calibration_run, subs_calibration_reset, 0xFF);
	subs_register( subs_abyss_run, subs_abyss_reset, 0xEF);
// 	subs_register( subs_collision_run, subs_collision_reset, 0xDF);
// 	subs_register( subs_movement_run, subs_movement_reset, 0xCF);
// 	subs_register( subs_movement_run, subs_movement_reset, 0xBF);
// 	subs_register( subs_node_run, subs_node_reset, 0xAF);
// 	subs_register( subs_line_run, subs_line_reset, 0x9F);
	subs_reset();

	hal_sel_init();
	hal_led_init();
	hal_motors_init();

	hal_i2c_init( 150, false);

	// Configuring the primary UART for the bluetooth module
	ringbuf_init( hal_uart1_getRxRingBuffer(), s_aui8RxBufferSpace, sizeof( s_aui8RxBufferSpace));
	ringbuf_init( hal_uart1_getTxRingBuffer(), s_aui8TxBufferSpace, sizeof( s_aui8TxBufferSpace));
	hal_uart1_configure( HAL_UART_CONFIG__8N1, UART1_BAUDRATE_DIVISOR);
	hal_uart1_enable( true);
//	hal_uart1_puts( "SEP 2010 ITS e-puck & Android Project\r\n");

//	hal_motors_setSpeed( 800, 0);

	uint16_t ui16Test = 9;
	char cstrTest[] = "This is an EEPROM test string.";
	hal_nvm_writeEEPROM( ui16Test, cstrTest, sizeof( cstrTest));
	hal_nvm_readEEPROM( ui16Test, cstrTest, sizeof( cstrTest));
	hal_uart1_puts( cstrTest);

	for( ;;)
		;


	// Real time clock with 100Hz
	hal_rtc_init( FCY / 256 / 100);
	hal_rtc_register( cbSubsumptionEvent, 1, true);
	hal_rtc_register( cbBlinker, 50, true);

	com_init();
//	com_register( cbDemoMessageHandler);
	for( ;;) {
		com_processIncoming();
	}
}

