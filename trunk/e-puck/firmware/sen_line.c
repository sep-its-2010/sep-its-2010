#include "sen_line.h"

char buffer[50];
int l_buffer[3];
const unsigned char I2C_address = 0xC0; // adress of ground sensors

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
		e_i2cp_enable();

        for( int j = 0; j < 6; j++) {
			if( j % 2 == 0) {
				buffer[j] = e_i2cp_read(I2C_address, j + 1);
			}
			else if( j % 2 != 0) {
				buffer[j] = e_i2cp_read(I2C_address, j - 1);
			}
        }
        e_i2cp_disable();
		l_buffer[0] = (unsigned int) (buffer[0] & 0xff) + ((unsigned int) buffer[1] << 8);
		l_buffer[1] = (unsigned int) (buffer[2] & 0xff) + ((unsigned int) buffer[3] << 8);
		l_buffer[2] = (unsigned int) (buffer[4] & 0xff) + ((unsigned int) buffer[5] << 8);

}
