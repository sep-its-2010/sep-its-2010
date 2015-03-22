#ifndef ringbuf_types_h__
#define ringbuf_types_h__

#include "common.h"


/*!
 * \struct ringbuf_SContext_t
 *
 * \brief
 * Provides the ring buffer context.
 * 
 * \remarks
 * Use #ringbuf_init() to create a ring buffer.
 */
typedef struct {
	uint8_t* lpui8Storage; ///< Holds the buffer space.
	uint16_t ui16Size; ///< Holds the buffer size.
	volatile uint16_t ui16ReadIndex; ///< Holds the current read index.
	volatile uint16_t ui16WriteOffset; ///< Holds the current write offset which is based on the read index.
} ringbuf_SContext_t;


#endif /* ringbuf_types_h__ */
