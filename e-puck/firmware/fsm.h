#ifndef fsm_h__
#define fsm_h__

#include "common.h"

#include "fsm_types.h"


void fsm_init(
	OUT fsm_SContext_t* const _lppodContext
	);

bool fsm_configureState(
	OUT fsm_SContext_t* const _lppodContext,
	IN const uint16_t _ui16State,
	IN OPT const fsm_fnAction_t _fnOnEnter,
	IN OPT const fsm_fnAction_t _fnOnUpdate,
	IN OPT const fsm_fnAction_t _fnOnExit
	);

bool fsm_switch(
	INOUT fsm_SContext_t* const _lppodContext,
	IN const uint16_t _ui16State
	);

void fsm_update(
	IN const fsm_SContext_t* const _lppodContext
	);

static inline uint16_t fsm_getState(
	IN const fsm_SContext_t* const _lppodContext
	);


/*!
 * \brief
 * Returns the current active state of the specifies FSM.
 * 
 * \param _lppodContext
 * Specifies the FSM to operate on.
 * 
 * \returns
 * An index ranging from \c 0 to \c #FSM_NUM_STATES \c - \c 1.
 * 
 * \remarks
 * This function is interrupt safe.
 *
 * \warning
 * The FSM must be initialized or results are unpredictable.
 * 
 * \see
 * fsm_init | fsm_switch
 */
uint16_t fsm_getState(
	IN const fsm_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16CurrentState;
}

#endif /* fsm_h__ */
