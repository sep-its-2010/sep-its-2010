#ifndef subs_node_h__
#define subs_node_h__

#include "common.h"
#include "subs_types.h"

enum {
	SUBS_NODE__DETECTION_THRESHOLD = 270, ///< Specifies the threshold for line-detection.
	SUBS_NODE__REQUIRED_MEASUREMENTS = 3, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
	SUBS_NODE__MOVE_ABOVE_CENTER = 250 ///< Specifies the number of steps which have to be performed by both motors to move the robot above the center of the node.
};

bool subs_node_run( void);

void subs_node_reset( void);


#endif /* subs_node_h__ */
