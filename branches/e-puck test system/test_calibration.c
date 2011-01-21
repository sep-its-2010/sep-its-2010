#include <stdio.h>

#include "hal_uart1.h"
#include "sen_line.h"

#include "test_calibration.h"


void test_calibration_run( void) {

	extern int32_t s_aai32Coefficients[SEN_LINE_NUM_SENSORS][2];

	char cstrBuffer[128];
	hal_uart1_puts( "\r\nLine sensor calibration test\r\n");

	sen_line_SData_t podWhiteLevel = { { 681, 773, 784 }, { 0 }};
	sen_line_SData_t podBlackLevel = { { 112, 240, 167 }, { 0 }};
	sprintf( cstrBuffer, "Raw white level values: %3d %3d %3d\r\nRaw black level values: %3d %3d %3d\r\n",
		podWhiteLevel.aui16Reflected[0], podWhiteLevel.aui16Reflected[1], podWhiteLevel.aui16Reflected[2],
		podBlackLevel.aui16Reflected[0], podBlackLevel.aui16Reflected[1], podBlackLevel.aui16Reflected[2]);
	hal_uart1_puts( cstrBuffer);

	hal_uart1_puts( sen_line_calibrate( &podBlackLevel, &podWhiteLevel) ? "Calibration succeeded\r\n" : "Calibration failed\r\n");

	sen_line_rescale( &podBlackLevel, &podBlackLevel);
	sen_line_rescale( &podWhiteLevel, &podWhiteLevel);
	sprintf( cstrBuffer, "Cal white level values: %3d %3d %3d\r\nCal black level values: %3d %3d %3d\r\n",
		podWhiteLevel.aui16Reflected[0], podWhiteLevel.aui16Reflected[1], podWhiteLevel.aui16Reflected[2],
		podBlackLevel.aui16Reflected[0], podBlackLevel.aui16Reflected[1], podBlackLevel.aui16Reflected[2]);
	hal_uart1_puts( cstrBuffer);

	sen_line_SData_t podTestVector = { { 445, 329, 598 }, { 0 } };
	sen_line_SData_t podCalTestVector;
	sen_line_rescale( &podTestVector, &podCalTestVector);
	sprintf( cstrBuffer, "Raw test vector: %3d %3d %3d\r\nCal test vector: %3d %3d %3d\r\n",
		podTestVector.aui16Reflected[0], podTestVector.aui16Reflected[1], podTestVector.aui16Reflected[2],
		podCalTestVector.aui16Reflected[0], podCalTestVector.aui16Reflected[1], podCalTestVector.aui16Reflected[2]);
	hal_uart1_puts( cstrBuffer);

	hal_uart1_puts( "Calibration coefficients\r\n");
	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		double a0 = s_aai32Coefficients[ui16][0] / 2048.0;
		double a1 = s_aai32Coefficients[ui16][1] / 2048.0;
		sprintf( cstrBuffer, "%10f %10f\r\n", a0, a1);
		hal_uart1_puts( cstrBuffer);
	}
}
