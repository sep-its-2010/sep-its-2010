#include "common.h"

#include "com.h"
#include "hal_adc.h"
#include "hal_i2c.h"
#include "hal_led.h"
#include "hal_motors.h"
#include "hal_rtc.h"
#include "hal_sel.h"
#include "hal_uart1.h"
#include "sen_line.h"
#include "sen_prox.h"

#include "conquest.h"


/*!
 * \brief
 * Specifies the baud rate of the primary UART.
 * 
 * The primary UART is used for communicating with the bluetooth module.
 * Thus, the baud rate needs to match the baud rate of the bluetooth module.
 * 
 * \see
 * UART1_BAUDRATE_DIVISOR
 */
#define UART1_BAUDRATE 115200


/*!
 * \brief
 * Specifies the baud rate divisor of the primary UART.
 * 
 * The calculation is based on the formula of the dsPIC30F6014A data sheet.
 * It requires that \c FCY and \c UART1_BAUDRATE are defined correctly.
 * 
 * \see
 * main | hal_uart1_configure
 */
#define UART1_BAUDRATE_DIVISOR (uint16_t)( FCY / ( 16 * UART1_BAUDRATE) - 1)


enum {
	UART1_TX_BUFFER_SIZE = 128, ///< Specifies the amount of transmitter buffer space in bytes.
	UART1_RX_BUFFER_SIZE = 128, ///< Specifies the amount of receiver buffer space in bytes.
	ACCELERATION_FREQUENCY = 10, ///< Specifies the acceleration event frequency in Hz.
	RTC_FREQENCY = 1000, ///< Specifies the real time clock frequency in Hz.
	SUBS_FREQUENCY = 100, ///< Specifies the subsumption layer frequency in Hz.
	BLINK_FREQUENCY = 2, ///< Specifies the toggle frequency of the blink event.
	I2C_DIVISOR = 150 ///< Specifies the baud rate divisor for the hal_i2c module.
};


void cbBlinker(
	IN const hal_rtc_handle_t _hEvent
	);
void cbBlinker(
	IN const hal_rtc_handle_t UNUSED _hEvent
	) {

	hal_led_toggle( HAL_LED_PIN_BV__B);
	if( hal_uart1_hasRxOverflow()) {
		hal_led_switchOn( HAL_LED_PIN_BV__F);
	}
}


int main( void) {

	hal_sel_init();
	hal_led_init();
	hal_adc_init();

	// Configuring the primary UART for the bluetooth module
	static uint8_t s_aui8RxBufferSpace[UART1_RX_BUFFER_SIZE];
	ringbuf_init( hal_uart1_getRxRingBuffer(), s_aui8RxBufferSpace, sizeof( s_aui8RxBufferSpace));
	static uint8_t s_aui8TxBufferSpace[UART1_TX_BUFFER_SIZE];
	ringbuf_init( hal_uart1_getTxRingBuffer(), s_aui8TxBufferSpace, sizeof( s_aui8TxBufferSpace));
	hal_uart1_configure( HAL_UART_CONFIG__8N1, UART1_BAUDRATE_DIVISOR);
	hal_uart1_enable( true, NULL);
	com_init( conquest_cbConnection);

	hal_i2c_init( I2C_DIVISOR, true);

	hal_rtc_init( FCY / 256 / RTC_FREQENCY);

	hal_motors_init( RTC_FREQENCY / ACCELERATION_FREQUENCY);

	// Proximity sensors with 1kHz cycles
	sen_prox_init( 1);
	sen_prox_enable();

	// Perform a default calibration which does not transform the raw data at all.
 	const sen_line_SData_t podDefaultBlackLevel = { { SEN_LINE_NOMINAL_BLACK_LEVEL } };
 	const sen_line_SData_t podDefaultWhiteLevel = { { SEN_LINE_NOMINAL_WHITE_LEVEL } };
 	sen_line_calibrate( &podDefaultBlackLevel, &podDefaultWhiteLevel);

	hal_rtc_register( cbBlinker, RTC_FREQENCY / BLINK_FREQUENCY, true);

	conquest_init();

	for( ;;) {
		com_processIncoming();
	}
}

