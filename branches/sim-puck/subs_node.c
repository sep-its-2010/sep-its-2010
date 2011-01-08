#include "hal_motors.h"
#include "conquest.h"
#include "conquest_sim.h"

#include "subs_node.h"


/*!
 * \brief
 * Subsumption layer for node detection.
 * 
 * \returns
 * - \c true to block higher level layers
 * 
 * The node detection is simulator based. It will be detected after the e-puck has driven a specified amount of steps.
 * 
 * \see
 * subs_node_reset
 */
bool subs_node_run( void) {

	if( hal_motors_getStepsLeft() >= 1000 && hal_motors_getStepsRight() >= 1000) {
		hal_motors_setSpeed( 0, 0);
		hal_motors_setSteps( 0);
		conquest_setLastNode( conquest_sim_move());
		conquest_setState( CONQUEST_STATE__HIT_NODE);
	}

	return true;
}


/*!
 * \brief
 * Resets the subsumption node detection layer.
 *
 * \see
 * subs_node_run
 */
void subs_node_reset( void) {

}
