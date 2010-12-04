#include "hal_i2c.h"

#include "sen_line.h"

enum {
	SENSOR_I2C_ADDRESS = 0xC0 ///< Specifies the I2C address of the line sensor device (padded with R/W bit).
};

/*!
 * \brief
 * Reads the left, middle and right line sensor values.
 * 
 * \param _lppodData
 * Specifies the output buffer for the sensor data.
 * 
 * The sensors are queried via the I2C bus through the hal_i2c layer.
 * 
 * \remarks
 * The I2C layer needs to be initialized before the sensor data can be read.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 *
 * \see
 * hal_i2c_init
 */
void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_SENSORS; ui16++) {
		int16_t i16Val = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2);
		if( i16Val >= 0) {
			_lppodData->aui16Data[ui16] = ( i16Val & 0xFF) << 8;
			i16Val = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + 1);
		}
		if( i16Val >= 0) {
			_lppodData->aui16Data[ui16] |= i16Val & 0xFF;
		} else {
			_lppodData->aui16Data[ui16] = 0;
		}
	}
}
