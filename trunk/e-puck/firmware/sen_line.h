#ifndef sen_line_h__
#define sen_line_h__

#include "common.h"
#include "sen_line_types.h"

enum {
	SEN_LINE_NOMINAL_BACK_LEVEL = 250 ///< Specifies the black level used for calibrating the sensors.
};

void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	);

void sen_line_calibrate(
	IN const sen_line_SData_t* const _lppodBlackLevel
	);

void sen_line_filter(
	IN const sen_line_SData_t* const _lppodRawData,
	OUT sen_line_SData_t* const _lppodFilteredData
	);

#endif // sen_line_h__
