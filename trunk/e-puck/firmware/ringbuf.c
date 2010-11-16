#include "ringbuf.h"


/*!
 * \brief
 * Gets the oldest ring buffer element.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * The oldest element or 0xFF if there is none.
 * 
 * This function contains an under run prevention. In case the buffer is empty, 0xFF is returned.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isEmpty | ringbuf_pop | ringbuf_popRange | 
 * ringbuf_push | ringbuf_pushRange | ringbuf_getAt | ringbuf_getRange
 */
uint8_t ringbuf_get(
	IN const ringbuf_SContext_t* const _lppodContext
	) {

	uint8_t ui8Return = 0xFF;

	if( _lppodContext->ui16WriteOffset) {
		ui8Return = _lppodContext->lpui8Storage[_lppodContext->ui16ReadIndex];
	}

	return ui8Return;
}


/*!
 * \brief
 * Gets the specified ring buffer element.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * \param _ui16Position
 * Specifies the position of the element relative to the oldest element.
 * 
 * \returns
 * The specified element or 0xFF if there is none.
 * 
 * This function contains an under run prevention. In case the element index is out of bound, 0xFF is returned.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isEmpty | ringbuf_pop | ringbuf_popRange | 
 * ringbuf_push | ringbuf_pushRange | ringbuf_get | ringbuf_getRange
 */
uint8_t ringbuf_getAt(
	IN const ringbuf_SContext_t* const _lppodContext,
	IN const uint16_t _ui16Position
	) {

	uint8_t ui8Return = 0xFF;

	if( _ui16Position < _lppodContext->ui16WriteOffset) {
		uint16_t ui16Index = _lppodContext->ui16ReadIndex + _ui16Position;
		if( ui16Index >= _lppodContext->ui16Size) {
			ui16Index -= _lppodContext->ui16Size;
		}
		ui8Return = _lppodContext->lpui8Storage[ui16Index];
	}

	return ui8Return;
}


/*!
 * \brief
 * Gets a range of elements of the specified ring buffer element.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * \param _lpvData
 * Specifies the destination buffer.
 *
 * \param _ui16StartIndex
 * Specifies the first element to be read.
 *
 * \param _ui16Length
 * Specifies the number of elements to be read.
 * 
 * \returns
 * The amount of elements which were read to the buffer.
 * 
 * This function contains an under run prevention. In case the index of the first element is out of bound or there are less than
 * the requested amount of elements in the buffer, no further action is taken besides returning the number of already read bytes.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * - The destination buffer must be capable of holding the requested amount of bytes.
 * 
 * \see
 * ringbuf_init | ringbuf_isEmpty | ringbuf_pop | ringbuf_popRange | ringbuf_push | ringbuf_pushRange | ringbuf_get
 */
uint16_t ringbuf_getRange(
	IN const ringbuf_SContext_t* const _lppodContext,
	OUT void* const _lpvData,
	IN const uint16_t _ui16StartIndex,
	IN const uint16_t _ui16Length
	) {

	uint8_t* lpui8 = (uint8_t*)_lpvData;

	// The maximal read length is constrained by the number of available elements
	uint16_t ui16Remaining = _lppodContext->ui16WriteOffset;
	if( _ui16Length < ui16Remaining) {
		ui16Remaining = _ui16Length;
	}

	// Calculate the absolute index of the first element to be read
	uint16_t ui16Index = _lppodContext->ui16ReadIndex + _ui16StartIndex;
	if( ui16Index >= _lppodContext->ui16Size) {
		ui16Index -= _lppodContext->ui16Size;
	}

	// Read until wrap around
	while( ui16Index < _lppodContext->ui16Size && ui16Remaining) {
		*lpui8++ = _lppodContext->lpui8Storage[ui16Index++];
		ui16Remaining--;
	}

	// Read remaining bytes after wrap around
	ui16Index = 0;
	while( ui16Remaining) {
		*lpui8++ = _lppodContext->lpui8Storage[ui16Index++];
		ui16Remaining--;
	}

	return lpui8 - (uint8_t*)_lpvData;
}


/*!
 * \brief
 * Pops the oldest ring buffer element.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \returns
 * The oldest element or 0xFF if there is none.
 * 
 * This function contains an under run prevention. In case the buffer is empty, 0xFF is returned and no further action is taken.
 * Otherwise, the oldest element is removed and returned.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isEmpty | ringbuf_get | ringbuf_getAt | ringbuf_push | ringbuf_drop
 */
uint8_t ringbuf_pop(
	INOUT ringbuf_SContext_t* const _lppodContext
	) {

	uint8_t ui8Return = 0xFF;

	// Buffer volatile data
	const uint16_t ui16WriteOffset = _lppodContext->ui16WriteOffset;
	const uint16_t ui16ReadIndex = _lppodContext->ui16ReadIndex;

	if( ui16WriteOffset) {
		ui8Return = _lppodContext->lpui8Storage[ui16ReadIndex];
		_lppodContext->ui16WriteOffset = ui16WriteOffset - 1;
		if( ui16ReadIndex + 1 >= _lppodContext->ui16Size) {
			_lppodContext->ui16ReadIndex = 0;
		} else {
			_lppodContext->ui16ReadIndex = ui16ReadIndex + 1;
		}
	}

	return ui8Return;
}


/*!
 * \brief
 * Pops the specified amount of ring buffer elements.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * \param _lpvData
 * Specifies the destination buffer for the popped elements.
 *
 * \param _ui16Length
 * Specifies the amount of elements to be popped.
 * 
 * \returns
 * The actual amount of elements which were popped
 * 
 * This function contains an under run prevention. In case the buffer does not contain the requested amount of elements, only
 * the available elements are popped.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isEmpty | ringbuf_get | ringbuf_getAt | ringbuf_push | ringbuf_pushRange | ringbuf_drop | ringbuf_pop
 */
uint16_t ringbuf_popRange(
	INOUT ringbuf_SContext_t* const _lppodContext,
	OUT void* const _lpvData,
	IN const uint16_t _ui16Length
	) {

	uint8_t* lpui8 = (uint8_t*)_lpvData;

	// The maximal read length is constrained by the number of available elements
	uint16_t ui16Remaining = _lppodContext->ui16WriteOffset;
	_lppodContext->ui16WriteOffset = ui16Remaining - _ui16Length;
	if( _ui16Length < ui16Remaining) {
		ui16Remaining = _ui16Length;
	}

	// Calculate read index after the elements were popped
	uint16_t ui16Index = _lppodContext->ui16ReadIndex;
	if( ui16Index < ui16Remaining) {
		_lppodContext->ui16ReadIndex = _lppodContext->ui16Size - ui16Remaining + ui16Index;
	} else {
		_lppodContext->ui16ReadIndex = ui16Index - ui16Remaining;
	}

	// Read until warp around
	while( ui16Index < _lppodContext->ui16Size && ui16Remaining) {
		*lpui8++ = _lppodContext->lpui8Storage[ui16Index++];
		ui16Remaining--;
	}

	// Read remaining bytes after wrap around
	ui16Index = 0;
	while( ui16Remaining) {
		*lpui8++ = _lppodContext->lpui8Storage[ui16Index++];
		ui16Remaining--;
	}

	return lpui8 - (uint8_t*)_lpvData;
}


/*!
 * \brief
 * Pushes an element into the ring buffer.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * \param _ui8Value
 * Specifies the value to be pushed.
 * 
 * This function contains an overflow prevention. In case the buffer is full, no further action is taken.
 * Otherwise, the specified element is added behind any other elements.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isFull | ringbuf_get | ringbuf_getAt | ringbuf_pop | ringbuf_popRange | ringbuf_pushRange
 */
void ringbuf_push(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const uint8_t _ui8Value
	) {

	// Buffer volatile data
	const uint16_t ui16WriteOffset = _lppodContext->ui16WriteOffset;

	if( ui16WriteOffset < _lppodContext->ui16Size) {
		uint16_t ui16WriteIndex = _lppodContext->ui16ReadIndex + ui16WriteOffset;
		if( ui16WriteIndex >= _lppodContext->ui16Size) {
			ui16WriteIndex -= _lppodContext->ui16Size;
		}
		_lppodContext->ui16WriteOffset = ui16WriteOffset + 1;
		_lppodContext->lpui8Storage[ui16WriteIndex] = _ui8Value;
	}
}


/*!
 * \brief
 * Pushes the specified amount of elements into the ring buffer.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 *
 * \param _lpvData
 * Specifies the source buffer.
 *
 * \param _ui16Elements
 * Specifies the amount of elements.
 *
 * \returns
 * The amount of elements which were actually pushed.
 * 
 * This function contains an overflow prevention. In case there is not enough free space for all elements, elements are pushed
 * until the buffer is full.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_isFull | ringbuf_get | ringbuf_getAt | ringbuf_pop | ringbuf_popRange | ringbuf_push
 */
uint16_t ringbuf_pushRange(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const void* const _lpvData,
	IN const uint16_t _ui16Elements
	) {

	const uint8_t* lpui8 = (const uint8_t*)_lpvData;

	// Buffer volatile data
	const uint16_t ui16WriteOffset = _lppodContext->ui16WriteOffset;

	// Get current write index
	uint16_t ui16Index = ui16WriteOffset + _lppodContext->ui16ReadIndex;
	if( ui16Index > _lppodContext->ui16Size) {
		ui16Index -= _lppodContext->ui16Size;
	}

	// Calculate the amount of elements which can actually be pushed
	uint16_t ui16Remaining = _lppodContext->ui16Size - ui16WriteOffset;
	if( ui16Remaining > _ui16Elements) {
		ui16Remaining = _ui16Elements;
	}

	// Push until wrap around
	while( ui16Index < _lppodContext->ui16Size && ui16Remaining) {
		_lppodContext->lpui8Storage[ui16Index++] = *lpui8++;
		ui16Remaining--;
	}

	// Push remaining bytes after warp around
	ui16Index = 0;
	while( ui16Remaining) {
		_lppodContext->lpui8Storage[ui16Index++] = *lpui8++;
		ui16Remaining--;
	}

	return lpui8 - (const uint8_t*)_lpvData;
}

/*!
 * \brief
 * Drops the specified amount of bytes from the beginning of the ring buffer.
 * 
 * \param _lppodContext
 * Specifies the ring buffer to operate on.
 * 
 * \param _ui16Elements
 * Specifies the amount of bytes to drop. 
 * 
 * The buffer is just cleared when dropping more bytes than there are actually available.
 *
 * \warning
 * - This function is not interrupt safe.
 * - The ring buffer must be initialized or results are unpredictable.
 * 
 * \see
 * ringbuf_init | ringbuf_clear | ringbuf_pop | ringbuf_popRange
 */
void ringbuf_drop(
	INOUT ringbuf_SContext_t* const _lppodContext,
	IN const uint16_t _ui16Elements
	) {

	if( _ui16Elements >= _lppodContext->ui16Size) {
		_lppodContext->ui16ReadIndex = 0;
		_lppodContext->ui16WriteOffset = 0;
	} else {
		_lppodContext->ui16WriteOffset -= _ui16Elements;
		const uint16_t ui16ReadIndex = _lppodContext->ui16ReadIndex + _lppodContext->ui16Size - _ui16Elements;
		if( ui16ReadIndex >= _lppodContext->ui16Size) {
			_lppodContext->ui16ReadIndex -= _lppodContext->ui16Size;
		} else {
			_lppodContext->ui16ReadIndex = ui16ReadIndex;
		}
	}
}
