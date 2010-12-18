#ifndef subs_line_h__
#define subs_line_h__

#include "common.h"


enum {
	SUBS_LINE_MAX_ABS_INTEGRATION = 15000, ///< Specifies the upper bound of the absolute value of the PID integration part.
	SUBS_LINE_PID_FIXPOINT_OFFSET = 1 << 8, ///< Specifies the fix point divider.
	SUBS_LINE_P_FIXPOINT_FACTOR = (int16_t)( 1.5f * SUBS_LINE_PID_FIXPOINT_OFFSET), ///< Specifies the fix point factor of the PID proportional part.
	SUBS_LINE_I_FIXPOINT_FACTOR = (int16_t)( 0.3f * SUBS_LINE_PID_FIXPOINT_OFFSET), ///< Specifies the fix point factor of the PID integration part.
	SUBS_LINE_D_FIXPOINT_FACTOR = (int16_t)( 0.5f * SUBS_LINE_PID_FIXPOINT_OFFSET) ///< Specifies the fix point factor of the PID differentiation part.
};


bool subs_line_run( void);

void subs_line_reset( void);

#endif /* subs_line_h__ */
