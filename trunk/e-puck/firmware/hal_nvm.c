#include <string.h>

#include "hal_int.h"

#include "hal_nvm.h"

enum {
	PSV_ADDRESS_ENABLE = 0x8000,
	EEPROM_PSV_ADDR_UPPER = 0xFF,
	EEPROM_PSV_ADDR_LOWER_OFFSET = 0x7000,
	EEPROM_NVM_ADDR_UPPER = 0x7F,
	EEPROM_NVM_ADDR_LOWER_OFFSET = 0xF000
};


typedef enum {
	OPERATION__FLASH_ERASE_ROW = 0x4041,
	OPERATION__FLASH_WRITE_ROW = 0x4001,
	OPERATION__EEPROM_ERASE_WORD = 0x4044,
	OPERATION__EEPROM_WRITE_WORD = 0x4004,
	OPERATION__EEPROM_ERASE_ROW = 0x4045,
	OPERATION__EEPROM_WRITE_ROW = 0x4005,
	OPERATION__EEPROM_ERASE_ALL = 0x4046,
	OPERATION__CONFIG_WRITE = 0x4008
} EOperation_t;



void hal_nvm_writeEEPROM(
	IN const uintptr_t _uipDestination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	) {

	HAL_INT_ATOMIC_BLOCK() {
		const bool blBackupNVMWriteEnable = NVMCONbits.WREN;

		// Backup current PSV & configure for EEPROM access
		const uint8_t ui8BackupPSVPAG = PSVPAG;
		const bool blBackupPSVEnable = CORCONbits.PSV;
		PSVPAG = EEPROM_PSV_ADDR_UPPER;
		CORCONbits.PSV = true;

		// Holds the offset of the EEPROM row boundary
		const uintptr_t uipBaseOffset = _uipDestination & ( HAL_NVM_EEPROM_WORDS_PER_ROW * HAL_NVM_EEPROM_BYTES_PER_WORD - 1);

		// Holds the current EEPROM row
		uintptr_t uipCurRowAddr = _uipDestination - uipBaseOffset;

		uint16_t ui16Written = 0;
		while( ui16Written < _ui16Length) {

			// Backup current row
			uint16_t aui16Image[HAL_NVM_EEPROM_WORDS_PER_ROW];
			const uintptr_t uipEffectiveReadAddress = ( uipCurRowAddr + EEPROM_PSV_ADDR_LOWER_OFFSET) | PSV_ADDRESS_ENABLE;
			memcpy( aui16Image, (void*)uipEffectiveReadAddress, sizeof( aui16Image));

			// Update row; need to consider boundary alignment on first write
			uint16_t ui16ToBeWritten = _ui16Length - ui16Written;
			if( !ui16Written) {
				if( ui16ToBeWritten > sizeof( aui16Image) - uipBaseOffset) {
					ui16ToBeWritten = sizeof( aui16Image) - uipBaseOffset;
				}
				memcpy( (uint8_t*)aui16Image + uipBaseOffset, (const uint8_t*)_lpvSource, ui16ToBeWritten);
			} else {
				if( ui16ToBeWritten > sizeof( aui16Image)) {
					ui16ToBeWritten = sizeof( aui16Image);
				}
				memcpy( aui16Image, (const uint8_t*)_lpvSource + ui16Written, ui16ToBeWritten);
			}

			// Prepare erase
			NVMCON = OPERATION__EEPROM_ERASE_ROW;
			NVMADRU = EEPROM_NVM_ADDR_UPPER;
			NVMADR = EEPROM_NVM_ADDR_LOWER_OFFSET + _uipDestination;

			// Load key sequence
			__asm volatile( "mov	#0x55,	w0");
			__asm volatile( "mov	w0,		NVMKEY");
			__asm volatile( "mov	#0xAA,	w0");
			__asm volatile( "mov	w0,		NVMKEY");

			// Erase & wait until finished
			NVMCONbits.WR = true;
			while( NVMCONbits.WR)
				;
/*
			// Fill table latch
			TBLPAG = EEPROM_NVM_ADDR_UPPER;
			for( uintptr_t uip = 0; uip < HAL_NVM_EEPROM_WORDS_PER_ROW; uip++) {
				const uintptr_t uipWordAddress = uipCurRowAddr + uip * 2 + EEPROM_PSV_ADDR_LOWER_OFFSET;
				__asm volatile( "tblwtl %0, [%1]" :  :  "r"( aui16Image[uip]), "r"( uipWordAddress));
			}

			NVMCON = OPERATION__EEPROM_WRITE_ROW;

			// Load key sequence
			__asm volatile( "mov	#0x55,	w0");
			__asm volatile( "mov	w0,		NVMKEY");
			__asm volatile( "mov	#0xAA,	w0");
			__asm volatile( "mov	w0,		NVMKEY");

			// Program & wait until finished
			NVMCONbits.WR = true;
			while( NVMCONbits.WR)
				;
*/
			// Advance to next row
			ui16Written += ui16ToBeWritten;
			uipCurRowAddr += sizeof( aui16Image);
		}

		NVMCONbits.WREN = blBackupNVMWriteEnable;
		PSVPAG = ui8BackupPSVPAG;
		CORCONbits.PSV = blBackupPSVEnable;
	}
}


void hal_nvm_readEEPROM(
	IN const uintptr_t _uipSource,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	) {

	HAL_INT_ATOMIC_BLOCK() {
		const uint8_t ui8BackupPSVPAG = PSVPAG;
		const bool blBackupPSVEnable = CORCONbits.PSV;
		PSVPAG = EEPROM_PSV_ADDR_UPPER;
		CORCONbits.PSV = true;

		const uintptr_t uipEffectiveAddress = ( _uipSource + EEPROM_PSV_ADDR_LOWER_OFFSET) | PSV_ADDRESS_ENABLE;
		memcpy( _lpvDestination, (const void*)uipEffectiveAddress, _ui16Length);

		PSVPAG = ui8BackupPSVPAG;
		CORCONbits.PSV = blBackupPSVEnable;
	}
}
