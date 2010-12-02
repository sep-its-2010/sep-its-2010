#include <p30f6014A.h>

#include "hal_led.h"

#include "hal_i2c.h"

enum {
	I2C_OPCODE_MASK = ( 1 << 4) | ( 1 << 3) | ( 1 << 2) | ( 1 << 1) | ( 1 << 0) ///< Specifies the operation mode bits within the control register.
};

static volatile bool s_blBreak = false;

static void reset( void);

static bool start( void);

static bool restart( void);

static bool stop( void);

static bool ack( void);

static bool nack( void);

static bool write(
	IN const uint8_t _ui8Data
	);

static bool read(
	OUT uint8_t* const _lpui8Data
	);

void reset( void) {

	I2CCONbits.I2CEN = false;
	I2CCONbits.I2CEN = true;
}

bool start( void) {

	I2CCONbits.SEN = true;
	while( !s_blBreak && I2CCONbits.SEN)
		;

	return !I2CCONbits.SEN;
}


bool restart( void) {

	bool blSuccess = false;

	while( !s_blBreak && ( I2CCON & I2C_OPCODE_MASK))
		;

	if( !( I2CCON & I2C_OPCODE_MASK)) {
		I2CCONbits.RSEN = true;
		while( !s_blBreak && I2CCONbits.RSEN)
			;

		blSuccess = !I2CCONbits.RSEN;
	}

	return blSuccess;
}

bool stop( void) {

	bool blSuccess = false;

	while( !s_blBreak && ( I2CCON & I2C_OPCODE_MASK))
		;

	if( !( I2CCON & I2C_OPCODE_MASK)) {
		I2CCONbits.PEN = true;
		while( !s_blBreak && I2CCONbits.PEN)
			;

		blSuccess = !I2CCONbits.PEN;
	}

	return blSuccess;
}

bool ack( void) {

	bool blSuccess = false;

	while( !s_blBreak && ( I2CCON & I2C_OPCODE_MASK))
		;

	if( !( I2CCON & I2C_OPCODE_MASK)) {
		I2CCONbits.ACKDT = false;
		I2CCONbits.ACKEN = true;
		while( !s_blBreak && I2CCONbits.ACKEN)
			;

		blSuccess = !I2CCONbits.ACKEN;
	}

	return blSuccess;
}

bool nack( void) {

	bool blSuccess = false;

	while( !s_blBreak && ( I2CCON & I2C_OPCODE_MASK))
		;

	if( !( I2CCON & I2C_OPCODE_MASK)) {
		I2CCONbits.ACKDT = true;
		I2CCONbits.ACKEN = true;
		while( !s_blBreak && I2CCONbits.ACKEN)
			;

		I2CCONbits.ACKDT = false;

		blSuccess = !I2CCONbits.ACKEN;
	}

	return blSuccess;
}


bool write(
	IN const uint8_t _ui8Data
	) {

	bool blSuccess = false;

	while( !s_blBreak && I2CSTATbits.TRSTAT)
		;

	if( !I2CSTATbits.TRSTAT) {
		I2CTRN = _ui8Data;
		while( !s_blBreak && I2CSTATbits.TRSTAT)
			;

		blSuccess = !I2CSTATbits.TRSTAT;
	}


	return blSuccess;
}

bool read(
	OUT uint8_t* const _lpui8Data
	) {

	bool blSuccess = false;

	while( !s_blBreak && ( I2CCON & I2C_OPCODE_MASK))
		;

	if( !( I2CCON & I2C_OPCODE_MASK)) {
		I2CCONbits.RCEN = true;
		while( !s_blBreak && I2CCONbits.RCEN)
			;

		if( !I2CCONbits.RCEN) {
			blSuccess = true;
			*_lpui8Data = I2CRCV;
		}
	}

	return blSuccess;
}

/*!
 * \brief
 * Initializes the I2C module.
 * 
 * \param _ui16BaudRateDiv
 * Specifies the baud rate divisor. The divisor must be greater than 0 and lower or equal #I2C_MAX_BAUDRATE_DIVISOR.
 *
 * \returns
 * - \c true: Success.
 * - \c false: Invalid parameter.
 * 
 * The I2C module is initialized as a polling (no interrupts) master.
 * 
 * \remarks
 * This function needs to be called before any other I2C function.
 *
 * \warning
 * This function is not interrupt safe.
 */
bool hal_i2c_init(
	IN const uint16_t _ui16BaudRateDiv
	) {

	bool blSuccess = false;

	if( _ui16BaudRateDiv > 0 && _ui16BaudRateDiv <= HAL_I2C_MAX_BAUDRATE_DIVISOR) {
		s_blBreak = false;
		I2CCON = 0;
		I2CBRG = _ui16BaudRateDiv;
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
 * \param _lpui8Data
 * Specifies the data to be written.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be written.
 *
 * \returns
 * - \c true: Success.
 * - \c false: Invalid parameter.
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

	bool blSuccess = true;

	s_blBreak = false;
	if( !start()) {
		blSuccess = false;
		reset();
	} else if( !write( _ui8SlaveAddress & 0xFE)) {
		blSuccess = false;
		reset();
	} else {
		for( uint16_t ui16 = 0; blSuccess && ui16 < _ui16Length; ui16++)	{
			blSuccess = write( _lpui8Data[ui16]);
		}
		if( !blSuccess || !stop()) {
			blSuccess = false;
			reset();
		}
	}

	return blSuccess;
}


/*!
 * \brief
 * Writes a byte to a specified register of a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _ui8Register
 * Specifies the register.
 * 
 * \param _ui8Data
 * Specifies the data byte.
 *
 * \returns
 * - \c true: Success.
 * - \c false: Invalid parameter.
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

	bool blSuccess = false;

	s_blBreak = false;
	if( !start()) {
		reset();
	} else if( !write( _ui8SlaveAddress & 0xFE)) {
		reset();
	} else if( !write( _ui8Register)) {
		reset();
	} else if( !write( _ui8Data)) {
		reset();
	} else if( !stop()) {
		reset();
	} else {
		blSuccess = true;
	}

	return blSuccess;
}


/*!
 * \brief
 * Reads the requested data from a given I2C slave.
 * 
 * \param _ui8SlaveAddress
 * Specifies the slave address. The least significant bit is reserved for the data direction and thus ignored.
 * 
 * \param _lpui8Data
 * Specifies the destination buffer.
 * 
 * \param _ui16Length
 * Specifies the length of the date to be read.
 * 
 * \returns
 * - \c 0x00 to \c 0xFF on success.
 * - \c 0xFFFF on timeout.
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

	bool blSuccess = true;

	s_blBreak = false;
	if( !start()) {
		blSuccess = false;
		reset();
	} else if( !write( _ui8SlaveAddress | 0x01)) {
		blSuccess = false;
		reset();
	} else {
		for( uint16_t ui16 = 0; blSuccess && ui16 < _ui16Length; ui16++) {
			if( !read( &_lpui8Data[ui16])) {
				blSuccess = false;
				reset();
			} else if( _ui16Length == ui16 + 1) {
				if( !nack()) {
					blSuccess = false;
					reset();
				}
			} else if( !ack()) {
				blSuccess = false;
				reset();
			}
		}
		if( blSuccess && !stop()) {
			blSuccess = false;
			reset();
		}
	}

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
 * - \c 0xFFFF on timeout.
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

	int16_t i16Return;
	uint8_t ui8Data = 1;

	s_blBreak = false;
	if( !start()) {
		reset();
		i16Return = -1;
	} else if( !write( _ui8SlaveAddress & 0xFE)) {
		reset();
		i16Return = -2;
	} else if( !write( _ui8Register)) {
		reset();
		i16Return = -3;
	} else if( !restart()) {
		reset();
		i16Return = -4;
	} else if( !write( _ui8SlaveAddress | 0x01)) {
		reset();
		i16Return = -5;
	} else if( !read( &ui8Data)) {
		reset();
		i16Return = -6;
	} else if( !nack()) {
		reset();
		i16Return = -7;
	} else if( !stop()) {
		reset();
		i16Return = -8;
	} else {
		i16Return = ui8Data;
	}

	return i16Return;
}
