#include "hal_uart1.h"
#include "hal_int.h"
#include "ringbuf.h"

#include "com.h"

static uint8_t s_aui8RxBufferSpace[COM_RX_BUFFER_SIZE];
static uint8_t s_aui8TxBufferSpace[COM_TX_BUFFER_SIZE];

void com_init( void ) {

	ringbuf_init( hal_uart1_getRxRingBuffer(), s_aui8RxBufferSpace, sizeof( s_aui8RxBufferSpace));
	ringbuf_init( hal_uart1_getTxRingBuffer(), s_aui8TxBufferSpace, sizeof( s_aui8TxBufferSpace));
}

bool com_processIncoming(
	IN const com_fnMessageHandler_t _fnHandler
	) {

	hal_uart1_forceRxMove();
	com_SMessage_t podMessage;
	const bool blHasMessage = ringbuf_getUsage( hal_uart1_getRxRingBuffer()) >= sizeof( podMessage);

	if( blHasMessage) {
		hal_uart1_read( &podMessage, sizeof( podMessage));
		if( _fnHandler) {
			_fnHandler( &podMessage);
		}
	}

	return blHasMessage;
}

void com_send(
	IN const com_SMessage_t* const _lppodMessage
	) {

	hal_uart1_write( _lppodMessage, sizeof( *_lppodMessage));
}
