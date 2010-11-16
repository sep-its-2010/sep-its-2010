package sep.conquest.model;

/**
 * Represents the directions the pucks can be adjusted.
 * 
 * @author Andreas Poxrucker
 *
 */
public enum Orientation {

	/**
	 * Orientation along initial defined 'up' direction.
	 */
	UP,
	
	/**
	 * Orientation 180 degree turned.
	 */
	DOWN,
	
	/**
	 * Orientation 90 degree left.
	 */
	LEFT,
	
	/**
	 * Orientation 90 degree right.
	 */
	RIGHT,
	
	/**
	 * Orientation is unknown.
	 */
	UNKNOWN
}
