#ifndef subs_node_h__
#define subs_node_h__

#include "common.h"
#include "subs_types.h"

enum {
	SUBS_NODE__LINE_THRESHOLD = 330, ///< Specifies the threshold for line-detection.
	SUBS_NODE__REQUIRED_MEASUREMENTS = 8, ///< Specifies the number of measurements, which have to provide data above a certain threshold for node-detection.
	SUBS_NODE__MOVE_ABOVE_CENTER = 230 ///< Specifies the number of steps which have to be performed by both motors to move the robot above the center of the node.
};

bool subs_node_run( void);

void subs_node_reset( void);

static inline uint8_t subs_node_getCurrentNodeType( void);

/*!
 * \brief
 * Get the type of the last visited node.
 * 
 * \returns
 * The node-type as integer like it has been defined in the Conquest_ENodeType_t enum in conquest_types.h.
 * 
 * Write detailed description for subs_node_getCurrentNodeType here.
 */
uint8_t subs_node_getCurrentNodeType( void) {

	extern volatile uint8_t subs_node_ui8CurrentNodeType;

	return subs_node_ui8CurrentNodeType;
}


#endif /* subs_node_h__ */
