#ifndef com_h__
#define com_h__

#include "common.h"

#include "com_types.h"

enum {
	COM_TX_BUFFER_SIZE = 128, ///< Specifies the amount of transmitter buffer space in bytes.
	COM_RX_BUFFER_SIZE = 128, ///< Specifies the amount of receiver buffer space in bytes.
	COM_MAX_HANDLERS = 16
};

void com_init( void);

bool com_processIncoming( void);

void com_send(
	IN const com_SMessage_t* const _lppodMessage
	);

bool com_register(
	IN const com_fnMessageHandler_t _fnHandler
	);

void com_unregister(
	IN const com_fnMessageHandler_t _fnHandler
	);

#endif /* com_h__ */
