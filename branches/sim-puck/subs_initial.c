#include "conquest.h"
#include "conquest_sim.h"

#include "subs_initial.h"


/*!
 * \brief
 * Subsumption layer for initial node detection.
 * 
 * \returns
 * \c true
 *
 * The detection is simulator based.
 *
 * \see
 * subs_initial_reset
 */
bool subs_initial_run( void) {

	bool blActed = false;

	if( conquest_getState() == CONQUEST_STATE__INITIAL) {
		conquest_setLastNode( conquest_sim_getCurrentNode());
		conquest_setState( CONQUEST_STATE__STOP);

		blActed = true;
	}

	return blActed;
}


/*!
 * \brief
 * Resets the initial node layer.
 * 
 * \see
 * subs_initial_run
 */
void subs_initial_reset( void) {

}
