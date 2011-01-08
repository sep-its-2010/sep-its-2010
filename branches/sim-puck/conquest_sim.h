#ifndef conquest_sim_h__
#define conquest_sim_h__

#include "common.h"

#include "conquest_types.h"


void conquest_sim_reset( void);
conquest_ENode_t conquest_sim_getCurrentNode( void);
conquest_ENode_t conquest_sim_move( void);
conquest_ENode_t conquest_sim_turnLeft( void);
conquest_ENode_t conquest_sim_turnRight( void);


#endif /* conquest_sim_h__ */
