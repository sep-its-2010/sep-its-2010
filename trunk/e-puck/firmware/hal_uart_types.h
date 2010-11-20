#ifndef hal_uart_types_h__
#define hal_uart_types_h__

#include "common.h"


/*!
 * \brief
 * Provides the UART data configuration options.
 * 
 * The word length is currently restricted to 8 data bits, an optional parity bit and one or two stop bits.
 * 
 * \see
 * hal_uart1_configure
 */
typedef enum {
	HAL_UART_CONFIG__8N1, ///< Data format: 8 data bits, no parity, 1 stop bit
	HAL_UART_CONFIG__8N2, ///< Data format: 8 data bits, no parity, 2 stop bits
	HAL_UART_CONFIG__8E1, ///< Data format: 8 data bits, even parity, 1 stop bit
	HAL_UART_CONFIG__8E2, ///< Data format: 8 data bits, even parity, 2 stop bits
	HAL_UART_CONFIG__8O1, ///< Data format: 8 data bits, odd parity, 1 stop bit
	HAL_UART_CONFIG__8O2 ///< Data format: 8 data bits, odd parity, 2 stop bits
} hal_uart_EConfig_t;


#endif /* hal_uart_types_h__ */
