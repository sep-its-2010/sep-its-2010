#ifndef hal_i2c_h__
#define hal_i2c_h__

#include "common.h"

enum {
	HAL_I2C_TIMEOUT_TICKS = 0xFFFF
};

void hal_i2c_init(
	IN const uint16_t _ui16BaudRateDiv
	);

bool hal_i2c_write(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t* const _lpui8Data,
	IN const uint16_t _ui16Length
	);

bool hal_i2c_writeRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register,
	IN const uint8_t _ui8Data
	);

bool hal_i2c_read(
	IN const uint8_t _ui8SlaveAddress,
	OUT uint8_t* const _lpui8Data,
	IN const uint16_t _ui16Length
	);

int16_t hal_i2c_readRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register
	);

#endif /* hal_i2c_h__ */
