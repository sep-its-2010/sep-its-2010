#include <string.h>
#include <stdlib.h>

#include "hal_rtc.h"
#include "hal_led.h"
#include "hal_int.h"
#include "com.h"
#include "sen_line.h"
#include "sen_prox.h"

#include "subs.h"
#include "subs_abyss.h"
#include "subs_calibration.h"
#include "subs_collision.h"
#include "subs_initial.h"
#include "subs_line.h"
#include "subs_movement.h"
#include "subs_node.h"

#include "fsm.h"

#include "conquest.h"


enum {
	SET_SPEED_MULTIPLIER = 10, ///< Specifies the speed factor which is applied to the set speed request.
	SYS_UP_TIME_STATUS_OFFSET = 0, ///< Specifies the system-up-time offset within the status response message.
	ABYSS_STATUS_OFFSET = SYS_UP_TIME_STATUS_OFFSET + sizeof( uint32_t), ///< Specifies the abyss status offset within the status response message.
	COLLISION_STATUS_OFFSET = ABYSS_STATUS_OFFSET + SEN_LINE_NUM_SENSORS, ///< Specifies the collision status offset within the status response message.
	LAST_NODE_STATUS_OFFSET = COLLISION_STATUS_OFFSET + SEN_PROX_NUM_SENSORS, ///< Specifies the last node type offset within the status response message.
};


/*!
 * \brief
 * Holds the current subsumption state.
 * 
 * \see
 * conquest_getState | conquest_setState
 */
static fsm_SContext_t s_podSubsumptionFSM;


/*!
 * \brief
 * Holds the current message processing state.
 */
static fsm_SContext_t s_podMessageFSM;


/*!
 * \brief
 * Holds the last received message.
 *
 * This is needed for message synchronization.
 */
static com_SMessage_t s_podMessage;


/*!
 * \brief
 * Holds the node type of the last visited node.
 * 
 * \see
 * conquest_getLastNode | conquest_setLastNode
 */
volatile conquest_ENode_t conquest_eLastNodeType = CONQUEST_NODE__INVALID;


/*!
 * \brief
 * Holds the line speed requested by the user.
 * 
 * \see
 * conquest_getRequestedLineSpeed | conquest_setRequestedLineSpeed
 */
volatile uint16_t conquest_ui16Speed = CONQUEST_INITIAL_SPEED;


static bool cbHandleDefault(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestStatus(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestReset(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestSetLED(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestSetSpeed(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestMove(
	IN const com_SMessage_t* const _lppodMessage
	);
static bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	);

static void cbMoveEntry( void);
static void cbMoveExit( void);

static void cbSyncRequestStatus( void);
static void cbSyncRequestReset( void);
static void cbSyncRequestSetLED( void);
static void cbSyncRequestSetSpeed( void);
static void cbSyncRequestMove( void);
static void cbSyncRequestTurn( void);


static void cbHeartbeat(
	IN const hal_rtc_handle_t UNUSED _hEvent
	);


/*!
 * \brief
 * Default message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * \c true; the return value of the default handler is not relevant.
 * 
 * Any message which arrives at the default handler is acknowledged with a #CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED response.
 *
 * A message can either be rejected because the type or the data is invalid or another message is being processed.
 *
 * The handler is synchronized to #cbHeartbeat().
 *
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * conquest_init | com_setDefault
 */
bool cbHandleDefault(
	IN const com_SMessage_t UNUSED* const _lppodMessage
	) {

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_REJECTED;
	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
		com_send( &podResponse);
	}

	return true;
}


/*!
 * \brief
 * Status request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A status request is accepted if the subsumption state is either #CONQUEST_STATE__START, #CONQUEST_STATE__STOP or
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__GET_STATUS is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestStatus | com_register
 */
bool cbHandleRequestStatus(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_STATUS) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			const conquest_EState_t eState = conquest_getState();
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__GET_STATUS);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Reset request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A reset request is accepted if the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__RESET is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestReset | com_register
 */
bool cbHandleRequestReset(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_RESET) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__RESET);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * LED change request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A LED change request is accepted if the subsumption state is either #CONQUEST_STATE__START, #CONQUEST_STATE__STOP or
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__SET_LED is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestSetLED | com_register
 */
bool cbHandleRequestSetLED(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_SET_LED) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			const conquest_EState_t eState = conquest_getState();
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__SET_LED);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Speed change request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A speed change request is accepted if the subsumption state is either #CONQUEST_STATE__START, #CONQUEST_STATE__STOP or
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__SET_SPEED is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestSetSpeed | com_register
 */
bool cbHandleRequestSetSpeed(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_SET_SPEED) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			const conquest_EState_t eState = conquest_getState();
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__SET_SPEED);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Move request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A move request is accepted if the subsumption state is #CONQUEST_STATE__STOP
 * and the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__MOVE is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestMove | com_register
 */
bool cbHandleRequestMove(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_MOVE) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE &&
				conquest_getState() == CONQUEST_STATE__STOP &&
				( conquest_getLastNode() & CONQUEST_DIRECTION__UP)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__MOVE);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Turn request message handler callback.
 * 
 * \param _lppodMessage
 * Specifies the message to be analyzed.
 * 
 * \returns
 * - \c true: message accepted and forwarded.
 * - \c false: message did not match the pattern (wrong type or wrong state).
 * 
 * A turn request is accepted if the subsumption state is #CONQUEST_STATE__STOP
 * and the message FSM is not busy (#CONQUEST_MESSSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSSAGE_STATE__TURN is entered.
 *
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 * 
 * \see
 * cbSyncRequestTurn | com_register
 */
bool cbHandleRequestTurn(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;
	
	if( _lppodMessage->ui16Type == CONQUEST_MESSAGE_TYPE__REQUEST_TURN) {
		HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSSAGE_STATE__NONE &&
				conquest_getState() == CONQUEST_STATE__STOP) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__TURN);

				memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

				blHandledMessage = true;
			}
		}
	}

	return blHandledMessage;
}


/*!
 * \brief
 * Synchronized status request handler.
 * 
 * Switches the subsumption state to #CONQUEST_STATE__INITIAL when currently in #CONQUEST_STATE__START. This will trigger
 * the initial node detection which will then pass on to #CONQUEST_STATE__STOP.
 *
 * When in #CONQUEST_STATE__STOP or #CONQUEST_STATE__ABYSS, a status response message is created (#CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS) and sent.
 * This message includes a timestamp in [ms], abyss detection bits and collision detection bits for each sensor and the last node type.
 * Finally, the message FSM switches to #CONQUEST_MESSSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestStatus
 */
void cbSyncRequestStatus( void) {

	if( conquest_getState() == CONQUEST_STATE__START) {
		conquest_setState( CONQUEST_STATE__INITIAL);
	} else if( conquest_getState() == CONQUEST_STATE__STOP || conquest_getState() == CONQUEST_STATE__ABYSS) {
		com_SMessage_t podResponse;
		podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS;

		// System up-time in little endian format
		uint32_t ui32UpTime = hal_rtc_getSystemUpTime();
		memcpy( &podResponse.aui8Data[SYS_UP_TIME_STATUS_OFFSET], &ui32UpTime, sizeof( ui32UpTime));

		// Abyss status
		sen_line_SData_t podLine;
		sen_line_read( &podLine);
		sen_line_rescale( &podLine, &podLine);
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__LEFT] = podLine.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD;
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__MIDDLE] = podLine.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD;
		podResponse.aui8Data[ABYSS_STATUS_OFFSET + SEN_LINE_SENSOR__RIGHT] = podLine.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD;

		// Collision status
		sen_prox_SData_t podProxSensorData;
		sen_prox_getCurrent( &podProxSensorData);
		for( uint16_t ui16 = 0; ui16 < SEN_PROX_NUM_SENSORS; ui16++) {
			podResponse.aui8Data[COLLISION_STATUS_OFFSET + ui16] = podProxSensorData.aui8Data[ui16] > SUBS_COLLISION_THRESHOLD;
		}

		podResponse.aui8Data[LAST_NODE_STATUS_OFFSET] = conquest_getLastNode() >> 8;

		com_send( &podResponse);

		fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
	}
}


/*!
 * \brief
 * Synchronized reset request handler.
 *
 * Resets the whole conquest logic and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 *
 * \remarks
 * The message FSM is switched to #CONQUEST_MESSSAGE_STATE__NONE by #conquest_reset().
 * 
 * \see
 * cbHandleRequestReset | conquest_reset
 */
void cbSyncRequestReset( void) {

	conquest_reset();

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	com_send( &podResponse);

	// conquest_reset() implies the FSM reset
//	fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
}


/*!
 * \brief
 * Synchronized LED change request handler.
 * 
 * Enforces the requested LEDs and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 * Finally, the message FSM switches to #CONQUEST_MESSSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestSetLED | hal_led_set
 */
void cbSyncRequestSetLED( void) {

	hal_led_set( s_podMessage.aui8Data[0] | ( s_podMessage.aui8Data[1] << 8));

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	com_send( &podResponse);

	fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
}


/*!
 * \brief
 * Synchronized speed change request handler.
 * 
 * Sets the new motor line speed and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 * Finally, the message FSM switches to #CONQUEST_MESSSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestSetSpeed | conquest_setRequestedLineSpeed | conquest_getRequestedLineSpeed
 */
void cbSyncRequestSetSpeed( void) {

	conquest_setRequestedLineSpeed( SET_SPEED_MULTIPLIER * s_podMessage.aui8Data[0]);

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	com_send( &podResponse);

	fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
}


/*!
 * \brief
 * Synchronized move request handler.
 * 
 * Initiates a move when the subsumption state is in #CONQUEST_STATE__STOP by switching to #CONQUEST_STATE__MOVE_FOWARD.
 * When reaching either #CONQUEST_STATE__COLLISION, #CONQUEST_STATE__ABYSS or #CONQUEST_STATE__HIT_NODE one of the following
 * response messages is sent respectively: #CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION, #CONQUEST_MESSAGE_TYPE__RESPONSE_ABYSS or
 * #CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE.
 * Finally, the message FSM switches to #CONQUEST_MESSSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestMove
 */
void cbSyncRequestMove( void) {

	// TODO: fix this hack; forces the node detection through subs_initial
	static bool s_blHACKforceDetect = false;

	switch( conquest_getState()) {
		case CONQUEST_STATE__STOP: {
			if( s_blHACKforceDetect) {
				s_blHACKforceDetect = false;

				com_SMessage_t podResponse;
				podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE;
				podResponse.aui8Data[0] = conquest_getLastNode() >> 8;
				com_send( &podResponse);
				fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
			} else {
				conquest_setState( CONQUEST_STATE__MOVE_FOWARD);
			}
			break;
		}
		case CONQUEST_STATE__COLLISION: {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION;
			com_send( &podResponse);
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
			conquest_setState( CONQUEST_STATE__STOP);
			break;
		}
		case CONQUEST_STATE__ABYSS: {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_ABYSS;
			com_send( &podResponse);
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
//			conquest_setState( CONQUEST_STATE__STOP);
			break;
		}
		case CONQUEST_STATE__HIT_NODE: {
			// TODO: Fix HACK
// 			com_SMessage_t podResponse;
// 			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE;
// 			podResponse.aui8Data[0] = 0xFA;
// 			com_send( &podResponse);
// 
// 			fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
// 			conquest_setState( CONQUEST_STATE__STOP);
			s_blHACKforceDetect = true;
			conquest_setState( CONQUEST_STATE__INITIAL);
			break;
		}
		default: {
	
		}
	}
}


/*!
 * \brief
 * Synchronized turn request handler.
 * 
 * Initiates a turn when the subsumption state is in #CONQUEST_STATE__STOP by switching to #CONQUEST_STATE__TURN_RIGHT or
 * #CONQUEST_STATE__TURN_LEFT when the requested turn count is either positive or negative.
 * The subsumption layer is required to switch back to #CONQUEST_STATE__STOP when the turn finished.
 * The turn count is reduced by one and as soon as it reaches zero, an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK) is sent.
 * Finally, the message FSM switches to #CONQUEST_MESSSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestTurn
 */
void cbSyncRequestTurn( void) {

	if( conquest_getState() == CONQUEST_STATE__STOP) {
		if( (int8_t)s_podMessage.aui8Data[0] > 0) {
			( (int8_t*)s_podMessage.aui8Data)[0]--;
			conquest_setState( CONQUEST_STATE__TURN_RIGHT);
		} else if( (int8_t)s_podMessage.aui8Data[0] < 0) {
			( (int8_t*)s_podMessage.aui8Data)[0]++;
			conquest_setState( CONQUEST_STATE__TURN_LEFT);
		} else {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
			com_send( &podResponse);

			fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
		}
	}
}


/*!
 * \brief
 * Wrapper function for the entry action callback of #CONQUEST_STATE__MOVE_FOWARD.
 * 
 * \see
 * cbMoveExit
 */
void cbMoveEntry( void) {

 	subs_register( subs_line_run, subs_line_reset, 0xAF);
	subs_register( subs_node_run, subs_node_reset, 0xBF);
 	subs_register( subs_movement_run, subs_movement_reset, 0xCF);
 	subs_register( subs_collision_run, subs_collision_reset, 0xDF);
	subs_register( subs_abyss_run, subs_abyss_reset, 0xEF);
	subs_line_reset();
	subs_node_reset();
	subs_movement_reset();
	subs_collision_reset();
	subs_abyss_reset();
}


/*!
 * \brief
 * Wrapper function for the exit action callback of #CONQUEST_STATE__MOVE_FOWARD.
 * 
 * \see
 * cbMoveEntry
 */
void cbMoveExit( void) {

	subs_unregister( subs_line_run);
	subs_unregister( subs_node_run);
	subs_unregister( subs_movement_run);
	subs_unregister( subs_collision_run);
	subs_unregister( subs_abyss_run);
}


/*!
 * \brief
 * Cyclic logic update callback.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * The heartbeat updates the subsumption and the message FSM.
 * 
 * \see
 * hal_rtc_register
 */
void cbHeartbeat(
	IN const hal_rtc_handle_t UNUSED _hEvent
	) {

	fsm_update( &s_podSubsumptionFSM);
	fsm_update( &s_podMessageFSM);
}


/*!
 * \brief
 * Initializes the conquest logic and management.
 * 
 * Configures the subsumption and the message FSM, registers the message handler callbacks and links the heartbeat event.
 *
 * \remarks
 * - The communication module needs to be initialized (#com_init()).
 * - The real time clock needs to be initialized (#hal_rtc_init()).
 * - Any module required by the subsumption layers or by the (synchronized) message handlers needs to be initialized, too.
 * 
 * \see
 * conquest_reset
 */
void conquest_init( void) {

	subs_init();

	fsm_init( &s_podSubsumptionFSM);

	// Quick & dirty (tm): return types do not matter when they are int compatible
	// -> cast valid even thou the spec says the result is undefined; any GCC version earlier than v4.2.0 can cope with that
	// see http://efreedom.com/Question/1-559581/Casting-Function-Pointer-Another-Type
	// A wrapper function would cost one opcode (3 bytes) as it would simply branch to the wrapped function
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__START,       subs_calibration_reset, (fsm_fnAction_t)subs_calibration_run, NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__CALIBRATION, NULL,                   (fsm_fnAction_t)subs_calibration_run, NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__INITIAL,     subs_initial_reset,     (fsm_fnAction_t)subs_initial_run,     NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__STOP,        NULL,                   NULL,                                 NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__MOVE_FOWARD, cbMoveEntry,            (fsm_fnAction_t)subs_run,             cbMoveExit);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__TURN_LEFT,   subs_movement_reset,    (fsm_fnAction_t)subs_movement_run,    NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__TURN_RIGHT,  subs_movement_reset,    (fsm_fnAction_t)subs_movement_run,    NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__HIT_NODE,    NULL,                   NULL,                                 NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__COLLISION,   NULL,                   NULL,                                 NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__ABYSS,       NULL,                   NULL,                                 NULL);

	fsm_init( &s_podMessageFSM);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE,       NULL, NULL,                  NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__GET_STATUS, NULL, cbSyncRequestStatus,   NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__RESET,      NULL, cbSyncRequestReset,    NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__SET_LED,    NULL, cbSyncRequestSetLED,   NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__SET_SPEED,  NULL, cbSyncRequestSetSpeed, NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__MOVE,       NULL, cbSyncRequestMove,     NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__TURN,       NULL, cbSyncRequestTurn,     NULL);

	com_setDefault( cbHandleDefault);
	com_register( cbHandleRequestStatus);
	com_register( cbHandleRequestReset);
	com_register( cbHandleRequestSetLED);
	com_register( cbHandleRequestSetSpeed);
	com_register( cbHandleRequestMove);
	com_register( cbHandleRequestTurn);

	hal_rtc_register( cbHeartbeat, CONQUEST_HEARTBEAT_INTERVAL, true);

	conquest_reset();
}


/*!
 * \brief
 * Resets the conquest logic.
 * 
 * Resets any internal logic state, stops the motors and switches the LEDs off.
 *
 * \warning
 * Calling this function must be synchronized to #cbHeartbeat(). Either deactivate the heartbeat or perform the reset from within
 * an event which is triggered by the heartbeat.
 * 
 * \see
 * conquest_init
 */
void conquest_reset( void) {

	subs_abyss_reset();
	subs_calibration_reset();
	subs_collision_reset();
	subs_initial_reset();
	subs_line_reset();
	subs_movement_reset();
	subs_node_reset();

	fsm_switch( &s_podSubsumptionFSM, CONQUEST_STATE__START);
	fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__NONE);
	conquest_setLastNode( CONQUEST_NODE__INVALID);
	conquest_setRequestedLineSpeed( CONQUEST_INITIAL_SPEED);

	hal_motors_setSpeed( 0, 0);
	hal_led_set( 0);
}


/*!
 * \brief
 * Bluetooth connection management callback.
 * 
 * \param _blConnected
 * - \c true: connected
 * - \c false: disconnected
 * 
 * The conquest logic can be linked to bluetooth connection events #with com_init().
 *
 * When a connection is established, an internal reset message is generated which responds with #CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS.
 * When the connection is closed, a plain reset is triggered (#conquest_reset()).
 * 
 * The handler is synchronized to #cbHeartbeat().
 * 
 * \remarks
 * The priority will escalate to the timer 1 interrupt priority during operation.
 */
void conquest_cbConnection(
	IN const bool _blConnected
	) {

	HAL_INT_ATOMIC_BLOCK( hal_int_getPriority( HAL_INT_SOURCE__TIMER1)) {
		if( _blConnected) {
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSSAGE_STATE__RESET);
		} else {
			conquest_reset();
		}
	}
}


/*!
 * \brief
 * Gets the current subsumption FSM state.
 * 
 * \returns
 * The logic state.
 * 
 * The initial state is #CONQUEST_STATE__START.
 * 
 * \remarks
 * Calling this function should be synchronized to #cbHeartbeat().
 * 
 * \see
 * conquest_setState | s_podSubsumptionFSM
 */
conquest_EState_t conquest_getState( void) {

	return (conquest_EState_t)fsm_getState( &s_podSubsumptionFSM);
}


/*!
 * \brief
 * Sets the current subsumption FSM state.
 * 
 * \param _eState
 * Specifies the new logic state.
 * 
 * \warning
 * Calling this function should be synchronized to #cbHeartbeat().
 * 
 * \see
 * conquest_getState | fsm_switch | s_podSubsumptionFSM
 */
void conquest_setState(
	IN const conquest_EState_t _eState
	) {

	fsm_switch( &s_podSubsumptionFSM, _eState);
}

