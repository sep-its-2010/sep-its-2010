#include <string.h>

#include "hal_adc.h"
#include "hal_rtc.h"

#include "sen_prox.h"


/*!
 * \brief
 * Specifies the different IR proximity sensor measurement phases.
 * 
 * \see
 * cbMeasurement | s_ePhase
 */
typedef enum {
	PHASE__AMBIENT, ///< Ambient measurement of all 8 IR sensors.
	PHASE__IR15, ///< Measurement of IR sensor 1 and 5.
	PHASE__IR26, ///< Measurement of IR sensor 2 and 6.
	PHASE__IR37, ///< Measurement of IR sensor 3 and 7.
	PHASE__IR48 ///< Measurement of IR sensor 4 and 8.
} EPhase_t;


static void cbMeasurement(
	IN const hal_rtc_handle_t _hEvent
	);


/*!
 * \brief
 * Holds the handle to the proximity sensor event.
 *
 * \remarks
 * The event is registered upon initialization (#sen_prox_init()) and will never be unregistered.
 * 
 * \see
 * sen_prox_getCurrent | cbMeasurement
 */
static hal_rtc_handle_t s_hMeasurementEvent = HAL_RTC_INVALID_HANDLE;


/*!
 * \brief
 * Holds the current phase of the measurement process.
 *
 * \remarks
 * The phase is reset by #sen_prox_enable().
 * 
 * \see
 * cbMeasurement | sen_prox_init
 */
static EPhase_t s_ePhase;


/*!
 * \brief
 * Holds the plain IR proximity sensor reflection values.
 * 
 * The data is updated by #cbMeasurement().
 */
static sen_prox_SData_t s_podCurrent;


/*!
 * \brief
 * Performs the IR proximity sensor measurement.
 * 
 * \param _hEvent
 * Specifies the unique event handle.
 * 
 * This event performs a cycle based measurement of the IR proximity sensors.
 * There are 5 cycles which all save their collected values:
 * - #PHASE__AMBIENT measures the ambient light of all 8 IR sensors.
 * - #PHASE__IR15 measures the IR sensors 1 and 5 with their associated pulse lights switched on.
 * - #PHASE__IR26 measures the IR sensors 2 and 6 with their associated pulse lights switched on.
 * - #PHASE__IR37 measures the IR sensors 3 and 7 with their associated pulse lights switched on.
 * - #PHASE__IR48 measures the IR sensors 4 and 8 with their associated pulse lights switched on.
 *
 * The least significant 4 bits of the 12 bit ADC values are stripped.
 * 
 * \remarks
 * #sen_prox_enable() needs to be called to enable the event ans start sampling.
 * 
 * \see
 * s_hMeasurementEvent | #sen_prox_disable()
 */
void cbMeasurement(
	IN const hal_rtc_handle_t _hEvent
	) {

	static sen_prox_SData_t s_podAmbient;

	switch( s_ePhase) {
		case PHASE__AMBIENT: {
			for( uint16_t ui16 = 0; ui16 < SEN_PROX_NUM_SENSORS; ui16++) {
				s_podAmbient.aui8Data[ui16] = hal_adc_read( HAL_ADC_CHANNEL__IR_1 + ui16) >> 4;
			}
			SEN_PROX_PIN__PULSE_IR15 = true;
			s_ePhase = PHASE__IR15;
			break;
		}
		case PHASE__IR15: {
			const uint8_t ui8IR1 = hal_adc_read( HAL_ADC_CHANNEL__IR_1) >> 4;
			const uint8_t ui8IR5 = hal_adc_read( HAL_ADC_CHANNEL__IR_5) >> 4;
			s_podCurrent.aui8Data[0] = ui8IR1 < s_podAmbient.aui8Data[0] ? s_podAmbient.aui8Data[0] - ui8IR1 : 0;
			s_podCurrent.aui8Data[4] = ui8IR5 < s_podAmbient.aui8Data[4] ? s_podAmbient.aui8Data[4] - ui8IR5 : 0;
			SEN_PROX_PIN__PULSE_IR15 = false;
			SEN_PROX_PIN__PULSE_IR26 = true;
			s_ePhase = PHASE__IR26;
			break;
		}
		case PHASE__IR26: {
			const uint8_t ui8IR2 = hal_adc_read( HAL_ADC_CHANNEL__IR_2) >> 4;
			const uint8_t ui8IR6 = hal_adc_read( HAL_ADC_CHANNEL__IR_6) >> 4;
			s_podCurrent.aui8Data[1] = ui8IR2 < s_podAmbient.aui8Data[1] ? s_podAmbient.aui8Data[1] - ui8IR2 : 0;
			s_podCurrent.aui8Data[5] = ui8IR6 < s_podAmbient.aui8Data[5] ? s_podAmbient.aui8Data[5] - ui8IR6 : 0;
			SEN_PROX_PIN__PULSE_IR26 = false;
			SEN_PROX_PIN__PULSE_IR37 = true;
			s_ePhase = PHASE__IR37;
			break;
		}
		case PHASE__IR37: {
			const uint8_t ui8IR3 = hal_adc_read( HAL_ADC_CHANNEL__IR_3) >> 4;
			const uint8_t ui8IR7 = hal_adc_read( HAL_ADC_CHANNEL__IR_7) >> 4;
			s_podCurrent.aui8Data[2] = ui8IR3 < s_podAmbient.aui8Data[2] ? s_podAmbient.aui8Data[2] - ui8IR3 : 0;
			s_podCurrent.aui8Data[6] = ui8IR7 < s_podAmbient.aui8Data[6] ? s_podAmbient.aui8Data[6] - ui8IR7 : 0;
			SEN_PROX_PIN__PULSE_IR37 = false;
			SEN_PROX_PIN__PULSE_IR48 = true;
			s_ePhase = PHASE__IR48;
			break;
		}
		case PHASE__IR48: {
			const uint8_t ui8IR4 = hal_adc_read( HAL_ADC_CHANNEL__IR_4) >> 4;
			const uint8_t ui8IR8 = hal_adc_read( HAL_ADC_CHANNEL__IR_8) >> 4;
			s_podCurrent.aui8Data[3] = ui8IR4 < s_podAmbient.aui8Data[3] ? s_podAmbient.aui8Data[3] - ui8IR4 : 0;
			s_podCurrent.aui8Data[7] = ui8IR8 < s_podAmbient.aui8Data[7] ? s_podAmbient.aui8Data[7] - ui8IR8 : 0;
			SEN_PROX_PIN__PULSE_IR48 = false;
			s_ePhase = PHASE__AMBIENT;
			break;
		}
		default: {
			SEN_PROX_PIN__PULSE_IR15 = false;
			SEN_PROX_PIN__PULSE_IR26 = false;
			SEN_PROX_PIN__PULSE_IR37 = false;
			SEN_PROX_PIN__PULSE_IR48 = false;
			s_ePhase = PHASE__AMBIENT;
			hal_rtc_deactivate( _hEvent);
		}
	}
}


/*!
 * \brief
 * Initializes the IR proximity sensors module.
 *
 * \param _ui16CycleInterval
 * Specifies the measurement cycle interval length in real time clock event ticks.
 *
 * \returns
 * - \c true: on success
 * - \c false: the measurement event could not be registered (e.g. invalid interval length or no free slot).
 *
 * Registers the measurement event, initializes the IR pulse light pins and clears the current measurement with all '1's.
 *
 * The measurement event needs to be enabled (#sen_prox_enable()) before one can obtain valid values with #sen_prox_getCurrent().
 * 
 * \remarks
 * - The real time clock needs to be initialized (#hal_rtc_init()).
 * - This function needs to be called before any other function from this module.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * cbMeasurement
 */
bool sen_prox_init(
	IN const uint16_t _ui16CycleInterval
	) {

	SEN_PROX_PIN_DIR__PULSE_IR15 = false;
	SEN_PROX_PIN_DIR__PULSE_IR26 = false;
	SEN_PROX_PIN_DIR__PULSE_IR37 = false;
	SEN_PROX_PIN_DIR__PULSE_IR48 = false;

	SEN_PROX_PIN__PULSE_IR15 = false;
	SEN_PROX_PIN__PULSE_IR26 = false;
	SEN_PROX_PIN__PULSE_IR37 = false;
	SEN_PROX_PIN__PULSE_IR48 = false;

	memset( &s_podCurrent, 0xFF, sizeof( s_podCurrent));

	s_ePhase = PHASE__AMBIENT;
	if( s_hMeasurementEvent == HAL_RTC_INVALID_HANDLE) {
		s_hMeasurementEvent = hal_rtc_register( cbMeasurement, _ui16CycleInterval, true);
	} else {
		hal_rtc_deactivate( s_hMeasurementEvent);
		hal_rtc_reset( s_hMeasurementEvent);
	}

	return s_hMeasurementEvent != HAL_RTC_INVALID_HANDLE;
}


/*!
 * \brief
 * Enables the measurement event.
 * 
 * \returns
 * - \c true: on success
 * - \c false: the measurement event could not be activated (e.g. module not initialized).
 * 
 * The state of the measurement event is reset. The event needs to be enabled to obtain valid data with #sen_prox_getCurrent().
 * 
 * \remarks
 * The module needs to be initialized (#sen_prox_init()).
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * sen_prox_disable
 */
bool sen_prox_enable( void) {

	s_ePhase = PHASE__AMBIENT;
	hal_rtc_reset( s_hMeasurementEvent);
	return hal_rtc_activate( s_hMeasurementEvent);
}


/*!
 * \brief
 * Disables the measurement event.
 * 
 * \remarks
 * Disabling a disabled or invalid event has no effect.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 * 
 * \see
 * sen_prox_enable
 */
void sen_prox_disable( void) {

	hal_rtc_deactivate( s_hMeasurementEvent);
}


/*!
 * \brief
 * Copies the current proximity sensor values.
 * 
 * \param _lppodData
 * Specifies the destination buffer.
 * 
 * The values are read from an internal buffer which is continuously updated by #cbMeasurement().
 *
 * The values represent the plain reflection, that is, the reflected IR light without ambient light influence.
 * 
 * \remarks
 * The module needs to be initialized (#sen_prox_init()) and
 * the measurement event needs to be enabled (#sen_prox_enable()) to get valid values.
 *
 * \warning
 * This function may not be preempted by any function which accesses this module.
 */
void sen_prox_getCurrent(
	OUT sen_prox_SData_t* const _lppodData
	) {

	memcpy( _lppodData, &s_podCurrent, sizeof( *_lppodData));
}
