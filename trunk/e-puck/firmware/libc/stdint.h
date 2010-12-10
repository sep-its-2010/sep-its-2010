#ifndef stdint_h__
#define stdint_h__

// Based on http://www.opengroup.org/onlinepubs/9699919799/basedefs/stdint.h.html

// Signed fixed width types
typedef signed char int8_t;
typedef signed int int16_t;
typedef signed long int32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef signed long long int64_t;
#endif

// Unsigned fixed width types
typedef unsigned char uint8_t;
typedef unsigned int uint16_t;
typedef unsigned long uint32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef unsigned long long uint64_t;
#endif

// Signed least width types
typedef int8_t int_least8_t;
typedef int16_t int_least16_t;
typedef int32_t int_least32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef int64_t int_least64_t;
#endif

// Unsigned least width types
typedef uint8_t uint_least8_t;
typedef uint16_t uint_least16_t;
typedef uint32_t uint_least32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef uint64_t uint_least64_t;
#endif

// Signed fast types
typedef int16_t int_fast8_t;
typedef int16_t int_fast16_t;
typedef int32_t int_fast32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef int64_t int_fast64_t;
#endif

// Unsigned fast types
typedef uint16_t uint_fast8_t;
typedef uint16_t uint_fast16_t;
typedef uint32_t uint_fast32_t;
#ifndef __STRICT_ANSI__
__extension__ typedef uint64_t uint_fast64_t;
#endif

// Signed and unsigned types capable of holding an object pointer
typedef signed int intptr_t;
typedef unsigned int uintptr_t;

// Signed and unsigned maximal width types
#ifndef __STRICT_ANSI__
typedef int64_t intmax_t;
typedef uint64_t uintmax_t;
#else
typedef int32_t intmax_t;
typedef uint32_t uintmax_t;
#endif

#endif /* stdint_h__ */
