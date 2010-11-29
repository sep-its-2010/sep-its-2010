#include "hal_i2c.h"

#include "sen_line.h"

enum {
	SENSOR_I2C_ADDRESS = 0xC0		///< adress of ground-sensors on the I2C-BUS
	// könnte auch 0xD4 sein, muss noch herausgefunden werden
};

/*!
 * \brief
 * Reads the left, middle and right line sensor values.
 * 
 * \param _lppodData
 * Specifies the output buffer for the sensor data.
 * 
 * The sensors are connected via the I2C bus. This function manages the whole communication based on the I2C layer.
 * 
 * \remarks
 * The I2C layer needs to be initialized before the sensor data can be read.
 *
 * \warning
 * This function is not interrupt safe.
 */
void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	) {

	uint8_t* lpui8 = (uint8_t*)_lppodData->aui16Data;
	for( uint16_t ui16 = 0; ui16 < sizeof( _lppodData->aui16Data); ui16++) {
		int16_t i16Val = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16);
		if( i16Val < 0) {
			_lppodData->aui16Data[ui16 / 2] = i16Val;
			break;
		}
		*lpui8++ = i16Val < 0 ? 1 : i16Val & 0xFF;
	}
}
