#include "hal_i2c.h"

#include "sen_line.h"

enum {
	SENSOR_I2C_ADDRESS = 0xC0 ///< Specifies the I2C address of the line sensor device (padded with R/W bit).
};


/*!
 * \brief
 * Holds the offset values for each sensor.
 * 
 * \remarks
 * The offsets are initialized to zero. Thus, filtering without previous calibration has no effect.
 * 
 * \see
 * sen_line_calibrate | sen_line_filter
 */
static int16_t s_ai16Offset[SEN_LINE_NUM_SENSORS] = { 0 };


/*!
 * \brief
 * Reads the left, middle and right line sensor values.
 * 
 * \param _lppodData
 * Specifies the output buffer for the sensor data.
 * 
 * The sensors are queried via the I2C bus through the hal_i2c layer.
 * The raw value of each sensor is between #SEN_LINE_LOWER_BOUND and #SEN_LINE_UPPER_BOUND.
 * 
 * \remarks
 * - The I2C layer needs to be initialized before the sensor data can be read.
 * - The sensors should be calibrated based on a black underground.
 *
 * \warning
 * This function may not be preempted by itself.
 *
 * \see
 * hal_i2c_init | sen_line_filter
 */
void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		int16_t i16Val = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2);
		if( i16Val >= 0) {
			_lppodData->aui16Data[ui16] = ( i16Val & 0xFF) << 8;
			i16Val = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + 1);
		}
		if( i16Val >= 0) {
			_lppodData->aui16Data[ui16] |= i16Val & 0xFF;
		} else {
			_lppodData->aui16Data[ui16] = SEN_LINE_LOWER_BOUND;
		}
	}
}


/*!
 * \brief
 * Performs a black level calibration.
 * 
 * \param _lppodBlackLevel
 * Specifies the sensor values on a black underground.
 * 
 * Due to different ambient light conditions for each sensor, the raw sensor values may vary.
 * The calibration adjusts the offset of the nominal black level to #SEN_LINE_NUM_SENSORS.
 * 
 * \remarks
 * The raw values which are read with #sen_line_read() remain unchanged.
 *
 * \warning
 * This function may not be preempted by any function from this module.
 * 
 * \see
 * sen_line_filter
 */
void sen_line_calibrate(
	IN const sen_line_SData_t* const _lppodBlackLevel
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		s_ai16Offset[ui16] = (int16_t)SEN_LINE_NOMINAL_BACK_LEVEL - (int16_t)_lppodBlackLevel->aui16Data[ui16];
	}
}


/*!
 * \brief
 * Write brief comment for sen_line_filter here.
 * 
 * \param _lppodRawData
 * Specifies the raw sensor values.
 * 
 * \param _lppodFilteredData
 * Specifies the output sensor values after filtering.
 * 
 * An offset for each sensor is added based on the calibration values.
 * The new values may range from #SEN_LINE_LOWER_BOUND to #SEN_LINE_UPPER_BOUND.
 * 
 * \warning
 * This function may not be preempted by itself or #sen_line_calibrate().
 * 
 * \see
 * sen_line_read
 */
void sen_line_filter(
	IN const sen_line_SData_t* const _lppodRawData,
	OUT sen_line_SData_t* const _lppodFilteredData
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		const int16_t i16Filtered = (int16_t)_lppodRawData->aui16Data[ui16] + s_ai16Offset[ui16];
		if( i16Filtered < SEN_LINE_LOWER_BOUND) {
			_lppodFilteredData->aui16Data[ui16] = SEN_LINE_LOWER_BOUND;
		} else if( i16Filtered > SEN_LINE_UPPER_BOUND) {
			_lppodFilteredData->aui16Data[ui16] = SEN_LINE_UPPER_BOUND;
		} else {
			_lppodFilteredData->aui16Data[ui16] = i16Filtered;
		}
	}
}
