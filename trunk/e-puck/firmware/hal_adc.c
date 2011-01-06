#include <p30f6014A.h>

#include "hal_adc.h"


enum {
	MIN_TAD_DURATION = 667 ///< Specifies the minimal ADC t_AD value. Check the reference manual for further info.
};


/*!
 * \brief
 * Initializes the ADC module.
 * 
 * The ADC is configured perform conversations by request.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * hal_adc_read
 */
void hal_adc_init( void) {

	ADCON1 = 0;
	ADCON2 = 0;
	ADCON3 = 0;
	ADCSSL = 0;

	// Sampling in integer format with automatic conversion
	ADCON1bits.SSRC = 7;

	// Configure ADC input sources: 2x digital I/O, 3x microphone, 3x acceleration, 8x IR 
	ADPCFG = 0x0003;

	// One channel -> one TAD
	ADCON3bits.SAMC = 1;

	// MIN_TAD_DURATION = Tad = ( 1 / FCY) * 0.5 * ( ADCS + 1)
	// -> ADCS + 1 = 2 * MIN_TAD_DURATION / TCY
	// ADC = 2 * MIN_TAD_DURATION / ( 1e9ns / FCY)   (w/o '- 1' to stay within timing bounds)
	ADCON3bits.ADCS = 2 * MIN_TAD_DURATION / ( 1000000000uLL / FCY);

	// Enable ADC
	ADCON1bits.ADON = true;
}


/*!
 * \brief
 * Reads an ADC channel.
 * 
 * \param _eChannel
 * Specifies the channel to be read.
 * 
 * \returns
 * The 12 bit sampled value in integer format
 * 
 * \remarks
 * The module needs to be initialized before it can be used.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_adc_init
 */
uint16_t hal_adc_read(
	IN const hal_adc_EChannel _eChannel
	) {

	ADCHS = _eChannel;
	ADCON1bits.SAMP = true;

	// A bug in the ADC module; ADCON1bits.DONE does not react fast enough and breaks the loop too early
	while( ADCON1bits.SAMP)
		;

 	while( !ADCON1bits.DONE)
 		;

	return ADCBUF0;
}
