#ifndef subs_node_h__
#define subs_node_h__

#include "common.h"


enum {
	SUBS_NODE_WHITE_THRESHOLD = 600, ///< Specifies the threshold for detecting white underground.
	SUBS_NODE_BLACK_THRESHOLD = 290, ///< Specifies the threshold for detecting black underground.
	SUBS_NODE_CROSSING_LINE_THRESHOLD = SUBS_NODE_BLACK_THRESHOLD + 180, ///< Specifies the threshold for crossing line detection.
	SUBS_NODE_CENTER_STEPS = 290 ///< Specifies the amount of steps to drive to reach the center of a detected node.
};


bool subs_node_run( void);

void subs_node_reset( void);

#endif /* subs_node_h__ */
