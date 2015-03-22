#ifndef com_types_h__
#define com_types_h__

#include "common.h"


/*!
 * \brief
 * Specifies the message frame for transparent bluetooth communication.
 * 
 * \see
 * com_send
 */
typedef struct {
	uint16_t ui16Type; ///< Holds the message type.
	uint8_t aui8Data[30]; ///< Holds additional data.
} com_SMessage_t;


/*!
 * \brief
 * Specifies a handler function for the chain-of-responsibility pattern.
 * 
 * The function must return true if it handled the message and false otherwise.
 */
typedef bool (*com_fnMessageHandler_t)( IN const com_SMessage_t* const);


/*!
 * \brief
 * Specifies an optional callback for bluetooth connection management.
 * 
 * The parameter specifies the new connection state:
 * - \c true: connected
 * - \c false: disconnected
 * 
 * \see
 * com_init | com_processIncoming
 */
typedef void (*com_fnConnectionEvent_t)( IN const bool);

#endif /* com_types_h__ */
