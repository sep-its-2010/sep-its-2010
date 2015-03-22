#include <p30f6014A.h>

#include "hal_led.h"

#include "hal_i2c.h"


enum {
	I2C_IDLE_MASK = ( 1 << 4) | ( 1 << 3) | ( 1 << 2) | ( 1 << 1) | ( 1 << 0) ///< Specifies the operation mode bits within the control register.
};


static void start( void);

static void restart( void);

static void stop( void);

static bool write(
	IN const uint8_t _ui8Data
	);

static uint8_t read(
	IN const bool _blAcknowledge
	);


/*!
 * \internal
 * \brief
 * Issues an I2C start condition.
 * 
 * No preconditions are checked. The function blocks until the start sequence is sent.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * restart | stop | write
 */
void start( void) {

	I2CCONbits.SEN = true;
	while( I2CCONbits.SEN)
		;
}


/*!
 * \internal
 * \brief
 * Issues an I2C restart condition.
 * 
 * The restart sequence starts as soon as the module is idle. The function blocks until the sequence is sent.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * start | stop | write
 */
void restart( void) {

	while( I2CCON & I2C_IDLE_MASK)
		;

	I2CCONbits.RSEN = true;
	while( I2CCONbits.RSEN)
		;
}


/*!
 * \internal
 * \brief
 * Issues an I2C stop condition.
 * 
 * The stop sequence starts as soon as the module is idle. The function blocks until the sequence is sent.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * start | restart
 */
void stop( void) {

	while( I2CCON & I2C_IDLE_MASK)
		;

	I2CCONbits.PEN = true;
	while( I2CCONbits.PEN)
		;
}


/*!
 * \internal
 * \brief
 * Writes a byte on the I2C bus and checks the subsequent acknowledge bit of the slave device.
 * 
 * \param _ui8Data
 * Specifies the byte to be written.
 * 
 * \returns
 * Whether or not a slave device acknowledged the byte.
 * 
 * This function can be used to write address and data bytes.
 * When writing an address the acknowledge bit indicates whether or not the device responded.
 *
 * The transmission will start as soon as any pending transmissions finished.
 * The function blocks until the transmission finished.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * read | start | restart | stop
 */
bool write(
	IN const uint8_t _ui8Data
	) {

	while( I2CSTATbits.TRSTAT)
		;

	I2CTRN = _ui8Data;
	while( I2CSTATbits.TRSTAT)
		;

	return !I2CSTATbits.ACKSTAT;
}


/*!
 * \internal
 * \brief
 * Reads one byte on the I2C bus and writes a subsequent acknowledge bit for the slave device.
 * 
 * \param _blAcknowledge
 * Specifies whether or not to acknowledge the byte.
 *
 * \returns
 * The byte which was read from the bus.
 * 
 * The reception starts as soon as the module is idle and finishes when the acknowledge bit is transmitted.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * write | stop
 */
uint8_t read(
	IN const bool _blAcknowledge
	) {

	while( I2CCON & I2C_IDLE_MASK)
		;

	I2CCONbits.RCEN = true;
	while( I2CCONbits.RCEN)
		;

	const uint8_t ui8Byte = I2CRCV;


	while( I2CCON & I2C_IDLE_MASK)
		;

	I2CCONbits.ACKDT = !_blAcknowledge;
	I2CCONbits.ACKEN = true;
	while( I2CCONbits.ACKEN)
		;

	return ui8Byte;
}


/*!
 * \brief
 * Initializes the I2C module.
 * 
 * \param _ui16BaudRateDiv
 * Specifies the baud rate divisor. The divisor must be greater than 0 and lower or equal #I2C_MAX_BAUDRATE_DIVISOR.
 *
 * \param _blEnableSlewRateControl
 * Specifies whether or not to enable the slew rate control.
 *
 * \returns
 * - \c true: Operation succeeded.
 * - \c false: Invalid baud rate divisor.
 * 
 * The I2C module is initialized and enabled as a polling (no interrupts) master in single master mode.
 *
 * When operating on a 400kHz bus, the I2C specification requires a slew rate control of the device pin output.
 * This slew rate control is integrated into the device.
 * 
 * \remarks
 * This function may not be preempted by any function which accesses this module.
 *
 * \warning
 * This function is not interrupt safe.
 */
bool hal_i2c_init(
	IN const uint16_t _ui16BaudRateDiv,
	IN const bool _blEnableSlewRateControl
	) {

	bool blSuccess = false;

	if( _ui16BaudRateDiv > 0 && _ui16BaudRateDiv <= HAL_I2C_MAX_BAUDRATE_DIVISOR) {
		I2CCON = 0;
		I2CBRG = _ui16BaudRateDiv;
		I2CCONbits.DISSLW = !_blEnableSlewRateControl;
		I2CCONbits.I2CEN = true;

		blSuccess = true;
	}

	return blSuccess;
}


/*!
 * \brief
 * Writes the specified data to a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _lpvData
 * Specifies the source buffer.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be written. The length may not exceed 0xFFFE.
 *
 * \returns
 * - The amount of bytes which were actually written; this includes the address byte.
 * - \c 0 when the addressed device did not respond.
 * 
 * A start-stop frame is used to write the data to the slave.
 * A transmission only takes place when the addressed device responds.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
uint16_t hal_i2c_write(
	IN const uint8_t _ui8SlaveAddress,
	IN const void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	uint16_t ui16Written = 0;

	start();
	if( write( _ui8SlaveAddress & 0xFE)) {
		bool blAck = true;
		for( ui16Written = 0; blAck && ui16Written < _ui16Length; ui16Written++) {
			blAck = write( ( (const uint8_t*)_lpvData)[ui16Written]);
		}
	}
	stop();

	return ui16Written + 1;
}


/*!
 * \brief
 * Writes a byte to a specified register of a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _ui8Register
 * Specifies the register address.
 * 
 * \param _ui8Data
 * Specifies the data byte.
 *
 * \returns
 * Whether or not the device responded.
 * 
 * A start-stop frame is used to write the register data to the slave.
 * A transmission only takes place when the addressed device responds.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_write
 */
bool hal_i2c_writeRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register,
	IN const uint8_t _ui8Data
	) {

	bool blSuccess = false;

	start();
	if( write( _ui8SlaveAddress & 0xFE)) {
		write( _ui8Register);
		write( _ui8Data);

		blSuccess = true;
	}
	stop();

	return blSuccess;
}


/*!
 * \brief
 * Reads the requested data from a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _lpvData
 * Specifies the destination buffer.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be read.
 *
 * \returns
 * Whether or not the device responded.
 * 
 * A start-stop frame is used to read the data from the slave.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_i2c_init | hal_i2c_write | hal_i2c_readRegister
 */
bool hal_i2c_read(
	IN const uint8_t _ui8SlaveAddress,
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	bool blSuccess = true;

	start();
	if( write( _ui8SlaveAddress | 0x01)) {
		for( uint16_t ui16 = 0; ui16 < _ui16Length; ui16++) {
			*( (uint8_t*) _lpvData) = read( ui16 + 1 < _ui16Length);
		}

		blSuccess = true;
	}
	stop();

	return blSuccess;
}


/*!
 * \brief
 * Reads a byte from a specified register of a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _ui8Register
 * Specifies the register.
 * 
 * \returns
 * - \c 0x00 to \c 0xFF on success.
 * - any negative value when the device did not respond.
 * 
 * A start-restart-stop frame is used to read the register data from the slave.
 * A transmission only takes place when the addressed device responds.
 * 
 * \remarks
 * The I2C module needs to be initialized.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * hal_i2c_init | hal_i2c_read | hal_i2c_writeRegister
 */
int16_t hal_i2c_readRegister(
	IN const uint8_t _ui8SlaveAddress,
	IN const uint8_t _ui8Register
	) {

	int16_t i16Return = -1;

	start();
	if( write( _ui8SlaveAddress & 0xFE)) {
		write( _ui8Register);
		restart();
		if( write( _ui8SlaveAddress | 0x01)) {
			i16Return = read( false);;
		}
	}
	stop();

	return i16Return;
}
