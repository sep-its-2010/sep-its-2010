#ifndef hal_nvm_h__
#define hal_nvm_h__

#include "common.h"

#include "hal_nvm_types.h"

enum {
	HAL_NVM_FLASH_WORDS_PER_ROW = 32,
	HAL_NVM_FLASH_BYTES_PER_WORD = 3,
	HAL_NVM_EEPROM_WORDS_PER_ROW = 16,
	HAL_NVM_EEPROM_BYTES_PER_WORD = 2
};


void hal_nvm_writeEEPROM(
	IN const hal_nvm_lpeep_t _lpeepDestination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	);


void hal_nvm_readEEPROM(
	IN const hal_nvm_lpeep_t _addrSource,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	);

#endif // hal_nvm_h__
