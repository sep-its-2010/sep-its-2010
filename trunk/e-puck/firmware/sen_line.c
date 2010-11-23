#include "sen_line.h"

enum {
	SENSOR_I2C_Address = 0xC0;			///< adress of ground-sensors on the I2C-BUS
}

/*!
 * \brief
 * Reads the left, middle and right line sensor values.
 * 
 * \param _lppodData
 * Specifies the output buffer for the sensor data.
 * 
 * The sensors are connected via the I2C bus. This function manages the whole communication based the I2C layer.
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
		uint8_t aui16RawBuffer[50] = {0};
		uint16_t aui16Buffer[3] = {0};			///< stores sensordata temporarily

        for( int j = 0; j < 6; j++) {
			if( j % 2 == 0) {
				hal_i2c_read( SENSOR_I2C_Address, aui16RawBuffer[j], j + 1);
				// TODO letzter Parameter der read-Funktion ist mir unklar, warum braucht die Kalibrierung einen RawBuffer von 50 Chars?
			}
			else if( j % 2 != 0) {
				hal_i2c_read(SENSOR_I2C_Address, aui16RawBuffer, j - 1);
			}
        }
		aui16Buffer[0] = (unsigned int) (aui16RawBuffer[0] & 0xff) + ((unsigned int) aui16RawBuffer[1] << 8);
		aui16Buffer[1] = (unsigned int) (aui16RawBuffer[2] & 0xff) + ((unsigned int) aui16RawBuffer[3] << 8);
		aui16Buffer[2] = (unsigned int) (aui16RawBuffer[4] & 0xff) + ((unsigned int) aui16RawBuffer[5] << 8);

}
