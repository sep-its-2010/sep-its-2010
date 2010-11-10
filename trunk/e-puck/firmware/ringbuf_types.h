#ifndef ringbuf_types_h__
#define ringbuf_types_h__


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
	uint8_t* lpui8Storage;
	uint16_t ui16Size;
	volatile uint16_t ui16ReadIndex;
	volatile uint16_t ui16WriteOffset;
} ringbuf_SContext_t;


#endif /* ringbuf_types_h__ */
