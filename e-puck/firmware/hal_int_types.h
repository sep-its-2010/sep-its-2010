#ifndef hal_int_types_h__
#define hal_int_types_h__

#include "common.h"


/*!
 * \brief
 * Specifies interrupt and CPU priority levels.
 * 
 * \see
 * hal_int_setPriority
 */
typedef enum {
	HAL_INT_PRIORITY__0 = 0, ///< Interrupt priority 0 (lowest priority); interrupts will never trigger at this level.
	HAL_INT_PRIORITY__1, ///< Interrupt priority 1
	HAL_INT_PRIORITY__2, ///< Interrupt priority 2
	HAL_INT_PRIORITY__3, ///< Interrupt priority 3
	HAL_INT_PRIORITY__4, ///< Interrupt priority 4
	HAL_INT_PRIORITY__5, ///< Interrupt priority 5
	HAL_INT_PRIORITY__6, ///< Interrupt priority 6
	HAL_INT_PRIORITY__7 ///< Interrupt priority 7 (highest priority); interrupts cannot be suppressed at this level.
} hal_int_EPriority_t;


/*!
 * \brief
 * Specifies the available interrupt sources.
 * 
 * \see
 * hal_int_enable | hal_int_disable | hal_int_setPriority | hal_int_clearFlag | hal_int_isFlagSet
 */
typedef enum {
	HAL_INT_SOURCE__DUMMY, ///< Dummy placeholder which is required by #HAL_INT_ATOMIC_BLOCK.

	HAL_INT_SOURCE__ADC_COMPLETE, ///< Analog-digital-conversion complete.
	HAL_INT_SOURCE__BROWNOUT, ///< Under voltage detected.
	HAL_INT_SOURCE__CAN1, ///< Primary CAN bus event.
	HAL_INT_SOURCE__CAN2, ///< Secondary CAN bus event.
	HAL_INT_SOURCE__CPU, ///< General CPU processing (required only for priority levels).
	HAL_INT_SOURCE__DATA_CONVERTER, ///< Data converter (sound processing) event.
	HAL_INT_SOURCE__EXTERNAL0, ///< External event 0.
	HAL_INT_SOURCE__EXTERNAL1, ///< External event 1.
	HAL_INT_SOURCE__EXTERNAL2, ///< External event 2.
	HAL_INT_SOURCE__EXTERNAL3, ///< External event 3.
	HAL_INT_SOURCE__EXTERNAL4, ///< External event 4.
	HAL_INT_SOURCE__I2C_MASTER,  ///< I2C bus collision; at least 2 masters active.
	HAL_INT_SOURCE__I2C_SLAVE, ///< I2C transfer complete.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1, ///< Input capture event on channel 1.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2, ///< Input capture event on channel 2.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3, ///< Input capture event on channel 3.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4, ///< Input capture event on channel 4.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5, ///< Input capture event on channel 5.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6, ///< Input capture event on channel 6.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7, ///< Input capture event on channel 7.
	HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8, ///< Input capture event on channel 8.
	HAL_INT_SOURCE__INPUT_CHANGED, ///< Pin input changed event.
	HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE, ///< Non-volatile memory write completion event.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1, ///< Output compare event on channel 1.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2, ///< Output compare event on channel 2.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3, ///< Output compare event on channel 3.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4, ///< Output compare event on channel 4.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5, ///< Output compare event on channel 5.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6, ///< Output compare event on channel 6.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7, ///< Output compare event on channel 7.
	HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8, ///< Output compare event on channel 8.
	HAL_INT_SOURCE__SPI1, ///< Primary serial peripheral interface event.
	HAL_INT_SOURCE__SPI2, ///< Secondary serial peripheral interface event.
	HAL_INT_SOURCE__TIMER1, ///< Timer 1 event.
	HAL_INT_SOURCE__TIMER2, ///< Timer 2 event.
	HAL_INT_SOURCE__TIMER3, ///< Timer 3 event.
	HAL_INT_SOURCE__TIMER4, ///< Timer 4 event.
	HAL_INT_SOURCE__TIMER5, ///< Timer 5 event.
	HAL_INT_SOURCE__UART1_RECEIVER, ///< Primary UART receiver event.
	HAL_INT_SOURCE__UART1_TRANSMITTER, ///< Primary UART transmitter event.
	HAL_INT_SOURCE__UART2_RECEIVER, ///< Secondary UART receiver event.
	HAL_INT_SOURCE__UART2_TRANSMITTER ///< Secondary UART transmitter event.
} hal_int_ESource_t;


#endif /* hal_int_types_h__ */
