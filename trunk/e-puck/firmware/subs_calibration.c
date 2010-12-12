#include <libpic30.h>

#include "hal_sel.h"
#include "hal_motors.h"
#include "hal_nvm.h"
#include "sen_line.h"

#include "subs_calibration.h"


enum {
	LEFT_MOTOR = 0, ///< Specifies the local index for data concerning the left motor.
	RIGHT_MOTOR = 1, ///< Specifies the local index for data concerning the right motor.
	BLACK_LEVEL = 0, ///< Specifies the local index for the black level calibration values.
	WHITE_LEVEL = 1 ///< Specifies the local index for the white level calibration values.
};


/*!
 * \brief
 * Specifies the states of the calibration FSM.
 * 
 * \see
 * subs_calibration_run | s_eStatus
 */
typedef enum {
	STATE__NOT_CALIBRATED = 0, ///< Entry state of the FSM. The calibration values are read from the EEPROM.
	STATE__WHITE_LEVEL, ///< The e-puck drives a specified amount of steps and measures the ground which is the new white level.
	STATE__RETURN, ///< The e-puck returns to the start position and performs the calibration with the black and white levels.
	STATE__CALIBRATED, ///< The selector needs to be switched before a new calibration can be started.
	STATE__WAIT ///< The e-puck checks the selector and measures the ground (which is the new black level) if calibration is selected.
} EState_t;


static EState_t s_eStatus = STATE__NOT_CALIBRATED;
static sen_line_SData_t s_podLevels[2];
static int16_t s_ai16SpeedBackup[2];
static uint16_t s_aui16StepCounterBackup[2];


/*!
 * \brief
 * Collects calibration values of the ground-sensors smartphone.
 *
 * \returns
 * True if the calibration has not been performed yet, false otherwise.
 * 
 * If the e-puck is correctly set up above a black line and its selector is at position '0' sensor data are collected.
 * After driving a few centimeters the robot is supposed to be above a white pane and collects again sensor data for the calibration.
 * These values are stored in the EEPROM.
 *
 * \see
 * subs_calibrate_reset
 */
bool subs_calibration_run( void) {

	bool blActed = false;

	switch( s_eStatus) {
		case STATE__NOT_CALIBRATED: {
			hal_nvm_readEEPROM( 0, s_podLevels, sizeof( s_podLevels));
			(void)sen_line_calibrate( &s_podLevels[BLACK_LEVEL], &s_podLevels[WHITE_LEVEL]);
			s_eStatus = STATE__WAIT;
			break;
		}
		case STATE__WHITE_LEVEL: {
			if( hal_motors_getStepsLeft() >= SUBS_CALIBRATION_DISTANCE &&
				hal_motors_getStepsRight() >= SUBS_CALIBRATION_DISTANCE) {

				sen_line_read( &s_podLevels[WHITE_LEVEL]);
				hal_motors_setSpeed( -SUBS_CALIBRATION_SPEED, 0);
				hal_motors_setSteps( 0);
				s_eStatus = STATE__RETURN;
			}
			blActed = true;
			break;
		}
		case STATE__RETURN: {
			if( hal_motors_getStepsLeft() >= SUBS_CALIBRATION_DISTANCE &&
				hal_motors_getStepsRight() >= SUBS_CALIBRATION_DISTANCE) {

				hal_nvm_writeEEPROM( 0, s_podLevels, sizeof( s_podLevels));
				(void)sen_line_calibrate( &s_podLevels[BLACK_LEVEL], &s_podLevels[WHITE_LEVEL]);
				hal_motors_setSpeedLeft( s_ai16SpeedBackup[LEFT_MOTOR]);
				hal_motors_setSpeedRight( s_ai16SpeedBackup[RIGHT_MOTOR]);
				hal_motors_setStepsLeft( s_aui16StepCounterBackup[LEFT_MOTOR]);
				hal_motors_setStepsRight( s_aui16StepCounterBackup[RIGHT_MOTOR]);
				s_eStatus = STATE__CALIBRATED;
			}
			blActed = true;
			break;
		}
		case STATE__CALIBRATED: {
			if( hal_sel_getPosition() != SUBS_CALIBRATION_SELECTOR) {
				s_eStatus = STATE__WAIT;
			}
			break;
		}
		case STATE__WAIT: {
			if( hal_sel_getPosition() == SUBS_CALIBRATION_SELECTOR) {
				sen_line_read( &s_podLevels[BLACK_LEVEL]);
				s_ai16SpeedBackup[LEFT_MOTOR] = hal_motors_getSpeedLeft();
				s_ai16SpeedBackup[RIGHT_MOTOR] = hal_motors_getSpeedRight();
				hal_motors_setSpeed( SUBS_CALIBRATION_SPEED, 0);
				s_aui16StepCounterBackup[LEFT_MOTOR] = hal_motors_getStepsLeft();
				s_aui16StepCounterBackup[RIGHT_MOTOR] = hal_motors_getStepsRight();
				hal_motors_setSteps( 0);
				s_eStatus = STATE__WHITE_LEVEL;
				blActed = true;
			}
			break;
		}
		default: {
	
		}
	}

	return blActed;
}

/*!
 * \brief
 * Resets the calibration-data of the robot.
 *
 * Deletes all former calibration-data from the EEPROM.
 */
void subs_calibration_reset( void) {

	s_eStatus = STATE__NOT_CALIBRATED;
}
