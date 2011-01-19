#ifndef comdef_h__
#define comdef_h__

#include <stddef.h>
#include <stdbool.h>
#include <stdint.h>


#ifndef FCY
#	warning "FCY (instruction cyle frequency) not defined; defaulting to 7.3728 MHz!"
#	define FCY 7372800
#endif

// stdlib.h might define a min macro in newer versions; replace it with a function
#ifdef min
#	undef min
#endif

// stdlib.h might define a max macro in newer versions; replace it with a function
#ifdef max
#	undef max
#endif


/*!
 * \brief
 * A marker to define denoting function parameters which are only read but not modified.
 * 
 * \see
 * OUT | INOUT | OPT | UNUSED
 */
#define IN

/*!
 * \brief
 * A marker to define denoting function parameters which are only modified but not read.
 * 
 * \see
 * IN | INOUT | OPT | UNUSED
 */
#define OUT

/*!
 * \brief
 * A marker to define denoting function parameters which are read and modified.
 * 
 * \see
 * IN | OUT | OPT | UNUSED
 */
#define INOUT


/*!
 * \brief
 * A marker to define denoting function parameters which are optional.
 * 
 * \see
 * IN | OUT | INOUT | UNUSED
 */
#define OPT


/*!
 * \brief
 * A marker to define denoting function parameters which are intentionally unused.
 * 
 * \see
 * IN | OUT | INOUT
 */
#define UNUSED __attribute__( ( unused))


/*!
 * \brief
 * Enables automatic PSV management.
 *
 * This function attribute is required for ISRs which use constants-in-code.
 * 
 * \see
 * NO_AUTO_PSV
 */
#define AUTO_PSV __attribute__( ( auto_psv))


/*!
 * \brief
 * Disables automatic PSV management.
 * 
 * \see
 * AUTO_PSV
 */
#define NO_AUTO_PSV	__attribute__( ( no_auto_psv))


static inline int16_t max(
	IN const int16_t _i16First,
	IN const int16_t _i16Second
	);

static inline int16_t min(
	IN const int16_t _i16First,
	IN const int16_t _i16Second
	);


/*!
 * \brief
 * Returns the maximum of two operands.
 * 
 * \param _i16First
 * Specifies the first operand.
 * 
 * \param _i16Second
 * Specifies the second operand.
 * 
 * \returns
 * The first operand if it is greater than the second one. Otherwise the second operand is returned.
 * 
 * \see
 * min
 */
int16_t max(
	IN const int16_t _i16First,
	IN const int16_t _i16Second
	) {

	return _i16First > _i16Second ? _i16First : _i16Second;
}


/*!
 * \brief
 * Returns the minimum of two operands.
 * 
 * \param _i16First
 * Specifies the first operand.
 * 
 * \param _i16Second
 * Specifies the second operand.
 * 
 * \returns
 * The first operand if it is lesser than the second one. Otherwise the second operand is returned.
 * 
 * \see
 * max
 */
int16_t min(
	IN const int16_t _i16First,
	IN const int16_t _i16Second
	) {

	return _i16First < _i16Second ? _i16First : _i16Second;
}


#endif /* comdef_h__ */
