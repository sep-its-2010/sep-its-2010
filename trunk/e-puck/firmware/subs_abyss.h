#ifndef subs_abyss_h__
#define subs_abyss_h__

#include "common.h"

#include "com_types.h"


enum {
	SUBS_ABYSS_REGRESSION = 200, ///< Specifies the amount of steps to drive backwards in case an abyss is detected.
	SUBS_ABYSS_REQUIRED_POSITIVES = 3 ///< Specifies the amount of successive positive abyss detections in order to trigger the prevention.
};


bool subs_abyss_run( void);

void subs_abyss_reset( void);

static inline const com_SMessage_t* subs_abyss_getResponse( void);


/*!
 * \brief
 * Gets the last abyss response message.
 * 
 * \returns
 * The abyss response message.
 *
 * Subsumption layers are not allowed to send messages directly.
 * However, they may trigger the conquest message FSM to send messages by switching the subsumption state.
 *
 * The message data is filled by the layer when an abyss is detected.
 * 
 * \remarks
 * - This function is interrupt safe.
 * - The message is invalid as long as no abyss was detected.
 * 
 * \see
 * subs_collision_run | conquest_getState
 */
const com_SMessage_t* subs_abyss_getResponse( void) {

	extern com_SMessage_t subs_abyss_podResponse;

	return &subs_abyss_podResponse;
}

#endif /* subs_abyss_h__ */
