#ifndef hal_nvm_h__
#define hal_nvm_h__

#include "common.h"


bool hal_nvm_writeEEPROM(
	IN const uint16_t _ui16Destination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	);


bool hal_nvm_readEEPROM(
	IN const uint16_t _ui16Source,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	);


#endif // hal_nvm_h__
