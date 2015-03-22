#ifndef fsm_types_h__
#define fsm_types_h__

#include "common.h"


enum {
	FSM_NUM_STATES = 16 ///< Specifies the number of FSM states per context.
};


/*!
 * \brief
 * Specifies a FSM enter, update or exit action callback.
 */
typedef void (*fsm_fnAction_t)( void);


/*!
 * \brief
 * Specifies the context of a single FSM state.
 * 
 * \see
 * fsm_SContext_t
 */
typedef struct {
	fsm_fnAction_t fnOnEnter; ///< Holds the optional action callback which is executed when the state is (re-)entered.
	fsm_fnAction_t fnOnUpdate; ///< Holds the optional action callback which is executed when the state is updated.
	fsm_fnAction_t fnOnExit; ///< Holds the optional action callback which is executed when the state is left.
} fsm_SState_t;


/*!
 * \brief
 * Specifies the context of a FSM.
 * 
 * \see
 * fsm_init
 */
typedef struct {
	volatile uint16_t ui16CurrentState; ///< Holds the index of the current active state.
	fsm_SState_t apodStates[FSM_NUM_STATES]; ///< Holds the context of each FSM state.
} fsm_SContext_t;


#endif /* fsm_types_h__ */
