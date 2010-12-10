#ifndef subs_node_h__
#define subs_node_h__

#include "common.h"
#include "subs_types.h"

/*!
 * Stores the type of the last visited node.
 * 
 * \remarks
 * The node-type is NODE_TYPE__UNKNOWN by default and will be set after a node has been detected.
 */
static uint8_t subs_node_sui8CurrentNodeType = NODE_TYPE__UNKNOWN;

bool subs_node_run( void);

void subs_node_reset( void);


#endif /* subs_node_h__ */
