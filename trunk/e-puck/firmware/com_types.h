#ifndef com_types_h__
#define com_types_h__

#include "common.h"

typedef enum {
	COM_MESSAGE_TYPE__RESET
} com_EMessageType_t;

typedef struct {
	com_EMessageType_t eType;
	uint8_t aui8Data[30];
} com_SMessage_t;

typedef void (*com_fnMessageHandler_t)( const com_SMessage_t* const);

#endif /* com_types_h__ */
