package sep.conquest.model;

/**
 * SpeedLevel describes the motion-speed of the robots.
 * 
 * @author Andreas Wilhelm
 *
 */
public enum SpeedLevel {
	
	/**
	 * The robot has to stop.
	 */
	ZERO_SPEED,
	
	/**
	 * A slow motion mode.
	 */
	CRAWLING_SPEED,
	
	/**
	 * The usual motion speed of the robots.
	 */
	NORMAL_SPEED,
	
	/**
	 * A motion-mode which is fast.
	 */
	LIGHT_SPEED,
	
	/**
	 * A very fast motion-mode.
	 */
	RIDICULOUS_SPEED,
	
	/**
	 * Don't try!
	 */
	LUDICROUS_SPEED
}
