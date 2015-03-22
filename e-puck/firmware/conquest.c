#include <string.h>
#include <stdlib.h>
#include <libpic30.h>

#include "hal_rtc.h"
#include "hal_led.h"
#include "hal_int.h"
#include "hal_motors.h"
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

static void cbSyncRequestStatus( void);
static void cbSyncRequestReset( void);
static void cbSyncRequestSetLED( void);
static void cbSyncRequestSetSpeed( void);
static void cbSyncRequestMove( void);
static void cbSyncRequestTurn( void);

static void cbHeartbeat(
	IN const hal_rtc_handle_t UNUSED _hEvent
	);

static void cbSubsumption( void);

static void cbStateMoveExit( void);


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


/*!
 * \brief
 * Holds current sensor data.
 *
 * The data is updated every cycle by #cbHeartbeat().
 *
 * \see
 * conquest_getSensorImage
 */
conquest_SSensorImage_t conquest_podSensorImage;


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
	memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
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
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__GET_STATUS is entered.
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
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__GET_STATUS);

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
 * A reset request is always accepted.
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__RESET is entered.
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
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__RESET);

			memcpy( &s_podMessage, _lppodMessage, sizeof( *_lppodMessage));

			blHandledMessage = true;
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
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__SET_LED is entered.
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
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__SET_LED);

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
 * #CONQUEST_STATE__ABYSS and the message FSM is not busy (#CONQUEST_MESSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__SET_SPEED is entered.
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
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSAGE_STATE__NONE &&
				( eState == CONQUEST_STATE__START || eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__SET_SPEED);

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
 * and the message FSM is not busy (#CONQUEST_MESSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__MOVE is entered.
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
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSAGE_STATE__NONE &&
				conquest_getState() == CONQUEST_STATE__STOP &&
				( conquest_getLastNode() & CONQUEST_DIRECTION__UP)) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__MOVE);

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
 * and the message FSM is not busy (#CONQUEST_MESSAGE_STATE__NONE).
 *
 * The message is then copied and #CONQUEST_MESSAGE_STATE__TURN is entered.
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
			if( fsm_getState( &s_podMessageFSM) == CONQUEST_MESSAGE_STATE__NONE &&
				conquest_getState() == CONQUEST_STATE__STOP) {

				fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__TURN);

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
 * Finally, the message FSM switches to #CONQUEST_MESSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestStatus
 */
void cbSyncRequestStatus( void) {

	const conquest_EState_t eState = conquest_getState();
	if( eState == CONQUEST_STATE__START) {
		conquest_setState( CONQUEST_STATE__INITIAL);
	} else if( eState == CONQUEST_STATE__STOP || eState == CONQUEST_STATE__ABYSS) {
		com_SMessage_t podResponse;
		podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS;
		memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));

		// System up-time in little endian format
		uint32_t ui32UpTime = hal_rtc_getSystemUpTime();
		memcpy( &podResponse.aui8Data[SYS_UP_TIME_STATUS_OFFSET], &ui32UpTime, sizeof( ui32UpTime));

		// Abyss status
		memcpy( &podResponse.aui8Data[ABYSS_STATUS_OFFSET], conquest_podSensorImage.ablAbyssMask, SEN_LINE_NUM_SENSORS);

		// Collision status
		memcpy( &podResponse.aui8Data[COLLISION_STATUS_OFFSET], conquest_podSensorImage.ablCollisionMask, SEN_PROX_NUM_SENSORS);

		podResponse.aui8Data[LAST_NODE_STATUS_OFFSET] = conquest_getLastNode() >> 8;

		com_send( &podResponse);

		fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
	}
}


/*!
 * \brief
 * Synchronized reset request handler.
 *
 * Resets the whole conquest logic and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 *
 * \remarks
 * The message FSM is switched to #CONQUEST_MESSAGE_STATE__NONE by #conquest_reset().
 * 
 * \see
 * cbHandleRequestReset | conquest_reset
 */
void cbSyncRequestReset( void) {

	conquest_reset();

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
	com_send( &podResponse);
}


/*!
 * \brief
 * Synchronized LED change request handler.
 * 
 * Enforces the requested LEDs and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 * Finally, the message FSM switches to #CONQUEST_MESSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestSetLED | hal_led_set
 */
void cbSyncRequestSetLED( void) {

	hal_led_set( s_podMessage.aui8Data[0] | ( s_podMessage.aui8Data[1] << 8));

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
	com_send( &podResponse);

	fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
}


/*!
 * \brief
 * Synchronized speed change request handler.
 * 
 * Sets the new motor line speed and sends an acknowledgment (#CONQUEST_MESSAGE_TYPE__RESPONSE_OK).
 * Finally, the message FSM switches to #CONQUEST_MESSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestSetSpeed | conquest_setRequestedLineSpeed | conquest_getRequestedLineSpeed
 */
void cbSyncRequestSetSpeed( void) {

	conquest_setRequestedLineSpeed( SET_SPEED_MULTIPLIER * s_podMessage.aui8Data[0]);

	com_SMessage_t podResponse;
	podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
	memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
	com_send( &podResponse);

	fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
}


/*!
 * \brief
 * Synchronized move request handler.
 * 
 * Initiates a move when the subsumption state is in #CONQUEST_STATE__STOP by switching to #CONQUEST_STATE__MOVE_FORWARD.
 * When reaching either #CONQUEST_STATE__COLLISION, #CONQUEST_STATE__ABYSS or #CONQUEST_STATE__HIT_NODE one of the following
 * response messages is sent respectively: #CONQUEST_MESSAGE_TYPE__RESPONSE_COLLISION, #CONQUEST_MESSAGE_TYPE__RESPONSE_ABYSS or
 * #CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE.
 * Finally, the message FSM switches to #CONQUEST_MESSAGE_STATE__NONE.
 * 
 * \see
 * cbHandleRequestMove
 */
void cbSyncRequestMove( void) {

	switch( conquest_getState()) {
		case CONQUEST_STATE__STOP: {
			conquest_setState( CONQUEST_STATE__MOVE_FORWARD);
			break;
		}
		case CONQUEST_STATE__COLLISION: {
			com_send( subs_collision_getResponse());
			conquest_setState( CONQUEST_STATE__STOP);
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
			break;
		}
		case CONQUEST_STATE__ABYSS: {
			com_send( subs_abyss_getResponse());
//			conquest_setState( CONQUEST_STATE__STOP);
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
			break;
		}
		case CONQUEST_STATE__HIT_NODE: {
			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_HIT_NODE;
			memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
			podResponse.aui8Data[0] = conquest_getLastNode() >> 8;
			com_send( &podResponse);

			conquest_setState( CONQUEST_STATE__STOP);
			fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
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
 * Finally, the message FSM switches to #CONQUEST_MESSAGE_STATE__NONE.
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
			memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
			com_send( &podResponse);

			fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
		}
	}
}


/*!
 * \brief
 * Cyclic logic update callback.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * The heartbeat updates the sensor image as well as the subsumption and the message FSM.
 *
 * \remarks
 * This function must be called periodically to ensure a proper behavior.
 * 
 * \see
 * hal_rtc_register | conquest_getSensorImage
 */
void cbHeartbeat(
	IN const hal_rtc_handle_t UNUSED _hEvent
	) {

	sen_line_read( &conquest_podSensorImage.podRawLineSensors);
	sen_line_rescale( &conquest_podSensorImage.podRawLineSensors, &conquest_podSensorImage.podCalibratedLineSensors);
	for( uint16_t ui16 = 0; ui16 < SEN_LINE_NUM_SENSORS; ui16++) {
		conquest_podSensorImage.ablAbyssMask[ui16] = conquest_podSensorImage.podCalibratedLineSensors.aui16Reflected[ui16] < CONQUEST_ABYSS_THRESHOLD;
	}

	sen_prox_getCurrent( &conquest_podSensorImage.podRawProximitySensors);
	for( uint16_t ui16 = 0; ui16 < SEN_PROX_NUM_SENSORS; ui16++) {
		conquest_podSensorImage.ablCollisionMask[ui16] = conquest_podSensorImage.podRawProximitySensors.aui8Data[ui16] > CONQUEST_COLLISION_THRESHOLD;
	}

	fsm_update( &s_podSubsumptionFSM);
	fsm_update( &s_podMessageFSM);
}


/*!
 * \brief
 * Wrapper function for #subs_run().
 *
 * Casting the subs_run function pointer to one returning void was quick & dirty but not standard conform.
 * 
 * \see
 * s_podSubsumptionFSM
 */
void cbSubsumption( void) {

	subs_run();
}


/*!
 * \brief
 * Exit action callback for #CONQUEST_STATE__MOVE_FORWARD.
 * 
 * The move state uses subsumption layers which have an internal state.
 * These states need to be reset when the move state is left to prevent behavior corruption.
 * 
 * \see
 * s_podSubsumptionFSM
 */
void cbStateMoveExit( void) {

	subs_node_reset();
	subs_line_reset();
	subs_abyss_reset();
	subs_movement_reset();
	subs_collision_reset();
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
	subs_register( subs_calibration_run, subs_calibration_reset, 0xFF);
	subs_register( subs_initial_run,     subs_initial_reset,     0xF5);
//	subs_register( subs_abyss_run,       subs_abyss_reset,       0xEF);
 	subs_register( subs_collision_run,   subs_collision_reset,   0xDF);
 	subs_register( subs_movement_run,    subs_movement_reset,    0xBF);
 	subs_register( subs_node_run,        subs_node_reset,        0xAF);
 	subs_register( subs_line_run,        subs_line_reset,        0x9F);

	fsm_init( &s_podSubsumptionFSM);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__START,        NULL, cbSubsumption, NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__CALIBRATION,  NULL, cbSubsumption, NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__INITIAL,      NULL, cbSubsumption, subs_initial_reset);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__STOP,         NULL, NULL,          NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__MOVE_FORWARD, NULL, cbSubsumption, cbStateMoveExit);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__TURN_LEFT,    NULL, cbSubsumption, cbStateMoveExit);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__TURN_RIGHT,   NULL, cbSubsumption, cbStateMoveExit);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__CENTER_LINE,  NULL, cbSubsumption, cbStateMoveExit);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__HIT_NODE,     NULL, NULL,          NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__COLLISION,    NULL, NULL,          NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__ABYSS,        NULL, NULL,          NULL);
	fsm_configureState( &s_podSubsumptionFSM, CONQUEST_STATE__RETURN_NODE,  NULL, cbSubsumption, cbStateMoveExit);

	fsm_init( &s_podMessageFSM);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE,       NULL, NULL,                  NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__GET_STATUS, NULL, cbSyncRequestStatus,   NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__RESET,      NULL, cbSyncRequestReset,    NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__SET_LED,    NULL, cbSyncRequestSetLED,   NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__SET_SPEED,  NULL, cbSyncRequestSetSpeed, NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__MOVE,       NULL, cbSyncRequestMove,     NULL);
	fsm_configureState( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__TURN,       NULL, cbSyncRequestTurn,     NULL);

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

	subs_reset();

	fsm_switch( &s_podSubsumptionFSM, CONQUEST_STATE__START);
	fsm_switch( &s_podMessageFSM, CONQUEST_MESSAGE_STATE__NONE);
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
 * Any connection event triggers a reset.
 * When a connection is established, a response (#CONQUEST_MESSAGE_TYPE__RESPONSE_STATUS) is sent after a short delay.
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
		conquest_reset();

		if( _blConnected) {
			__delay32( FCY / 4);

			com_SMessage_t podResponse;
			podResponse.ui16Type = CONQUEST_MESSAGE_TYPE__RESPONSE_OK;
			memset( podResponse.aui8Data, 0xFF, sizeof( podResponse.aui8Data));
			com_send( &podResponse);
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

