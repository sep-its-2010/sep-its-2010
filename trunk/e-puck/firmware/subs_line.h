#ifndef subs_line_h__
#define subs_line_h__

#include "common.h"


enum {
	SUBS_LINE_CENTERING_CYCLES = 30, ///< Specifies the amount of line centering cycles after a turn has finished.
	SUBS_LINE_MAX_DELAY_CYCLES = 64, ///< Specifies the maximal PID dead time buffer depth in cycles.
	SUBS_LINE_DELAY_CYCLES = 3, ///< Specifies the actual delay in cycles.
	SUBS_LINE_MAX_ABS_INTEGRATION = 5000, ///< Specifies the upper bound of the absolute value of the PID integration part.
	SUBS_LINE_PID_FIXPOINT_OFFSET = 1 << 8, ///< Specifies the fix point divider.
	SUBS_LINE_P_FIXPOINT_FACTOR = (int16_t)( 1.5f * SUBS_LINE_PID_FIXPOINT_OFFSET), ///< Specifies the fix point factor of the PID proportional part.
	SUBS_LINE_I_FIXPOINT_FACTOR = (int16_t)( 0.1f * SUBS_LINE_PID_FIXPOINT_OFFSET), ///< Specifies the fix point factor of the PID integration part.
	SUBS_LINE_D_FIXPOINT_FACTOR = (int16_t)( 0.6f * SUBS_LINE_PID_FIXPOINT_OFFSET) ///< Specifies the fix point factor of the PID differentiation part.
};


bool subs_line_run( void);

void subs_line_reset( void);

#endif /* subs_line_h__ */
