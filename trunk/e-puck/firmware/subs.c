#include <string.h>

#include "subs.h"

static subs_SBehaviorPriorityList_t apodBehaviors[SUBS_MAX_BEHAVIORS];
subs_SBehaviorPriorityList_t* lppodFirstBehavior = NULL;

/*!
 * \brief
 * Initializes the container for the different behaviors.
 */
void subs_init( void) {

	memset( apodBehaviors, 0, sizeof( apodBehaviors) * sizeof( *apodBehaviors));
	lppodFirstBehavior = NULL;
}

/*!
 * \brief
 * Coordinates the different behaviors of the subsumption-architecture.
 * 
 * Manages a list of all registered behaviors. While endlessly traversing the list in a loop, every behavior checks if it is supposed to be executed.
 * If a behavior has been executed the loop continues at the first element of the behavior-list.
 * 
 * \see
 * subs_init
 */
void subs_run( void) {

	const subs_SBehaviorPriorityList_t* lppodCurrent = lppodFirstBehavior;
	while( lppodCurrent && lppodCurrent->fnRun()) {
		lppodCurrent = lppodCurrent->lppodNext;
	}
}

void subs_reset( void) {

	const subs_SBehaviorPriorityList_t* lppodCurrent = lppodFirstBehavior;
	while( lppodCurrent) {
		if( lppodCurrent->fnReset) {
			lppodCurrent->fnReset();
		}
		lppodCurrent = lppodCurrent->lppodNext;
	}
}

bool subs_register(
	IN const subs_fnReset_t _fnResetCallback,
	IN const subs_fnRun_t _fnRunCallback,
	IN const uint16_t _ui16Priority
	) {

	bool blSuccess = false;

	if( _fnRunCallback) {
		if( lppodFirstBehavior) {
			subs_SBehaviorPriorityList_t* lppodNextFree = NULL;
			for( uint16_t ui16 = 0; !lppodNextFree && ui16 < SUBS_MAX_BEHAVIORS; ui16++) {
				if( !apodBehaviors[ui16].fnRun || apodBehaviors[ui16].fnRun == _fnRunCallback) {
					lppodNextFree = &apodBehaviors[ui16];
				}
			}

			if( lppodNextFree && lppodNextFree->fnRun != _fnRunCallback) {
				subs_SBehaviorPriorityList_t* lppodPrev = lppodFirstBehavior;
//TODO: sorted insert

				blSuccess = true;
			}
		} else {
			lppodFirstBehavior = apodBehaviors;
			lppodFirstBehavior->fnReset = _fnResetCallback;
			lppodFirstBehavior->fnRun = _fnRunCallback;
			lppodFirstBehavior->lppodNext = NULL;
			lppodFirstBehavior->ui16Priotity = _ui16Priority;

			blSuccess = true;
		}
	}

	return blSuccess;
}
