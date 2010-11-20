#include "hal_i2c.h"


/*!
 * \brief
 * Initializes the I2C module.
 * 
 * \param _ui16BaudRateDiv
 * Specifies the baud rate divisor.
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
 * A start-stop frame is used to write the data to the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
void hal_i2c_write(
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
 * A start-stop frame is used to write the data to the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_write
 */
void hal_i2c_writeRegister(
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
 * Specifies the salve address
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
void hal_i2c_read(
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
 * The register byte which was read.
 * 
 * A start-stop frame is used to read the data from the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized before being used.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
uint8_t hal_i2c_readRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register
	) {

}
