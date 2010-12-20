#ifndef comdef_h__
#define comdef_h__

#include <stddef.h>
#include <stdbool.h>
#include <stdint.h>

#ifndef FCY
#	warning "FCY (instruction cyle frequency) not defined; defaulting to 7.3728 MHz!"
#	define FCY 7372800
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


#endif /* comdef_h__ */
