#include <string.h>

#include "hal_i2c.h"

#include "sen_line.h"

enum {
	SENSOR_I2C_ADDRESS = 0xC0, ///< Specifies the I2C address of the line sensor device (padded with R/W bit).
	DELTA_WHITE_TO_BLACK = SEN_LINE_NOMINAL_WHITE_LEVEL - SEN_LINE_NOMINAL_BLACK_LEVEL, ///< Specifies the delta between white and black.
	NUM_COEFFICIENTS = 2, ///< Specifies the degree of the rescaling polynomial.
	COEFFICIENT_SCALAR = 2048, ///< Specifies the scalar for all coefficients.
	SENSOR_I2C_AMBIENT_REG_OFFSET = 6, ///< Specifies the register block offset of the ambient light values.
	SENSOR_I2C_REFLECTED_REG_OFFSET = 0 ///< Specifies the register block offset of the reflected light values.
};


/*!
 * \brief
 * Holds the rescaling coefficients for each sensor.
 * 
 * \remarks
 * - The coefficients above the 0. degree are scaled by #COEFFICIENT_SCALAR to maintain precision.
 * - The offsets are initialized to zero. Thus, filtering without previous calibration maps any sensor values to zero.
 * 
 * \see
 * sen_line_calibrate | sen_line_rescale
 */
static int32_t s_aai32Coefficients[SEN_LINE_NUM_SENSORS][NUM_COEFFICIENTS];


/*!
 * \brief
 * Reads the left, middle and right line sensor values.
 * 
 * \param _lppodData
 * Specifies the output buffer for the sensor data.
 * 
 * The sensors are queried via the I2C bus through the hal_i2c layer. Their address is specified b< #SENSOR_I2C_ADDRESS.
 *
 * The raw value of each sensor is between #SEN_LINE_LOWER_BOUND and #SEN_LINE_UPPER_BOUND.
 * 
 * \remarks
 * - The I2C layer needs to be initialized before the sensor data can be read.
 * - The sensors should be calibrated based on a black underground.
 *
 * \warning
 * This function may not be preempted by itself or any module which accesses the I2C bus.
 *
 * \see
 * hal_i2c_init | sen_line_rescale
 */
void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		_lppodData->aui16Reflected[ui16] = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + SENSOR_I2C_REFLECTED_REG_OFFSET) << 8;
		_lppodData->aui16Reflected[ui16] |= hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + 1 + SENSOR_I2C_REFLECTED_REG_OFFSET) & 0xFF;
		_lppodData->aui16Ambient[ui16] = hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + SENSOR_I2C_AMBIENT_REG_OFFSET) << 8;
		_lppodData->aui16Ambient[ui16] |= hal_i2c_readRegister( SENSOR_I2C_ADDRESS, ui16 * 2 + 1 + SENSOR_I2C_AMBIENT_REG_OFFSET) & 0xFF;
	}
}


/*!
 * \brief
 * Performs a black and white calibration.
 * 
 * \param _lppodRawBlackLevel
 * Specifies the raw sensor values on a black underground.
 * 
 * \param _lppodRawWhiteLevel
 * Specifies the raw sensor values on a white underground.
 *
 * \returns
 * - \c true: the calibration succeeded.
 * - \c false: the calibration failed due to invalid raw data. The current calibration remains unchanged.
 * 
 * Due to different ambient light conditions for each sensor, the raw sensor values may vary.
 * The calibration calculates the rescaling coefficients of a first order polynomial in a way that the raw black level will map to
 * #SEN_LINE_NOMINAL_BLACK_LEVEL and the raw white level will map to #SEN_LINE_NOMINAL_WHITE_LEVEL.
 * 
 * \remarks
 * The raw values which are read with #sen_line_read() remain unchanged.
 *
 * \warning
 * This function may not be preempted by any function from this module.
 * 
 * \see
 * sen_line_rescale
 */
bool sen_line_calibrate(
	IN const sen_line_SData_t* const _lppodRawBlackLevel,
	IN const sen_line_SData_t* const _lppodRawWhiteLevel
	) {

	bool blSuccess = true;

	/*
	// / 1  x_black \   / a_0 \   / y_black \
	// |            | * |     | = |         | 
	// \ 1  x_white /   \ a_1 /   \ y_white / 
	//
	// => a_0 + a_1 * x_black = y_black
	//    a_0 + a_1 * x_white = y_white
	//
	// => a_1 = (y_white - y_black) / (x_white - x_black)
	//
	// => a_0 = y_black - a_1 * x_black
	*/

	int32_t aai32[SEN_LINE_NUM_SENSORS][NUM_COEFFICIENTS];
	for( uint16_t ui16 = 0; blSuccess && ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		if( _lppodRawBlackLevel->aui16Reflected[ui16] >= _lppodRawWhiteLevel->aui16Reflected[ui16]) {
			blSuccess = false;
		} else {
			const uint16_t ui16DeltaRaw = _lppodRawWhiteLevel->aui16Reflected[ui16] - _lppodRawBlackLevel->aui16Reflected[ui16];
			aai32[ui16][1] = (int32_t)DELTA_WHITE_TO_BLACK * COEFFICIENT_SCALAR / ui16DeltaRaw;
			const int32_t i32RescaledBlack = aai32[ui16][1] * _lppodRawBlackLevel->aui16Reflected[ui16];
			aai32[ui16][0] = (int32_t)SEN_LINE_NOMINAL_BLACK_LEVEL * COEFFICIENT_SCALAR - i32RescaledBlack;
		}
	}

	if( blSuccess) {

		// Element addressing is required due to compiler optimization
		memcpy( &s_aai32Coefficients[0][0], &aai32[0][0], sizeof( aai32));
	}

	return blSuccess;
}


/*!
 * \brief
 * Rescales the specified sensor values based on the calibration values. 
 * 
 * \param _lppodRawData
 * Specifies the raw sensor values.
 * 
 * \param _lppodRescaledData
 * Specifies the output buffer.
 *
 * The rescaled values will not exceed the range from #SEN_LINE_LOWER_BOUND to #SEN_LINE_UPPER_BOUND.
 * 
 * \remarks
 * The sensor must be calibrated before rescaling or the returned values are all zero.
 * 
 * \warning
 * This function may not be preempted by itself or #sen_line_calibrate().
 * 
 * \see
 * sen_line_read
 */
void sen_line_rescale(
	IN const sen_line_SData_t* const _lppodRawData,
	OUT sen_line_SData_t* const _lppodRescaledData
	) {

	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		const int32_t i32ScalarRescale = s_aai32Coefficients[ui16][1] * _lppodRawData->aui16Reflected[ui16];
		const int16_t i16Rescale = ( s_aai32Coefficients[ui16][0] + i32ScalarRescale) / COEFFICIENT_SCALAR;

		if( i16Rescale < SEN_LINE_LOWER_BOUND) {
			_lppodRescaledData->aui16Reflected[ui16] = SEN_LINE_LOWER_BOUND;
		} else if( i16Rescale > SEN_LINE_UPPER_BOUND) {
			_lppodRescaledData->aui16Reflected[ui16] = SEN_LINE_UPPER_BOUND;
		} else {
			_lppodRescaledData->aui16Reflected[ui16] = i16Rescale;
		}

		_lppodRescaledData->aui16Ambient[ui16] = _lppodRawData->aui16Ambient[ui16];
	}
}
