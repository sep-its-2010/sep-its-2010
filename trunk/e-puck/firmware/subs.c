#include <string.h>

#include "subs.h"

/*!
 * \brief
 * Contains all the available behavior list nodes.
 * 
 * \see
 * s_lppodFirstBehavior | subs_init
 */
static subs_SBehaviorPriorityList_t s_apodBehaviors[SUBS_MAX_BEHAVIORS];


/*!
 * \brief
 * Points to the first behavior list node.
 * 
 * \see
 * s_apodBehaviors | subs_init
 */
static subs_SBehaviorPriorityList_t* s_lppodFirstBehavior = NULL;


/*!
 * \brief
 * Initializes the subsumption architecture.
 * 
 * The priority list of the behaviors gets cleared.
 * 
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * subs_run | subs_reset | subs_register
 */
void subs_init( void) {

	memset( s_apodBehaviors, 0, sizeof( s_apodBehaviors) * sizeof( *s_apodBehaviors));
	s_lppodFirstBehavior = NULL;
}


/*!
 * \brief
 * Calls the action callbacks of all behaviors.
 * 
 * The action callbacks are called in decreasing priority until a callback returns true.
 * 
 * \remarks
 * An action callback may call #subs_register() and #subs_unregister().
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * subs_init | subs_reset
 */
void subs_run( void) {

	const subs_SBehaviorPriorityList_t* lppodCurrent = s_lppodFirstBehavior;
	bool blActed = false;
	while( lppodCurrent && !blActed) {
		const subs_fnRun_t fnRun = lppodCurrent->fnRun;
		lppodCurrent = lppodCurrent->lppodNext;
		blActed = fnRun();
	}
}


/*!
 * \brief
 * Calls the reset callbacks of all behaviors.
 * 
 * Reset callbacks are called - if they exist - in decreasing priority which will also insure a consistent state.
 * 
 * \remarks
 * A reset callback may call #subs_register() and #subs_unregister().
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * subs_init
 */
void subs_reset( void) {

	const subs_SBehaviorPriorityList_t* lppodCurrent = s_lppodFirstBehavior;
	while( lppodCurrent) {
		const subs_fnReset_t fnReset = lppodCurrent->fnReset;
		lppodCurrent = lppodCurrent->lppodNext;
		if( fnReset) {
			fnReset();
		}
	}
}


/*!
 * \brief
 * Registers a new behavior.
 * 
 * \param _fnRunCallback
 * Specifies the action callback which is called by #subs_run().
 * 
 * \param _fnResetCallback
 * Specifies an optional reset callback which is called by #subs_reset().
 * 
 * \param _ui16Priority
 * Specifies the priority of the new behavior.
 * 
 * \returns
 * - true: Insertion was successful.
 * - false: The behavior already exists or there is not enough space to register another behavior.
 * 
 * Registers a new behavior which is uniquely identified by its action callback. Optionally, a reset callback can be
 * specified. The behavior is inserted into the list right behind the last entry with a higher or equal priority.
 *
 * The reset callback - if it exists - is called after a successful registration.
 *
 * \remarks
 * An action callback must return true if it triggered and false otherwise.
 * 
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * subs_init | subs_unregister | subs_run | subs_reset
 */
bool subs_register(
	IN const subs_fnRun_t _fnRunCallback,
	IN OPT const subs_fnReset_t _fnResetCallback,
	IN const uint16_t _ui16Priority
	) {

	bool blSuccess = false;

	if( _fnRunCallback) {
		if( s_lppodFirstBehavior) {
			subs_SBehaviorPriorityList_t* lppodNextFree = NULL;
			for( uint16_t ui16 = 0; !lppodNextFree && ui16 < SUBS_MAX_BEHAVIORS; ui16++) {
				if( !s_apodBehaviors[ui16].fnRun || s_apodBehaviors[ui16].fnRun == _fnRunCallback) {
					lppodNextFree = &s_apodBehaviors[ui16];
				}
			}

			if( lppodNextFree && lppodNextFree->fnRun != _fnRunCallback) {
				subs_SBehaviorPriorityList_t* lppodPrev = s_lppodFirstBehavior;
//TODO: sorted insert

				blSuccess = true;
			}
		} else {
			s_lppodFirstBehavior = s_apodBehaviors;
			s_lppodFirstBehavior->fnReset = _fnResetCallback;
			s_lppodFirstBehavior->fnRun = _fnRunCallback;
			s_lppodFirstBehavior->lppodNext = NULL;
			s_lppodFirstBehavior->ui16Priotity = _ui16Priority;

			blSuccess = true;
		}
	}

	if( blSuccess && _fnResetCallback) {
		_fnResetCallback();
	}

	return blSuccess;
}


/*!
 * \brief
 * Unregisters a specified behavior.
 * 
 * \param _fnRunCallback
 * Specifies the behaviors action callback.
 * 
 * The behavior to be unregistered is found based on its action callback.
 * No action is taken in case the associated behavior does not exist.
 * 
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * subs_init | subs_register
 */
void subs_unregister(
	IN const subs_fnRun_t _fnRunCallback
	) {

}