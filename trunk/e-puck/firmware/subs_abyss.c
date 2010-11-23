#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_line.h"

#include "subs_abyss.h"

/*!
 * \brief
 * Decides if there is an abyss in front of the robot.
 * 
 * \returns
 * True if an abyss has been detected, false otherwise.
 * 
 * Reads the values of the ground-sensors and stops the motors if the data is below a critical value.
 * Afterwards a message is created and sent to the Smartphone via bluetooth.
 */
bool subs_abyss_run( void) {
	bool abyssDetected = false;
	int groundSensorValues[3];
	char buffer[50];
	const unsigned char I2C_address = 0xC0; // adress of ground sensors
	e_i2cp_enable();						//TODO e_i2cp_enable() : heiﬂt das bei uns anders?

    for(j = 0; j < 6; j++) {
		if( j % 2 == 0) {
			buffer[j] = e_i2cp_read(I2C_address, j + 1);
		} else if( j % 2 != 0) {
			buffer[j] = e_i2cp_read(I2C_address, j - 1);
		}
    }
    e_i2cp_disable();

	groundSensorValues[0] = (unsigned int) (buffer[0] & 0xff) + ((unsigned int) buffer[1] << 8);
	groundSensorValues[1] = (unsigned int) (buffer[2] & 0xff) + ((unsigned int) buffer[3] << 8);
	groundSensorValues[2] = (unsigned int) (buffer[4] & 0xff) + ((unsigned int) buffer[5] << 8);

	return false;
}
