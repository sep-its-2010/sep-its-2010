#ifndef sen_line_types_h__
#define sen_line_types_h__

#include "common.h"


enum {
	SEN_LINE_LOWER_BOUND = 0, ///< Specifies the lower bound of the sensor values.
	SEN_LINE_UPPER_BOUND = 1023, ///< Specifies the upper bound of the sensor values.
	SEN_LINE_NUM_SENSORS = 3 ///< Specifies the amount of line sensors (left, middle, right).
};


/*!
 * \brief
 * Specifies the array indices of the separate line sensors within #sen_line_SData_t.
 */
typedef enum {
	SEN_LINE_SENSOR__LEFT = 0, ///< Specifies the array index of the left line sensor.
	SEN_LINE_SENSOR__MIDDLE = 1, ///< Specifies the array index of the middle line sensor.
	SEN_LINE_SENSOR__RIGHT = 2 ///< Specifies the array index of the right line sensor.
} sen_line_ESensor_t;


/*!
 * \brief
 * Specifies the line sensor data container.
 * 
 * \see
 * sen_line_read | SEN_LINE_SENSOR__LEFT | SEN_LINE_SENSOR__RIGHT | SEN_LINE_SENSOR__MIDDLE
 */
typedef struct {
	uint16_t aui16Reflected[SEN_LINE_NUM_SENSORS]; ///< Holds the values of the left, middle and right sensor ranging from #SEN_LINE_LOWER_BOUND to #SEN_LINE_UPPER_BOUND.
	uint16_t aui16Ambient[SEN_LINE_NUM_SENSORS]; ///< Holds the values of the left, middle and right sensor ranging from #SEN_LINE_LOWER_BOUND to #SEN_LINE_UPPER_BOUND.
} sen_line_SData_t;

#endif // sen_line_types_h__
