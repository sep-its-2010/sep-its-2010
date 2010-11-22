#include "hal_motors.h"
#include "hal_sel.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_calibration.h"

bool isNotCalibrated = true;
static int	l_calibrate[3] = {0,0,0};	// soll im EEPROM gespeichert werden
int l_buffer[3];						// speichert Kalibrierungsdaten tempor‰r

/*!
 * \brief
 * Collects calibration values of the ground-sensors.
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

	uint8_t selector = hal_sel_getPosition( void);

	if( isNotCalibrated && (selector == 0)) {

		//perform calibration
		const unsigned char I2C_address = 0xC0; // adress of ground sensors
		e_i2cp_enable();  //TODO e_i2cp_enable() : heiﬂt das bei uns anders?

        for(j = 0; j < 6; j++) {
			if( j % 2 == 0) {
				buffer[j] = e_i2cp_read(I2C_address, j + 1);
			} else if( j % 2 != 0) {
				buffer[j] = e_i2cp_read(I2C_address, j - 1);
			}
        }
        e_i2cp_disable();
		l_buffer[0] = (unsigned int) (buffer[0] & 0xff) + ((unsigned int) buffer[1] << 8);
		l_buffer[1] = (unsigned int) (buffer[2] & 0xff) + ((unsigned int) buffer[3] << 8);
		l_buffer[2] = (unsigned int) (buffer[4] & 0xff) + ((unsigned int) buffer[5] << 8);

		l_calibrate[0] = l_buffer[0];
		l_calibrate[1] = l_buffer[1];
		l_calibrate[2] = l_buffer[2];

		isNotCalibrated = false;
	} else if( !isNotCalibrated) {
		// groundsensors are already calibrated => next behavior can be run		
	}
	//TODO Kalibrierung sollte nur 1 mal bei Systemstart und nach Reset erfolgen

	return isNotCalibrated;
}

/*!
 * \brief
 * Resets the calibration-data of the robot.
 *
 * Deletes all former calibration-data from the EEPROM.
 */
void subs_calibration_reset( void) {

}
