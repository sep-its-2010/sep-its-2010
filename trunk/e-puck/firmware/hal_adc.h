#ifndef hal_adc_h__
#define hal_adc_h__

#include "common.h"

#include "hal_adc_types.h"

void hal_adc_init( void);

uint8_t hal_adc_read(
	IN const hal_adc_EChannel _eChannel
	);

#endif /* hal_adc_h__ */
