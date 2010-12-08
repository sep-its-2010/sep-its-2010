#ifndef hal_nvm_types_h__
#define hal_nvm_types_h__

#include "common.h"

typedef enum {
	HAL_NVM_OPERATION__FLASH_ERASE_ROW = 0x4041,
	HAL_NVM_OPERATION__FLASH_WRITE_ROW = 0x4001,
	HAL_NVM_OPERATION__EEPROM_ERASE_WORD = 0x4044,
	HAL_NVM_OPERATION__EEPROM_WRITE_WORD = 0x4004,
	HAL_NVM_OPERATION__EEPROM_ERASE_ROW = 0x4045,
	HAL_NVM_OPERATION__EEPROM_WRITE_ROW = 0x4005,
	HAL_NVM_OPERATION__EEPROM_ERASE_ALL = 0x4046,
	HAL_NVM_OPERATION__CONFIG_WRITE = 0x4008
} hal_nvm_EOperation_t;

typedef uint8_t* hal_nvm_lpeep_t;

#endif // hal_nvm_types_h__
