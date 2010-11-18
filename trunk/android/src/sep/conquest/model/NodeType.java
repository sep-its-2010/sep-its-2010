package sep.conquest.model;

/**
 * The enum NodeType saves the different types of the node.
 * 
 * @author Florian Lorenz
 */
public enum NodeType{
	/**
	 * Left upper corner.
	 */
	TOPLEFTEDGE,
	
	/**
	 * Right upper corner.
	 */
	TOPRIGHTEDGE,
	
	/**
	 * Left lower corner.
	 */
	BOTTOMLEFTEDGE,
	
	/**
	 * Right lower corner.
	 */
	BOTTOMRIGHTEDGE,
	
	/**
	 * T-Cross.
	 */
	TOPT,
	
	/**
	 * T-Cross turned 90 degrees.
	 */
	RIGHTT,

	/**
	 * T-Cross turned 180 degrees.
	 */
	BOTTOMT,
	
	/**
	 * T-Cross turned 270 degrees.
	 */
	LEFTT,
	
	/**
	 * A cross with two lines.
	 */
	CROSS,
	
	/**
	 * The node hasen't been visited yet.
	 */
	FRONTIERNODE;
}