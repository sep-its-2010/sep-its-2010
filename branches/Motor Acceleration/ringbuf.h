#ifndef ringbuf_h__
#define ringbuf_h__

#include "common.h"

#include "ringbuf_types.h"

static inline void ringbuf_init(
	OUT ringbuf_SContext_t* const _lppodContext,
	INOUT void* const _lpvStorage,
	IN const uint16_t _ui16Size
	);

static inline void ringbuf_clear(
	OUT ringbuf_SContext_t* const _lppodContext
	);

static inline uint16_t ringbuf_getSize(
	IN const ringbuf_SContext_t* const _lppodContext
	);

static inline uint16_t ringbuf_getUsage(
	IN const ringbuf_SContext_t* const _lppodContext
	);

static inline uint16_t ringbuf_getFree(
	IN const ringbuf_SContext_t* const _lppodContext
	);

static inline bool ringbuf_isEmpty(
	IN const ringbuf_SContext_t* const _lppodContext
	);

static inline bool ringbuf_isFull(
	IN const ringbuf_SContext_t* const _lppodContext
	);

static inline bool ringbuf_isLinear(
	IN const ringbuf_SContext_t* const _lppodContext
	);

uint8_t ringbuf_get(
	IN const ringbuf_SContext_t* const _lppodContext
	);

uint8_t ringbuf_getAt(
	IN const ringbuf_SContext_t* const _lppodContext,
	IN const uint16_t _ui16Position
	);

uint16_t ringbuf_getRange(
	IN const ringbuf_SContext_t* const _lppodContext,
	OUT void* const _lpvData,
	IN const uint16_t _ui16StartIndex,
	IN const uint16_t _ui16Length
	);

uint8_t ringbuf_pop(
	INOUT ringbuf_SContext_t* const _lppodContext
	);

uint16_t ringbuf_popRange(
	INOUT ringbuf_SContext_t* const _lppodContext,
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	);

void ringbuf_push(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const uint8_t _ui8Value
	);

uint16_t ringbuf_pushRange(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const void* const _lpvData,
	IN const uint16_t _ui16Elements
	);

void ringbuf_drop(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const uint16_t _ui16Elements
	);


/*!
 * \brief
 * Initializes a ring buffer context.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \param _lpvStorage
 * Specifies the storage space to be used by the ring buffer.
 * 
 * \param _ui16Size
 * Specifies the size of the ring buffer storage space in bytes.
 * 
 * The ring buffer context pointed to by _lppodContext is initialized as an empty buffer.
 * 
 * \remarks
 * A ring buffer must be initialized before it can be used.
 *
 * \warning
 * This function is not interrupt safe.
 * 
 * \see
 * ringbuf_clear | ringbuf_get | ringbuf_pop | ringbuf_push | ringbuf_getSize
 */
void ringbuf_init(
	OUT ringbuf_SContext_t* const _lppodContext,
	INOUT void* const _lpvStorage,
	IN const uint16_t _ui16Size
	) {

	_lppodContext->lpui8Storage = (uint8_t*)_lpvStorage;
	_lppodContext->ui16Size = _ui16Size;
	_lppodContext->ui16ReadIndex = 0;
	_lppodContext->ui16WriteOffset = 0;
}


/*!
 * \brief
 * Clears the ring buffer.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * Resets the read index and the write offset. The storage and the buffer size are not altered at all.
 * 
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_drop
 */
void ringbuf_clear(
	OUT ringbuf_SContext_t* const _lppodContext
	) {

	_lppodContext->ui16ReadIndex = 0;
	_lppodContext->ui16WriteOffset = 0;
}

/*!
 * \brief
 * Gets the ring buffer size.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * The amount of the overall buffer space.
 * 
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_getUsage | ringbuf_getFree | ringbuf_isEmpty | ringbuf_isFull
 */
uint16_t ringbuf_getSize(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16Size;
}


/*!
 * \brief
 * Gets the ring buffer usage.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * The amount of used buffer space.
 * 
 * \remarks
 * This function is interrupt safe.
 *
 * \warning
 * The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_getSize | ringbuf_getFree | ringbuf_isEmpty | ringbuf_isFull
 */
uint16_t ringbuf_getUsage(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16WriteOffset;
}


/*!
 * \brief
 * Gets the free ring buffer space.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * The amount of free buffer space.
 * 
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_getSize | ringbuf_getUsage | ringbuf_isEmpty | ringbuf_isFull
 */
uint16_t ringbuf_getFree(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16Size - _lppodContext->ui16WriteOffset;
}


/*!
 * \brief
 * Checks whether the ring buffer is empty.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * Whether or not the ring buffer is empty.
 * 
 * \remarks
 * This function is interrupt safe.
 *
 * \warning
 * The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_getSize | ringbuf_getUsage | ringbuf_getFree | ringbuf_isFull
 */
bool ringbuf_isEmpty(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return !_lppodContext->ui16WriteOffset;
}


/*!
 * \brief
 * Checks whether the ring buffer is full.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * Whether or not the ring buffer is full.
 * 
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_getSize | ringbuf_getUsage | ringbuf_getFree | ringbuf_isEmpty
 */
bool ringbuf_isFull(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16WriteOffset >= _lppodContext->ui16Size;
}


/*!
 * \brief
 * Checks whether the ring buffer content is without wrap-around.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * If the buffer is linear all bytes can be addressed directly with a read index because no wrap-around must be taken into
 * account. This is especially useful when using memcpy of memcmp on the buffer.
 * 
 * \returns
 * Whether or not FIFO bytes have ascending addresses.
 * 
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init
 */
bool ringbuf_isLinear(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	return _lppodContext->ui16ReadIndex + _lppodContext->ui16WriteOffset <= _lppodContext->ui16Size;
}


#endif /* ringbuf_h__ */
