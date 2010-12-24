#ifndef hal_adc_types_h__
#define hal_adc_types_h__

#include "common.h"


/*!
 * \brief
 * Specifies the available ADC channels.
 * 
 * \see
 * hal_adc_read
 */
typedef enum {
	HAL_ADC_CHANNEL__1 = 0, ///< Analog to digital converter channel 1.
	HAL_ADC_CHANNEL__2, ///< Analog to digital converter channel 2.
	HAL_ADC_CHANNEL__3, ///< Analog to digital converter channel 3.
	HAL_ADC_CHANNEL__4, ///< Analog to digital converter channel 4.
	HAL_ADC_CHANNEL__5, ///< Analog to digital converter channel 5.
	HAL_ADC_CHANNEL__6, ///< Analog to digital converter channel 6.
	HAL_ADC_CHANNEL__7, ///< Analog to digital converter channel 7.
	HAL_ADC_CHANNEL__8, ///< Analog to digital converter channel 8.
} hal_adc_EChannel;

#endif // hal_adc_types_h__
