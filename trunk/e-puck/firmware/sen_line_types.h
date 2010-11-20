#ifndef sen_line_types_h__
#define sen_line_types_h__

#include "common.h"

enum {
	SEN_LINE_SENSORS = 3, ///< Specifies the amount of line sensors (left, middle, right).
	SEN_LINE_LEFT_SENSOR = 0, ///< Specifies the array index in #sen_line_SData_t of the left line sensor.
	SEN_LINE_MIDDLE_SENSOR = 1, ///< Specifies the array index in #sen_line_SData_t of the middle line sensor.
	SEN_LINE_RIGHT_SENSOR = 2 ///< Specifies the array index in #sen_line_SData_t of the right line sensor.
};

/*!
 * \brief
 * Specifies the line sensor data container.
 * 
 * \see
 * sen_line_read | SEN_LINE_LEFT_SENSOR | SEN_LINE_RIGHT_SENSOR | SEN_LINE_MIDDLE_SENSOR
 */
typedef struct {
	uint16_t aui8Data[SEN_LINE_SENSORS]; ///< Holds the values of the left, middle and right sensor.
} sen_line_SData_t;

#endif // sen_line_types_h__
