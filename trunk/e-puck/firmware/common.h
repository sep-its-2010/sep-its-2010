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
 * OUT | INOUT | OPT
 */
#define IN

/*!
 * \brief
 * A marker to define denoting function parameters which are only modified but not read.
 * 
 * \see
 * IN | INOUT | OPT
 */
#define OUT

/*!
 * \brief
 * A marker to define denoting function parameters which are read and modified.
 * 
 * \see
 * IN | OUT | OPT
 */
#define INOUT


/*!
 * \brief
 * A marker to define denoting function parameters which are optional.
 * 
 * \see
 * IN | OUT | INOUT
 */
#define OPT


#define ISR					__attribute__( ( __interrupt__))
#define AUTO_PSV			__attribute__( ( auto_psv))
#define NO_AUTO_PSV			__attribute__( ( no_auto_psv))
#define USE_SHADOWING		__attribute__( ( shadow))
#define UNUSED				__attribute__( ( unused))


#endif /* comdef_h__ */
