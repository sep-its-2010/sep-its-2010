#include "hal_i2c.h"

enum {
	I2C_MAX_BAUDRATE_DIVISOR = ( 1 << 8) - 1, ///< Specifies the maximal i2c baud rate divisor.
	I2C_OPCODE_MASK = ( 1 << 4) | ( 1 << 3) | ( 1 << 2) | ( 1 << 1) | ( 1 << 0) ///< Specifies the operation mode bits within the control register.
};


static inline bool waitIdle(
	IN const uint16_t _ui16TimeoutTicks
	);

bool waitIdle(
	IN const uint16_t _ui16TimeoutTicks
	) {

	bool blTimeout = false;

	if( !_ui16TimeoutTicks) {
		while( I2CCON & I2C_OPCODE_MASK)
			;
	} else {
		uint16_t ui16 = _ui16TimeoutTicks;
		while( ui16 && ( I2CCON & I2C_OPCODE_MASK)) {
			ui16--;
		}

		blTimeout = !ui16;
	}

	return blTimeout;
}

/*!
 * \brief
 * Initializes the I2C module.
 * 
 * \param _ui16BaudRateDiv
 * Specifies the baud rate divisor. The divisor must be greater than 0 and lower or equal #I2C_MAX_BAUDRATE_DIVISOR.
 *
 * \retunrs
 * - true: Success.
 * - false: Invalid parameter.
 * 
 * The I2C module is initialized as a polling (no interrupts) master.
 * 
 * \remarks
 * This function needs to be called before any other I2C function.
 *
 * \warning
 * This function is not interrupt safe.
 */
void hal_i2c_init(
	IN const uint16_t _ui16BaudRateDiv
	) {

	if( _ui16BaudRateDiv > 0 && _ui16BaudRateDiv <= I2C_MAX_BAUDRATE_DIVISOR) {
		I2CCONbits.I2CEN = false;
		I2CBRG = _ui16BaudRateDiv;
		I2CCONbits.I2CEN = true;
	}
}


/*!
 * \brief
 * Writes the specified data to a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the salve address
 * 
 * \param _lpui8Data
 * Specifies the data to be written.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be written.
 *
 * \retunrs
 * - true: Success.
 * - false: Invalid parameter.
 * 
 * A start-stop frame is used to write the data to the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
bool hal_i2c_write(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t* const _lpui8Data,
	IN const uint16_t _ui16Length
	) {

}


/*!
 * \brief
 * Writes a byte to a specified register of a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address.
 * 
 * \param _ui8Register
 * Specifies the register.
 * 
 * \param _ui8Data
 * Specifies the data byte.
 *
 * \retunrs
 * - true: Success.
 * - false: Invalid parameter.
 * 
 * A start-stop frame is used to write the data to the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_write
 */
bool hal_i2c_writeRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register,
	IN const uint8_t _ui8Data
	) {

}


/*!
 * \brief
 * Reads the requested data from a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address.
 * 
 * \param _lpui8Data
 * Specifies the destination buffer.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be read.
 * 
 * A start-stop frame is used to read the data from the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_write | hal_i2c_readRegister
 */
bool hal_i2c_read(
	IN const uint8_t _ui8SlaveAddress,
	OUT uint8_t* const _lpui8Data,
	IN const uint16_t _ui16Length
	) {

}


/*!
 * \brief
 * Reads a byte from a specified register of a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address.
 * 
 * \param _ui8Register
 * Specifies the register.
 * 
 * \returns
 * - 0x00 to 0xFF on success.
 * - 0xFFFF on timeout.
 * 
 * A start-stop frame is used to read the data from the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
int16_t hal_i2c_readRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register
	) {

}
