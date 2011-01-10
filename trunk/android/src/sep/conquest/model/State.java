package sep.conquest.model;

/**
 * Represents the states of the robots.
 * 
 * @author Andreas Poxrucker
 *
 */
public enum State {

	/**
	 * Initial state.
	 */
	IDLE,
	
	/**
	 * Localize procedure.
	 */
	LOCALIZE,
	
	/**
	 * Wait until all robots are localized.
	 */
	LOCALIZED,
	
	/**
	 * Explore unknown map.
	 */
	EXPLORE,
	
	/**
	 * Return to initial position on the map.
	 */
	RETURN,
	
	/**
	 * Robot has finished exploration.
	 */
	FINISH,
	
	/**
	 * Error occurred in one of the other states.
	 */
	ERROR
}
