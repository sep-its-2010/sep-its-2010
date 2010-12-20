#ifndef hal_nvm_h__
#define hal_nvm_h__

#include "common.h"

#include <libpic30.h>


bool hal_nvm_writeEEPROM(
	IN const _prog_addressT _addrDestination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	);


bool hal_nvm_readEEPROM(
	IN const _prog_addressT _addrSource,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	);


#endif // hal_nvm_h__
