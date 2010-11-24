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
	ZERO_SPEED(0),
	
	/**
	 * A slow motion mode.
	 */
	CRAWLING_SPEED(20),
	
	/**
	 * The usual motion speed of the robots.
	 */
	NORMAL_SPEED(40),
	
	/**
	 * A motion-mode which is fast.
	 */
	LIGHT_SPEED(60),
	
	/**
	 * A very fast motion-mode.
	 */
	RIDICULOUS_SPEED(80),
	
	/**
	 * Don't try!
	 */
	LUDICROUS_SPEED(100);
	
	/**
	 * The speed.
	 */
	private int speed;
	
	/**
	 * The constructor sets the speed.
	 * @param speed The speed.
	 */
	private SpeedLevel(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Returns the speed.
	 * 
	 * @return The speed.
	 */
	public int getSpeed() {
		return speed;
	}
}
