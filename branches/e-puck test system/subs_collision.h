#ifndef subs_collision_h__
#define subs_collision_h__

#include "common.h"

#include "com_types.h"

enum {
	SUBS_COLLISION_SURFACE_THRESHOLD = 500, ///< Specifies the threshold-value above which the robot detects that there is no line under a line-sensor.
	SUBS_COLLISION_LINE_MEASUREMENTS = 5, ///< Specifies how many positive measurements in a row are necessary to detect a line.
	SUBS_COLLISION_LINE_THRESHOLD = 290, ///< Specifies the threshold for line-detection.
	SUBS_COLLISION_MOVE_BACKWARD_THRESHOLD = 295 ///< Specifies the number of steps above which it is possible to turn the e-puck by 180° and he will detect a node.
};

bool subs_collision_run( void);

void subs_collision_reset( void);

static inline const com_SMessage_t* subs_collision_getResponse( void);


/*!
 * \brief
 * Gets the last collision response message.
 * 
 * \returns
 * The collision response message.
 *
 * Subsumption layers are not allowed to send messages directly.
 * However, they may trigger the conquest message FSM to send messages by switching the subsumption state.
 *
 * The message data is filled by the layer when a collision is detected.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The message is invalid as long as no collision was detected.
 * 
 * \see
 * subs_collision_run | conquest_getState
 */
const com_SMessage_t* subs_collision_getResponse( void) {

	extern com_SMessage_t subs_collision_podResponse;

	return &subs_collision_podResponse;
}

#endif /* subs_collision_h__ */
