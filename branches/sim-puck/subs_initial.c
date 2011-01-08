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

	conquest_setState( conquest_sim_getCurrentNode());
	conquest_setState( CONQUEST_STATE__STOP);

	return true;
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
