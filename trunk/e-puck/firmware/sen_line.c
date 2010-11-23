#include "sen_line.h"

enum {
	SENSOR_I2C_Address = 0xC0;			///< adress of ground-sensors on the I2C-BUS
	// könnte auch 0xD4 sein, muss noch herausgefunden werden
}

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
		uint8_t aui8RawBuffer[30] = {0};
		uint16_t aui16Buffer[3] = {0};			///< stores sensordata temporarily

        for( uint8_t j = 0; j < 6; j++) {
			if( j % 2 == 0) {
				hal_i2c_read( SENSOR_I2C_Address, aui8RawBuffer[j + 1], 1);
				// TODO letzter Parameter der read-Funktion ist mir unklar. Im Demo wird byteweise über den BUS ausgelesen und anschließend konkateniert, geht das bei uns vielleicht sogar bequemer?
				// muss ich hal_i2c_read einen pointer auf das array übergeben?
			}
			else if( j % 2 != 0) {
				hal_i2c_read(SENSOR_I2C_Address, aui8RawBuffer[j - 1], 1);
			}
        }
		aui16Buffer[0] = (unsigned int) (aui8RawBuffer[0] << 8) | ((unsigned int) aui8RawBuffer[1]);
		aui16Buffer[1] = (unsigned int) (aui8RawBuffer[2] << 8) | ((unsigned int) aui8RawBuffer[3]);
		aui16Buffer[2] = (unsigned int) (aui8RawBuffer[4] << 8) | ((unsigned int) aui8RawBuffer[5]);
		// TODO Nachfragen ob diese Konkatenation der Bytes so funktionieren kann
}
