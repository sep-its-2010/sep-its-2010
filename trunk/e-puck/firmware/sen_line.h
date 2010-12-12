#ifndef sen_line_h__
#define sen_line_h__

#include "common.h"
#include "sen_line_types.h"

enum {
	SEN_LINE_NOMINAL_BLACK_LEVEL = 250, ///< Specifies the black level used for calibrating the sensors.
	SEN_LINE_NOMINAL_WHITE_LEVEL = 750 ///< Specifies the white level used for calibrating the sensors.
};

void sen_line_read(
	OUT sen_line_SData_t* const _lppodData
	);

bool sen_line_calibrate(
	IN const sen_line_SData_t* const _lppodRawBlackLevel,
	IN const sen_line_SData_t* const _lppodRawWhiteLevel
	);

void sen_line_rescale(
	IN const sen_line_SData_t* const _lppodRawData,
	OUT sen_line_SData_t* const _lppodRescaledData
	);

#endif // sen_line_h__
