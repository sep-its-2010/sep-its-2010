#include <string.h>

#include "subs.h"
#include "com.h"
#include "hal_rtc.h"
#include "sen_line.h"
#include "subs_abyss.h"
#include "subs_node.h"
#include "sen_prox.h"
#include "subs_collision.h"
#include "hal_led.h"

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

static bool cbHandleRequestStatus( IN const com_SMessage_t* const _lppodMessage);
static bool cbHandleRequestReset( IN const com_SMessage_t* const _lppodMessage);
static bool cbHandleRequestSetLED( IN const com_SMessage_t* const _lppodMessage);


/*!
 * \brief
 * Initializes the subsumption architecture.
 * 
 * The priority list of the behaviors gets cleared.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * subs_run | subs_reset | subs_register
 */
void subs_init( void) {

	memset( s_apodBehaviors, 0, sizeof( s_apodBehaviors) / sizeof( *s_apodBehaviors));
	s_lppodFirstBehavior = NULL;
	com_register( cbHandleRequestStatus);
	com_register( cbHandleRequestReset);
	com_register( cbHandleRequestSetLED);
}


/*!
 * \brief
 * Calls the action callbacks of all behaviors.
 * 
 * The action callbacks are called in decreasing priority until a callback returns true.
 * 
 * \remarks
 * An action callback may call #subs_register() and #subs_unregister(). Refer to this functions for detailed information and
 * restrictions.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
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
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * subs_init | subs_register
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
	com_register( cbHandleRequestStatus);
	com_register( cbHandleRequestReset);
	com_register( cbHandleRequestSetLED);
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
 * - \c true on success.
 * - \c false on error (invalid action callback or no free slot).
 * 
 * Registers a new behavior which is uniquely identified by its action callback. Optionally, a reset callback can be
 * specified. The behavior is inserted into the list right behind the last entry with a higher or equal priority.
 *
 * An action callback may register other action callbacks. However, the new action might or might not trigger
 * in the current run sequence depending on the insertion point and the return value of the current action.
 *
 * \remarks
 * An action callback must return \c true if it triggered and it must return \c false otherwise.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
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

		// List empty?
		if( !s_lppodFirstBehavior) {
			s_lppodFirstBehavior = s_apodBehaviors;
			s_lppodFirstBehavior->fnReset = _fnResetCallback;
			s_lppodFirstBehavior->fnRun = _fnRunCallback;
			s_lppodFirstBehavior->lppodNext = NULL;
			s_lppodFirstBehavior->ui16Priotity = _ui16Priority;

			blSuccess = true;
		} else {

			// Find free space
			subs_SBehaviorPriorityList_t* lppodNextFree = NULL;
			bool blDuplicate = false;
			for( uint16_t ui16 = 0; !blDuplicate && ui16 < SUBS_MAX_BEHAVIORS; ui16++) {
				if( s_apodBehaviors[ui16].fnRun == _fnRunCallback) {
					blDuplicate = true;
				} else if( !lppodNextFree && !s_apodBehaviors[ui16].fnRun) {
					lppodNextFree = &s_apodBehaviors[ui16];
				}
			}

			// Found free space?
			if( !blDuplicate && lppodNextFree) {
				lppodNextFree->fnReset = _fnResetCallback;
				lppodNextFree->fnRun = _fnRunCallback;
				lppodNextFree->ui16Priotity = _ui16Priority;

				// Find insertion point
				subs_SBehaviorPriorityList_t* lppodPrev = NULL;
				subs_SBehaviorPriorityList_t* lppodCurrent = s_lppodFirstBehavior;
				while( lppodCurrent && lppodCurrent->ui16Priotity >= _ui16Priority) {
					lppodPrev = lppodCurrent;
					lppodCurrent = lppodCurrent->lppodNext;
				}

				// Insert at list head?
				if( !lppodPrev) {
					lppodNextFree->lppodNext = lppodCurrent;
					s_lppodFirstBehavior = lppodNextFree;
				} else {
					lppodNextFree->lppodNext = lppodCurrent;
					lppodPrev->lppodNext = lppodNextFree;
				}

				blSuccess = true;
			}
		}
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
 *
 * An action may unregister itself or other actions provided that the performing action returns \c true afterwards.
 * 
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * subs_init | subs_register
 */
void subs_unregister(
	IN const subs_fnRun_t _fnRunCallback
	) {

	if( _fnRunCallback && s_lppodFirstBehavior) {
		subs_SBehaviorPriorityList_t* lppodCurrent = s_lppodFirstBehavior;
		subs_SBehaviorPriorityList_t* lppodPrev = NULL;
		while( lppodCurrent && lppodCurrent->fnRun != _fnRunCallback) {
			lppodPrev = lppodCurrent;
			lppodCurrent = lppodCurrent->lppodNext;
		}

		// Found it?
		if( lppodCurrent) {

			// List head?
			if( !lppodPrev) {
				s_lppodFirstBehavior = NULL;
			} else {
				lppodPrev->lppodNext = lppodCurrent->lppodNext;
			}
			memset( lppodCurrent, 0, sizeof( *lppodCurrent));
		}
	}
}

/*!
 * \brief
 * Handles status-requests.
 * 
 * \param _podMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible a message containing all current status-data will be created and sent to the smartphone.
 * 
 * \remarks
 * Handler-functions have to be registered.
 */
bool cbHandleRequestStatus(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_STATUS) {
		com_SMessage_t podStatusResponse = {COM_MESSAGE_TYPE__RESPONSE_STATUS, {0}};

		// record system up-time in little endian format
		uint32_t ui32SystemUpTime = hal_rtc_getSystemUpTime();
 		podStatusResponse.aui8Data[0] = (ui32SystemUpTime & 0x000000FF);
		podStatusResponse.aui8Data[1] = (ui32SystemUpTime & 0x0000FF00) >> 8;
 		podStatusResponse.aui8Data[2] = (ui32SystemUpTime & 0x00FF0000) >> 16;
 		podStatusResponse.aui8Data[3] = (ui32SystemUpTime & 0xFF000000) >> 24;

		// record abyss status
		sen_line_SData_t podSensorData= {{0}};
		sen_line_read( &podSensorData);
		sen_line_rescale( &podSensorData, &podSensorData);

		if( podSensorData.aui16Data[SEN_LINE_SENSOR__LEFT] < SUBS_ABYSS_THRESHOLD) {
			podStatusResponse.aui8Data[3 + SEN_LINE_SENSOR__LEFT] = true;
		}
		if( podSensorData.aui16Data[SEN_LINE_SENSOR__MIDDLE] < SUBS_ABYSS_THRESHOLD) {
			podStatusResponse.aui8Data[3 + SEN_LINE_SENSOR__MIDDLE] = true;
		}
		if( podSensorData.aui16Data[SEN_LINE_SENSOR__RIGHT] < SUBS_ABYSS_THRESHOLD) {
			podStatusResponse.aui8Data[3 + SEN_LINE_SENSOR__RIGHT] = true;
		}

		// record collision status
		sen_prox_SData_t podProxSensorData = {{0}};
		sen_prox_getCurrent( &podProxSensorData);

		for( uint8_t i = 0; i < sizeof(podProxSensorData.aui8Data); i++) {
			if( podProxSensorData.aui8Data[i] > SUBS_COLLISION__PROX_THRESHOLD) {
				podStatusResponse.aui8Data[7 + i] = true;
			}
		}

		// record last visited node-type
		podStatusResponse.aui8Data[15] = subs_node_getCurrentNodeType();

		blHandledMessage = true;
	}

	return blHandledMessage;
}

/*!
 * \brief
 * Handles reset-requests.
 * 
 * \param _podMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the robot will be reset to its default state.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 */
bool cbHandleRequestReset(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_STATUS) {
		subs_reset();
		blHandledMessage = true;
	}

	return blHandledMessage;
}

/*!
 * \brief
 * Handles SetLED-requests.
 * 
 * \param _podMessage
 * Specifies the message which has to be analyzed.
 * 
 * \returns
 * True, if a message has been handled by this function, false otherwise.
 *
 * Computes the type of the message by analyzing the first and second Byte of the message.
 * If this handler is responsible the LEDs which are specified in the first ten bytes of the message-data will be switched on, all other LEDs will be switched off.
 * 
 * \remarks
 * Handler-functions have to be registered during the reset function.
 */
bool cbHandleRequestSetLED(
	IN const com_SMessage_t* const _lppodMessage
	) {

	bool blHandledMessage = false;

	if( _lppodMessage->eType == COM_MESSAGE_TYPE__REQUEST_SET_LED) {
		uint16_t ui16LEDsToSet = 0;

		for( uint8_t ui8 = 0; ui8 < 10; ui8++) {
			if( _lppodMessage->aui8Data[ui8] == true) {
				ui16LEDsToSet++;
				ui16LEDsToSet = ui16LEDsToSet << 1;
			}
		}
		hal_led_set(ui16LEDsToSet);
		blHandledMessage = true;
	}

	return blHandledMessage;
}
