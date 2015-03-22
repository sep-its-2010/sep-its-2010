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
	HAL_ADC_CHANNEL__0 = 0, ///< Analog to digital converter channel 0 (unused).
	HAL_ADC_CHANNEL__1, ///< Analog to digital converter channel 1 (unused).
	HAL_ADC_CHANNEL__2, ///< Analog to digital converter channel for microphone 1.
	HAL_ADC_CHANNEL__MIC_1 = HAL_ADC_CHANNEL__2, ///< \see HAL_ADC_CHANNEL__2
	HAL_ADC_CHANNEL__3, ///< Analog to digital converter channel for microphone 2.
	HAL_ADC_CHANNEL__MIC_2 = HAL_ADC_CHANNEL__3, ///< \see HAL_ADC_CHANNEL__3
	HAL_ADC_CHANNEL__4, ///< Analog to digital converter channel for microphone 3.
	HAL_ADC_CHANNEL__MIC_3 = HAL_ADC_CHANNEL__4, ///< \see HAL_ADC_CHANNEL__4
	HAL_ADC_CHANNEL__5, ///< Analog to digital converter channel for acceleration sensor x axis.
	HAL_ADC_CHANNEL__ACCEL_X = HAL_ADC_CHANNEL__5, ///< \see HAL_ADC_CHANNEL__5
	HAL_ADC_CHANNEL__6, ///< Analog to digital converter channel for acceleration sensor y axis.
	HAL_ADC_CHANNEL__ACCEL_Y = HAL_ADC_CHANNEL__6, ///< \see HAL_ADC_CHANNEL__6
	HAL_ADC_CHANNEL__7, ///< Analog to digital converter channel for acceleration sensor z axis.
	HAL_ADC_CHANNEL__ACCEL_Z = HAL_ADC_CHANNEL__7, ///< \see HAL_ADC_CHANNEL__7
	HAL_ADC_CHANNEL__8, ///< Analog to digital converter channel for IR sensor 1.
	HAL_ADC_CHANNEL__IR_1 = HAL_ADC_CHANNEL__8, ///< \see HAL_ADC_CHANNEL__8
	HAL_ADC_CHANNEL__9, ///< Analog to digital converter channel for IR sensor 2.
	HAL_ADC_CHANNEL__IR_2 = HAL_ADC_CHANNEL__9, ///< \see HAL_ADC_CHANNEL__9
	HAL_ADC_CHANNEL__10, ///< Analog to digital converter channel for IR sensor 3.
	HAL_ADC_CHANNEL__IR_3 = HAL_ADC_CHANNEL__10, ///< \see HAL_ADC_CHANNEL__10
	HAL_ADC_CHANNEL__11, ///< Analog to digital converter channel for IR sensor 4.
	HAL_ADC_CHANNEL__IR_4 = HAL_ADC_CHANNEL__11, ///< \see HAL_ADC_CHANNEL__11
	HAL_ADC_CHANNEL__12, ///< Analog to digital converter channel for IR sensor 5.
	HAL_ADC_CHANNEL__IR_5 = HAL_ADC_CHANNEL__12, ///< \see HAL_ADC_CHANNEL__12
	HAL_ADC_CHANNEL__13, ///< Analog to digital converter channel for IR sensor 6.
	HAL_ADC_CHANNEL__IR_6 = HAL_ADC_CHANNEL__13, ///< \see HAL_ADC_CHANNEL__13
	HAL_ADC_CHANNEL__14, ///< Analog to digital converter channel for IR sensor 7.
	HAL_ADC_CHANNEL__IR_7 = HAL_ADC_CHANNEL__14, ///< \see HAL_ADC_CHANNEL__14
	HAL_ADC_CHANNEL__15, ///< Analog to digital converter channel for IR sensor 8.
	HAL_ADC_CHANNEL__IR_8 = HAL_ADC_CHANNEL__15, ///< \see HAL_ADC_CHANNEL__15
} hal_adc_EChannel;

#endif // hal_adc_types_h__
