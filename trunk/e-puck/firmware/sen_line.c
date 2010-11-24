#include "sen_line.h"
#include "hal_i2c.h"

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
		// reads 2 Bytes of data from each ground-sensor
        for( uint8_t i = 0; i < 3; i++) {
			hal_i2c_read( SENSOR_I2C_ADDRESS, _lppodData, 2);
			// TODO Reihenfolge der Bytes überprüfen
        }
}
