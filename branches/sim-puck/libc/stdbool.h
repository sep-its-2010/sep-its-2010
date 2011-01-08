#ifndef stdbool_h__
#define stdbool_h__

#ifdef __cplusplus
	typedef bool _Bool;
#else
#	ifndef __bool_true_false_are_defined
		typedef _Bool bool;
#		define true 1
#		define false 0
#		define __bool_true_false_are_defined 1
#	endif
#endif

#endif /* stdbool_h__ */
