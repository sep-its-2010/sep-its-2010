#include "subs_calibrate.h"
#include "subs_abyss.h"
#include "subs_collision.h"
#include "subs_anayse.h"
#include "subs_move.h"
#include "subs_pid.h"


/*!
 * \brief
 * Initializes the container for the different behaviors.
 */
void subs_init( void) {
}

/*!
 * \brief
 * Coordinates the different behaviors of the subsumption-architecture.
 * 
 * Manages a list of all registered behaviors. While endlessly traversing the list in a loop, every behavior checks if it is supposed to be executed.
 * If a behavior has been executed the loop continues at the first element of the behavior-list.
 * 
 * \see
 * subs_init
 */
void subs_executeBehaviors( void) {
}

//TODO how to register new behaviors?
