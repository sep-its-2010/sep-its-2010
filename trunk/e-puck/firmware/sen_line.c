#include "sen_line.h"


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

}
