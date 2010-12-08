#include <string.h>

#include "hal_int.h"

#include "hal_nvm.h"

void hal_nvm_writeEEPROM(
	IN const hal_nvm_lpeep_t _lpeepDestination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	) {

	HAL_INT_ATOMIC_BLOCK() {
		const bool blBackupNVMWriteEnable = NVMCONbits.WREN;

		// Backup current PSV & configure for EEPROM access
		const uint8_t ui8BackupPSVPAG = PSVPAG;
		const bool blBackupPSVEnable = CORCONbits.PSV;
		PSVPAG = 0xFF;
		CORCONbits.PSV = true;

		// Holds the offset of the EEPROM row boundary
		const uint16_t ui16BaseOffset = (uint16_t)_lpeepDestination & ( HAL_NVM_EEPROM_WORDS_PER_ROW * HAL_NVM_EEPROM_BYTES_PER_WORD - 1);

		// Holds the current EEPROM row
		hal_nvm_lpeep_t lpeepCurRowAddr = _lpeepDestination - ui16BaseOffset;

		uint16_t ui16Written = 0;
		while( ui16Written < _ui16Length) {

			// Backup current row
			uint8_t ui8Image[HAL_NVM_EEPROM_WORDS_PER_ROW * HAL_NVM_EEPROM_BYTES_PER_WORD];
			memcpy( ui8Image, lpeepCurRowAddr, sizeof( ui8Image));

			// Update row; need to consider boundary alignment on first write
			uint16_t ui16ToBeWritten = _ui16Length - ui16Written;
			if( !ui16Written) {
				if( ui16ToBeWritten > sizeof( ui8Image) - ui16BaseOffset) {
					ui16ToBeWritten = sizeof( ui8Image) - ui16BaseOffset;
				}
				memcpy( ui8Image, (const uint8_t*)_lpvSource + ui16Written + ui16BaseOffset, sizeof( ui8Image) - ui16BaseOffset);
			} else {
				if( ui16ToBeWritten > sizeof( ui8Image)) {
					ui16ToBeWritten = sizeof( ui8Image);
				}
				memcpy( ui8Image, (const uint8_t*)_lpvSource + ui16Written, ui16ToBeWritten);
			}

			// Prepare erase
			NVMADRU = 0x7F;
			NVMADR = (uint16_t)_lpeepDestination;
			NVMCON = HAL_NVM_OPERATION__EEPROM_ERASE_ROW;

			// Load key sequence
			__asm volatile( "mov	#0x55,	w0");
			__asm volatile( "mov	w0,		NVMKEY");
			__asm volatile( "mov	#0xAA,	w0");
			__asm volatile( "mov	w0,		NVMKEY");

			// Erase & wait until finished
			NVMCONbits.WR = true;
			while( NVMCONbits.WR)
				;

			/* Prepare Table Page Register for loading up table write latches */
			TBLPAG = 0x7F;
			NVMCON = HAL_NVM_OPERATION__EEPROM_WRITE_ROW;

			// Fill table latch
			for( uint16_t ui16 = 0; ui16 < sizeof( ui8Image); ui16++) {
				__asm volatile( "tblwtl %0, [%1]" :  :  "r"( ui8Image[ui16]), "r"( (uint16_t)( lpeepCurRowAddr + ui16)));
			}

			// Load key sequence
			__asm volatile( "mov	#0x55,	w0");
			__asm volatile( "mov	w0,		NVMKEY");
			__asm volatile( "mov	#0xAA,	w0");
			__asm volatile( "mov	w0,		NVMKEY");

			// Program & wait until finished
			NVMCONbits.WR = true;
			while( NVMCONbits.WR)
				;

			// Advance to next row
			ui16Written += ui16ToBeWritten;
			lpeepCurRowAddr += sizeof( ui8Image);
		}

		NVMCONbits.WREN = blBackupNVMWriteEnable;
		PSVPAG = ui8BackupPSVPAG;
		CORCONbits.PSV = blBackupPSVEnable;
	}
}

void hal_nvm_readEEPROM(
	IN const hal_nvm_lpeep_t _addrSource,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	) {

	HAL_INT_ATOMIC_BLOCK() {
		const uint8_t ui8BackupPSVPAG = PSVPAG;
		const bool blBackupPSVEnable = CORCONbits.PSV;
		PSVPAG = 0xFF;
		CORCONbits.PSV = true;

		memcpy( _lpvDestination, _addrSource, _ui16Length);

		PSVPAG = ui8BackupPSVPAG;
		CORCONbits.PSV = blBackupPSVEnable;
	}
}
