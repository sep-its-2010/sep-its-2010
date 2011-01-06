#include <p30f6014A.h>
#include <libpic30.h>

#include "hal_led.h"
#include "hal_sel.h"
#include "hal_motors.h"
#include "hal_nvm.h"
#include "hal_rtc.h"
#include "sen_line.h"
#include "conquest.h"

#include "subs_calibration.h"


enum {
	BLACK_LEVEL = 0, ///< Specifies the local index for the black level calibration values.
	WHITE_LEVEL = 1, ///< Specifies the local index for the white level calibration values.
	BLINK_AMOUNT = 6, ///< Specifies the amount of LED blinks when a calibration error occurs.
	BLINK_MASK = 0xFF, ///< Specifies the LEDs to be toggled when a calibration error occurs.
	BLINK_INTERVAL = 100 ///< Specifies the blink interval in time units defined by #hal_rtc_init().
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


static void cbInvalidCalibrationBlinker(
	IN const hal_rtc_handle_t _hEvent
	);


/*!
 * \brief
 * Holds the current calibration state.
 * 
 * \see
 * subs_calibration_reset
 */
static EState_t s_eStatus = STATE__NOT_CALIBRATED;


/*!
 * \brief
 * Holds the back level and white level calibration values.
 * 
 * \see
 * subs_calibration_run
 */
static sen_line_SData_t s_podCalibrationLevels[2] _EEDATA( 2);


/*!
 * \brief
 * Holds the current toggle count of the calibration error visualization.
 * 
 * \see
 * cbInvalidCalibrationBlinker
 */
static uint16_t s_ui16ToggleCount = 0;


/*!
 * \brief
 * Holds the handle to the calibration error visualization event.
 * 
 * \remarks
 * #subs_calibration_reset() needs to be called to register the event. Otherwise the handle is invalid.
 * 
 * \see
 * cbInvalidCalibrationBlinker
 */
static hal_rtc_handle_t s_hBlinkEvent = HAL_RTC_INVALID_HANDLE;


/*!
 * \brief
 * Visualizes a calibration error by blinking LEDs.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * In case the back level and white level calibration values are invalid this event is activated.
 * 
 * \remarks
 * #subs_calibration_reset() needs to be called to register the event.
 *
 * \see
 * sen_line_calibrate | s_hBlinkEvent
 */
void cbInvalidCalibrationBlinker(
	IN const hal_rtc_handle_t _hEvent
	) {

	// 1 blink := toggle LEDs twice
	if( ++s_ui16ToggleCount > BLINK_AMOUNT * 2) {
		s_ui16ToggleCount = 0;
		hal_rtc_deactivate( _hEvent);
		hal_rtc_reset( _hEvent);
	} else {
		hal_led_toggle( BLINK_MASK);
	}
}


/*!
 * \brief
 * Subsumption layer for line sensor calibration.
 * 
 * \returns
 * - \c true: calibration active
 * - \c false: calibration inactive
 * 
 * When no calibration has taken place so far, the calibration values are read from the EEPROM. If these values are invalid, the
 * error visualization event (#cbInvalidCalibrationBlinker()) is activated.
 *
 * When the initial calibration has taken place already, the selector is switched to #SUBS_CALIBRATION_SELECTOR and there is no
 * move intention (#conquest_getState()), the measurement routine is entered.
 * This requires that the e-puck is standing on the black level underground with all three line sensors.
 * The line sensors are read and their values are buffered as the new black level. Then the robot drives a specified amount of
 * steps (#SUBS_CALIBRATION_DISTANCE). It is now supposed to stand on the white level underground. Another measurement is taken
 * which is buffered as the new white level. After returning to its point of origin the line sensors are calibrated
 * (#sen_line_calibrate) and the buffered values are stored in the EEPROM on success.
 * 
 * \remarks
 * - #subs_line_reset() should be called to initialize the layer.
 * - Any ongoing higher subsumption actions will be interrupted and resumed after this layer finishes.
 *
 * \warning
 * - The motors abstraction layer needs to be initialized (#hal_motors_init()).
 * - The I2C abstraction layer needs to be initialized (#hal_i2c_init()).
 */
bool subs_calibration_run( void) {

	static sen_line_SData_t s_podLevels[2];
	static hal_motors_SSettings_t s_podSettings;

	bool blActed = false;

	switch( s_eStatus) {
		case STATE__NOT_CALIBRATED: {
			_prog_addressT addrCalibrationValues;
			_init_prog_address( addrCalibrationValues, s_podCalibrationLevels);
			hal_nvm_readEEPROM( addrCalibrationValues, s_podLevels, sizeof( s_podLevels));
			if( !sen_line_calibrate( &s_podLevels[BLACK_LEVEL], &s_podLevels[WHITE_LEVEL])) {
				hal_rtc_activate( s_hBlinkEvent);
			}
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

				if( !sen_line_calibrate( &s_podLevels[BLACK_LEVEL], &s_podLevels[WHITE_LEVEL])) {
					hal_rtc_activate( s_hBlinkEvent);
				} else {
					_prog_addressT addrCalibrationValues;
					_init_prog_address( addrCalibrationValues, s_podCalibrationLevels);
					hal_nvm_writeEEPROM( addrCalibrationValues, s_podLevels, sizeof( s_podLevels));
				}
				hal_motors_restoreSettings( &s_podSettings);
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
			if( hal_sel_getPosition() == SUBS_CALIBRATION_SELECTOR && conquest_getState() == CONQUEST_STATE__STOP) {
				sen_line_read( &s_podLevels[BLACK_LEVEL]);
				hal_motors_backupSettings( &s_podSettings);
				hal_motors_setSpeed( SUBS_CALIBRATION_SPEED, 0);
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
 * Resets the calibration layer.
 * 
 * The current state of the calibration layer is reset and the blinker event is registered if this did not already happen.
 * The event is initially inactive and will never be unregistered.
 * 
 * \warning
 * - The LED abstraction layer needs to be initialized (#hal_led_init()).
 * - The real time clock needs to be initialized (#hal_rtc_init()).
 * 
 * \see
 * subs_calibration_run
 */
void subs_calibration_reset( void) {

	if( s_hBlinkEvent == HAL_RTC_INVALID_HANDLE) {
		s_hBlinkEvent = hal_rtc_register( cbInvalidCalibrationBlinker, BLINK_INTERVAL, false);
	} else {
		hal_rtc_deactivate( s_hBlinkEvent);
		hal_rtc_reset( s_hBlinkEvent);
	}
	s_eStatus = STATE__NOT_CALIBRATED;
	s_ui16ToggleCount = 0;
	hal_led_switchOff( BLINK_MASK);
}
