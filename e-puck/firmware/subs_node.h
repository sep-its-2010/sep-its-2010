#ifndef subs_node_h__
#define subs_node_h__

#include "common.h"


enum {
	SUBS_NODE_CROSSING_LINE_THRESHOLD = 500, ///< Specifies the threshold for crossing line detection.
	SUBS_NODE_CENTER_STEPS = 265, ///< Specifies the amount of steps to drive to reach the center of a detected node.
	SUBS_NODE_MARKER_LENGTH = 265, ///< Specifies the length of the node marker in motor steps.
	SUBS_NODE_MARKER_WIDTH = 50 ///< Specifies the width of the node marker in motor steps.
};


bool subs_node_run( void);

void subs_node_reset( void);

#endif /* subs_node_h__ */
