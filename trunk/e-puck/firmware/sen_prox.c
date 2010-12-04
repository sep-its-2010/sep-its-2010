#include "sen_prox.h"


/*!
 * \brief
 * Holds the current IR proximity sensor values.
 * 
 * \see
 * sen_prox_init | sen_prox_getCurrent
 */
static volatile sen_prox_SData_t s_podData;


/*!
 * \brief
 * Initializes the IR proximity sensors module.
 * 
 * Performs ADC and timer 3 configurations.
 * 
 * \remarks
 * This function needs to be called before any other function from this module.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * hal_adc_init
 */
void sen_prox_init( void) {

}


/*!
 * \brief
 * Enables proximity sensor processing.
 * 
 * The timer 3 interrupt is configured to trigger the sensor A/D conversion.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 * 
 * \see
 * sen_prox_init | sen_prox_disable
 */
void sen_prox_enable( void) {

}


/*!
 * \brief
 * Disables proximity sensor processing.
 * 
 * Ongoing conversions are finished, then the processing is disabled.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized.
 * 
 * \see
 * sen_prox_init | sen_prox_disable
 */
void sen_prox_disable( void) {

}

/*!
 * \brief
 * Copies the current proximity sensor values.
 * 
 * \param _lppodData
 * Specifies the destination buffer.
 * 
 * The values are read from an internal buffer which is continuously updated by an interrupt.
 * 
 * \remarks
 * - This function is interrupt safe concerning interrupts from this module.
 * - The module needs to be initialized and enabled to get valid values.
 * 
 * \see
 * sen_prox_init | sen_prox_enable
 */
void sen_prox_getCurrent(
	OUT sen_prox_SData_t* const _lppodData
	) {

}
