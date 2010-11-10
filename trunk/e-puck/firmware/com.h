#ifndef com_h__
#define com_h__

#include "common.h"

#include "com_types.h"

enum {
	COM_TX_BUFFER_SIZE = 128,
	COM_RX_BUFFER_SIZE = 128
};

void com_init( void);

bool com_processIncoming(
	IN const com_fnMessageHandler_t _fnHandler
	);

void com_send(
	IN const com_SMessage_t* const _lppodMessage
	);


#endif /* com_h__ */
