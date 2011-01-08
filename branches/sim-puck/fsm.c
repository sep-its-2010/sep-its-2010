#include <string.h>

#include "fsm.h"


/*!
 * \brief
 * Initializes the specifies FSM.
 * 
 * \param _lppodContext
 * Specifies the FSM to operate on.
 * 
 * Resets all action callbacks. The active state is set to \c 0.
 * Any FSM must be initializes prior to operation.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module and the same FSM.
 * 
 * \see
 * fsm_configureState | fsm_switch | fsm_update
 */
void fsm_init(
	OUT fsm_SContext_t* const _lppodContext
	) {

	memset( _lppodContext, 0, sizeof( *_lppodContext));
}


/*!
 * \brief
 * Configures a single state of a specified FSM.
 * 
 * \param _lppodContext
 * Specifies the FSM to operate on.
 * 
 * \param _ui16State
 * Specifies the state to be configured.
 * 
 * \param _fnOnEnter
 * Specifies an optional action callback which is executed when the this state is (re-)entered.
 * 
 * \param _fnOnUpdate
 * Specifies an optional action callback which is executed when #fsm_update() is called.
 * 
 * \param _fnOnExit
 * Specifies an optional action callback which is executed when the this state is left.
 * 
 * \returns
 * - \c true: on success.
 * - \c false: on failure; the specified state does not exist. 
 * 
 * A state does not need to have any action callbacks. A state can be configured when it is either active or inactive.
 * 
 * \warning
 * This function may not be preempted by any function (except #fsm_getState()) which accesses this module and the same FSM.
 * 
 * \see
 * fsm_init | fsm_switch
 */
bool fsm_configureState(
	OUT fsm_SContext_t* const _lppodContext,
	IN const uint16_t _ui16State,
	IN OPT const fsm_fnAction_t _fnOnEnter,
	IN OPT const fsm_fnAction_t _fnOnUpdate,
	IN OPT const fsm_fnAction_t _fnOnExit
	) {

	bool blSuccess = false;

	if( _ui16State < FSM_NUM_STATES) {
		_lppodContext->apodStates[_ui16State].fnOnEnter = _fnOnEnter;
		_lppodContext->apodStates[_ui16State].fnOnUpdate = _fnOnUpdate;
		_lppodContext->apodStates[_ui16State].fnOnExit = _fnOnExit;

		blSuccess = true;
	}

	return blSuccess;
}


/*!
 * \brief
 * Switches the active FSM state.
 * 
 * \param _lppodContext
 * Specifies the FSM to operate on.
 * 
 * \param _ui16State
 * Specifies the new active state.
 * 
 * \returns
 * - \c true: on success.
 * - \c false: on failure; the specified state does not exist.
 * 
 * First, the exit action callback of the current state is called if it exists. Then the state index is switched.
 * Finally, the enter action callback of the new active state is called if it exists.
 * 
 * \remarks
 * Reentering an active state has the effect described above.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module and the same FSM.
 * 
 * \see
 * fsm_init | fsm_configureState | fsm_getState
 */
bool fsm_switch(
	INOUT fsm_SContext_t* const _lppodContext,
	IN const uint16_t _ui16State
	) {

	bool blSuccess = false;

	if( _ui16State < FSM_NUM_STATES) {
		const fsm_fnAction_t fnExit = _lppodContext->apodStates[_lppodContext->ui16CurrentState].fnOnExit;
		if( fnExit) {
			fnExit();
		}
		_lppodContext->ui16CurrentState = _ui16State;
		const fsm_fnAction_t fnEnter = _lppodContext->apodStates[_ui16State].fnOnEnter;
		if( fnEnter) {
			fnEnter();
		}

		blSuccess = true;
	}

	return blSuccess;
}


/*!
 * \brief
 * Updates the active state of the specified FSM.
 * 
 * \param _lppodContext
 * Specifies the FSM to operate on.
 * 
 * Calls the update action callback of the active state if it exists.
 * 
 * \warning
 * This function may not be preempted by any function (except #fsm_getState()) which accesses this module and the same FSM.
 *
 * \see
 * fsm_init | fsm_configureState | fsm_getState
 */
void fsm_update(
	IN const fsm_SContext_t* const _lppodContext
	) {

	const fsm_fnAction_t fnUpdate = _lppodContext->apodStates[_lppodContext->ui16CurrentState].fnOnUpdate;
	if( fnUpdate) {
		fnUpdate();
	}
}
