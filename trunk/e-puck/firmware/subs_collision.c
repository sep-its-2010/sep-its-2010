#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_prox.h"

#include "subs_collision.h"

/*!
 * \brief
 * Detects collisions between e-puck and obstacles in the near future.
 * 
 * \returns
 * True if a collision has been detected, false otherwise.
 *
 * Reads the values of the eight proximity sensors, which are located around the e-puck.
 * If any of these values is above a critical value a collision is detected and the motors are stopped.
 * After that a message, which contains the direction of the obstacle is created and sent to the Smartphone via bluetooth.
 *
 * \see
 * subs_collision_reset
 */
bool subs_collision_run( void) {
	bool blCollisionDetected = false;
	sen_prox_SData_t podSensorData;
	sen_prox_getCurrent( &podSensorData);
	

	return blCollisionDetected;
}

/*!
 * \brief
 * Reset all collision-data to default values.
 * 
 * Deletes all values of the IR-sensors concerning the last collision.
 */
void subs_collision_reset( void) {
}
