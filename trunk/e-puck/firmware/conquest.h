#ifndef conquest_h__
#define conquest_h__

#include "common.h"

#include "com.h"

#include "conquest_types.h"


/*!
 * \brief
 * Specifies the baud rate of the primary UART.
 * 
 * The primary UART is used for communicating with the bluetooth module.
 * Thus, the baud rate needs to match the baud rate of the bluetooth module.
 * 
 * \see
 * UART1_BAUDRATE_DIVISOR
 */
#define UART1_BAUDRATE 115200


/*!
 * \brief
 * Specifies the baud rate divisor of the primary UART.
 * 
 * The calculation is based on the formula of the dsPIC30F6014A data sheet.
 * It requires that \c FCY and \c UART1_BAUDRATE are defined correctly.
 * 
 * \see
 * main | hal_uart1_configure
 */
#define UART1_BAUDRATE_DIVISOR (uint16_t)( FCY / ( 16 * UART1_BAUDRATE) - 1)


enum {
	UART1_TX_BUFFER_SIZE = 128, ///< Specifies the amount of transmitter buffer space in bytes.
	UART1_RX_BUFFER_SIZE = 128 ///< Specifies the amount of receiver buffer space in bytes.
};


typedef enum {
	DIRECTION_UP = 1,
	DIRECTION_RIGHT = 4,
	DIRECTION_DOWN = 16,
	DIRECTION_LEFT = 64
} EDirection_t;


typedef enum {
	TYPE__INVALID		= 0x0900,
	TYPE__CROSS			= 0x0800 | DIRECTION_UP | DIRECTION_LEFT | DIRECTION_DOWN | DIRECTION_RIGHT,
	TYPE__UP_T			= 0x0400 | DIRECTION_LEFT | DIRECTION_DOWN | DIRECTION_RIGHT,
	TYPE__LEFT_T		= 0x0700 | DIRECTION_UP | DIRECTION_DOWN | DIRECTION_RIGHT,
	TYPE__DOWN_T		= 0x0600 | DIRECTION_UP | DIRECTION_LEFT | DIRECTION_RIGHT,
	TYPE__RIGHT_T		= 0x0500 | DIRECTION_UP | DIRECTION_LEFT | DIRECTION_DOWN,
	TYPE__UP_LEFT		= 0x0300 | DIRECTION_UP | DIRECTION_LEFT,
	TYPE__UP_RIGHT		= 0x0200 | DIRECTION_UP | DIRECTION_RIGHT,
	TYPE__DOWN_LEFT		= 0x0100 | DIRECTION_LEFT | DIRECTION_DOWN,
	TYPE__DOWN_RIGHT	= 0x0000 | DIRECTION_DOWN | DIRECTION_RIGHT
} EType_t;


enum {
	DIM_X = 5,
	DIM_Y = 5,
	TURN_STEPS = 329,
	FOWARD_STEPS = 1000,
	INITIAL_SPEED = 1000,
	INITIAL_POS_X = 0,
	INITIAL_POS_Y = 0
};


bool cbDemoMessageHandler(
	IN const com_SMessage_t* _lppodMessage
	);

void conquest_init( void);

static inline uint16_t conquest_getRequestedLineSpeed( void);

static inline void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	);


uint16_t conquest_getRequestedLineSpeed( void) {

	extern volatile uint16_t conquest_ui16Speed;

	return conquest_ui16Speed;
}


void conquest_setRequestedLineSpeed(
	IN const uint16_t _ui16RequestedSpeed
	) {

	extern volatile uint16_t conquest_ui16Speed;

	conquest_ui16Speed = _ui16RequestedSpeed;
}


#endif /* conquest_h__ */
