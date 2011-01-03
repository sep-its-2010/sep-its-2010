#include <string.h>

#include "hal_int.h"

#include "hal_nvm.h"

// Porting notice: check the constants which follow this guard
#if( !defined( __dsPIC30F6014A__))
#	error "Target not supported! (required: dsPIC30F6014A)"
#endif


enum {
	EEPROM_SIZE = 4096, ///< Specifies the size in bytes of the internal EEPROM.
	EEPROM_WORDS_PER_ROW = _EE_ROW / _EE_WORD, ///< Specifies the amount of EEPROM words within one EEPROM row.
	EEPROM_BYTES_PER_WORD = _EE_WORD, ///< Specifies the amount of bytes within one EEPROM word.
	EEPROM_PSV_ADDR_UPPER = 0xFF, ///< Specifies the upper address (8 bit) for the EEPROM PSV window.
	EEPROM_PSV_ADDR_LOWER_OFFSET = 0x7000, ///< Specifies the offset within the lower address (15 bit) for the EEPROM PSV window.
	EEPROM_NVM_ADDR_UPPER = 0x7F, ///< Specifies the upper address (7 bit) for the EEPROM NVM page.
	EEPROM_NVM_ADDR_LOWER_OFFSET = 0xF000, ///< Specifies the offset within the lower address (16 bit) for the EEPROM NVM page.
	PSV_ADDRESS_ENABLE = 0x8000 ///< Specifies the address mask which needs to be set to read from the PSV window.
};


/*!
 * \brief
 * Write brief comment for  here.
 * 
 * Write detailed description for  here.
 * 
 * \remarks
 * Write remarks for  here.
 * 
 * \see
 * executeOperation
 */
typedef enum {
	OPERATION__FLASH_ERASE_ROW = _FLASH_ERASE_CODE, ///< Specifies a flash memory row erase operation.
	OPERATION__FLASH_WRITE_ROW = _FLASH_WRITE_ROW_CODE, ///< Specifies a flash memory row write operation.
	OPERATION__EEPROM_ERASE_WORD = _EE_ERASE_WORD_CODE, ///< Specifies an EEPROM word erase operation.
	OPERATION__EEPROM_WRITE_WORD = _EE_WORD_WRITE_CODE, ///< Specifies an EEPROM word write operation.
	OPERATION__EEPROM_ERASE_ROW = _EE_ERASE_ROW_CODE, ///< Specifies an EEPROM row erase operation.
	OPERATION__EEPROM_WRITE_ROW = _EE_ROW_WRITE_CODE, ///< Specifies an EEPROM row write operation.
	OPERATION__EEPROM_ERASE_ALL = _EE_ERASE_ALL_CODE, ///< Specifies a full EEPROM erase operation.
	OPERATION__CONFIG_WRITE = 0x4008 ///< Specifies a configuration write operation.
} EOperation_t;


static inline void executeOperation(
	IN const EOperation_t _eOperation
	);

/*!
 * \brief
 * Performs erase or write operations on the non-volatile memory.
 * 
 * \param _eOperation
 * Specifies the operation to be executed.
 * 
 * The user needs to prepare the registers and data latches required by the specified operation.
 * The function blocks until the operation finished.
 * 
 * \remarks
 * - The function does not check for ongoing NVM operations.
 * - The NVM write mode is not disabled after the operation is performed.
 * - The NVM error state is not checked.
 *
 * \warning
 * This function may not be preempted. This is crucial for the operation to execute correctly.
 * 
 * \see
 * hal_nvm_writeEEPROM | hal_nvm_readEEPROM
 */
void executeOperation(
	IN const EOperation_t _eOperation
	) {

	NVMCON = _eOperation;

	// Load key sequence
	__asm volatile( "mov	#0x55,	w0");
	__asm volatile( "mov	w0,		NVMKEY");
	__asm volatile( "mov	#0xAA,	w0");
	__asm volatile( "mov	w0,		NVMKEY");

	// Program & wait until finished
	NVMCONbits.WR = true;
	while( NVMCONbits.WR)
		;
}


/*!
 * \brief
 * Writes data to the internal EEPROM.
 * 
 * \param _addrDestination
 * Specifies the program memory space address.
 * 
 * \param _lpvSource
 * Specifies the source buffer.
 * 
 * \param _ui16Length
 * Specifies the amount of bytes to be written.
 *
 * \returns
 * - \c true: the operation succeeded.
 * - \c false: either the destination address or the data length were out of bounds.
 * 
 * Writes the specified data row based into the internal EEPROM. Misalignment is handled internally. Thus, the user can address
 * any byte within the EEPROM directly.
 * 
 * \remarks
 * - This function is interrupt safe. Care should be taken when writing long data sequences due to starvation.
 * - In case a row is not fully overwritten (e.g. due to misalignment) the unchanged content will backuped and restored.
 * - The PSV window maps on the EEPROM due to the recovery operation mentioned above. The previous PSV settings are restored afterwards.
 * - The NVM mode will be left when the write operation succeeded \c (NVMCONbits.WREN \c = \c false).
 *
 * \warning
 * Results are unpredictable when the source buffer addresses the PSV window.
 * 
 * \see
 * hal_nvm_readEEPROM
 */
bool hal_nvm_writeEEPROM(
	IN const _prog_addressT _addrDestination,
	IN const void* const _lpvSource,
	IN const uint16_t _ui16Length
	) {

	bool blSuccess = false;

	// Check bounds
	const uint16_t ui16EEPROMAddr = (uint16_t)_addrDestination - EEPROM_NVM_ADDR_LOWER_OFFSET;
	if( ( (uint16_t)( _addrDestination >> 16) == EEPROM_NVM_ADDR_UPPER) &&
		( (uint16_t)_addrDestination >= EEPROM_NVM_ADDR_LOWER_OFFSET) && 
		( _ui16Length <= EEPROM_SIZE) &&
		( EEPROM_SIZE >= ui16EEPROMAddr + _ui16Length)) {

		HAL_INT_ATOMIC_BLOCK( HAL_INT_PRIORITY__7) {

			// Backup current PSV & configure for EEPROM access
			const uint8_t ui8BackupPSVPAG = PSVPAG;
			const bool blBackupPSVEnable = CORCONbits.PSV;
			PSVPAG = EEPROM_PSV_ADDR_UPPER;
			CORCONbits.PSV = true;

			// Holds the offset of the EEPROM row boundary
			const uint16_t ui16BaseOffset = ui16EEPROMAddr & ( EEPROM_WORDS_PER_ROW * EEPROM_BYTES_PER_WORD - 1);

			// Holds the current EEPROM row (with correct alignment)
			uint16_t ui16CurRowAddr = ui16EEPROMAddr - ui16BaseOffset;

			uint16_t ui16Written = 0;
			while( ui16Written < _ui16Length) {

				// Read a backup image if the row is not completely renewed
				uint16_t aui16Image[EEPROM_WORDS_PER_ROW];
				uint16_t ui16ToBeWritten = _ui16Length - ui16Written;
				if( ui16ToBeWritten < sizeof( aui16Image) || ( !ui16Written && ui16BaseOffset)) {
					const uint16_t ui16EffectiveReadAddress = ( ui16CurRowAddr + EEPROM_PSV_ADDR_LOWER_OFFSET) | PSV_ADDRESS_ENABLE;
					memcpy( aui16Image, (void*)ui16EffectiveReadAddress, sizeof( aui16Image));
				}

				// Update row; need to consider boundary alignment on first write
				if( !ui16Written) {
					if( ui16ToBeWritten > sizeof( aui16Image) - ui16BaseOffset) {
						ui16ToBeWritten = sizeof( aui16Image) - ui16BaseOffset;
					}
					memcpy( (uint8_t*)aui16Image + ui16BaseOffset, (const uint8_t*)_lpvSource, ui16ToBeWritten);
				} else {
					if( ui16ToBeWritten > sizeof( aui16Image)) {
						ui16ToBeWritten = sizeof( aui16Image);
					}
					memcpy( aui16Image, (const uint8_t*)_lpvSource + ui16Written, ui16ToBeWritten);
				}

				// Prepare address & perform row erase
				NVMADRU = EEPROM_NVM_ADDR_UPPER;
				NVMADR = EEPROM_NVM_ADDR_LOWER_OFFSET + ui16CurRowAddr;
				executeOperation( OPERATION__EEPROM_ERASE_ROW);

				// Fill table latches & perform row write
				TBLPAG = EEPROM_NVM_ADDR_UPPER;
				for( uint16_t ui16 = 0; ui16 < EEPROM_WORDS_PER_ROW; ui16++) {
					const uint16_t ui16WordAddress = ui16CurRowAddr + ui16 * 2 + EEPROM_NVM_ADDR_LOWER_OFFSET;
					__asm volatile( "tblwtl %0, [%1]" :  :  "r"( aui16Image[ui16]), "r"( ui16WordAddress));
				}
				executeOperation( OPERATION__EEPROM_WRITE_ROW);

				// Advance to next row
				ui16Written += ui16ToBeWritten;
				ui16CurRowAddr += sizeof( aui16Image);
			}

			NVMCONbits.WREN = false;
			PSVPAG = ui8BackupPSVPAG;
			CORCONbits.PSV = blBackupPSVEnable;
		}

		blSuccess = true;
	}

	return blSuccess;
}


/*!
 * \brief
 * Reads data from the internal EEPROM.
 * 
 * \param _addrSource
 * Specifies the program memory space address.
 * 
 * \param _lpvDestination
 * Specifies the destination buffer.
 * 
 * \param _ui16Length
 * Specifies the amount of bytes to be read.
 * 
 * \returns
 * - \c true: the operation succeeded.
 * - \c false: either the source address or the data length were out of bounds.
 * 
 * The read operation is based on an EEPROM PSV mapping.
 * 
 * \remarks
 * - This function is interrupt safe. Care should be taken when reading long data sequences due to starvation.
 * - The previous PSV settings are restored after the operation has finished.
 * 
 * \see
 * hal_nvm_readEEPROM
 */
bool hal_nvm_readEEPROM(
	IN const _prog_addressT _addrSource,
	OUT void* const _lpvDestination,
	IN const uint16_t _ui16Length
	) {

	bool blSuccess = false;

	// Check bounds
	const uint16_t ui16EEPROMAddr = (uint16_t)_addrSource - EEPROM_NVM_ADDR_LOWER_OFFSET;
	if( ( (uint16_t)( _addrSource >> 16) == EEPROM_NVM_ADDR_UPPER) &&
		( (uint16_t)_addrSource >= EEPROM_NVM_ADDR_LOWER_OFFSET) && 
		( _ui16Length <= EEPROM_SIZE) &&
		( EEPROM_SIZE >= ui16EEPROMAddr + _ui16Length)) {

		HAL_INT_ATOMIC_BLOCK( HAL_INT_PRIORITY__7) {
			const uint8_t ui8BackupPSVPAG = PSVPAG;
			const bool blBackupPSVEnable = CORCONbits.PSV;
			PSVPAG = EEPROM_PSV_ADDR_UPPER;
			CORCONbits.PSV = true;

			const uint16_t ui16EffectiveAddress = ( ui16EEPROMAddr + EEPROM_PSV_ADDR_LOWER_OFFSET) | PSV_ADDRESS_ENABLE;
			memcpy( _lpvDestination, (const void*)ui16EffectiveAddress, _ui16Length);

			PSVPAG = ui8BackupPSVPAG;
			CORCONbits.PSV = blBackupPSVEnable;

		}

		blSuccess = true;
	}

	return blSuccess;
}
