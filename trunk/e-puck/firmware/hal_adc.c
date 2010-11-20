#include "hal_adc.h"


/*!
 * \brief
 * Initializes the ADC module.
 * 
 * The ADC is configured perform conversations by request.
 * 
 * \remarks
 * The module needs to be initialized before it can be used.
 *
 * \warning
 * This function is not interrupt safe.
 */
void hal_adc_init( void) {

}


/*!
 * \brief
 * Reads an ADC channel.
 * 
 * \param _eChannel
 * Specifies the channel to be read.
 * 
 * \returns
 * The read value in 8 bits.
 * 
 * \remarks
 * The module needs to be initialized before it can be used.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_adc_init
 */
uint8_t hal_adc_read(
	IN const hal_adc_EChannel _eChannel
	) {

}
