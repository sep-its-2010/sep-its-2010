#include <string.h>

#include "com.h"
#include "hal_motors.h"
#include "hal_led.h"
#include "sen_prox.h"

#include "subs_collision.h"

enum {
	SUBS_COLLISION_THRESHOLD = 64 ///< Specifies the threshold-value above which a collision will be detected.
};

static com_SMessage_t s_podCollisionMessage = {COM_MESSAGE_TYPE__RESPONSE_COLLISION, {0}};

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
	
	// Check all IR-sensors for collision
	for( uint8_t i = 0; i < sizeof(podSensorData.aui8Data); i++) {
		if( podSensorData.aui8Data[i] > SUBS_COLLISION_THRESHOLD) { // TODO reichen 8 bit für die Sensorwerte?
			hal_motors_setSpeed( 0, 0);
			hal_motors_setSteps( 0);
			s_podCollisionMessage.aui8Data[i] = true;
			blCollisionDetected = true;
		}
	}

	if( blCollisionDetected) {
		com_send( &s_podCollisionMessage);
	}

	return blCollisionDetected;
}

/*!
 * \brief
 * Reset all collision-data to default values.
 * 
 * Deletes all values of the IR-sensors concerning the last collision.
 */
void subs_collision_reset( void) {
	memset( s_podCollisionMessage.aui8Data, 0, sizeof(s_podCollisionMessage.aui8Data));
}
