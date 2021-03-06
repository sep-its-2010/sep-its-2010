package sep.conquest.model;

/**
 * Contains methods to send control commands to an implementing class.
 * 
 * @author Andreas Poxrucker
 * 
 */
public interface IRobot {

	/**
	 * Sends 'left'-command to implementing class.
	 */
	void left();

	/**
	 * Sends 'right'-command to implementing class.
	 */
	void right();

	/**
	 * Sends 'up'-command to implementing class.
	 */
	void forward();

	/**
	 * Sends 'turn'-command to implementing class.
	 */
	void turn();
	
	/**
	 * Request the current status of the robot.
	 */
	void requestStatus();

	/**
	 * Sends command to set speed to implementing class.
	 * 
	 * @param level
	 *            The speed to set.
	 */
	void setSpeed(SpeedLevel level);

	/**
	 * Enables (or disables) the manual control of the robot.
	 * 
	 * @param enabled
	 *            <code>true</code> if the robot is controlled by the user,
	 *            otherwise <code>false</code>.
	 */
	void setControlled(boolean enabled);

	/**
	 * Returns if the robot is controlled manually by the user.
	 * 
	 * @return Is controlled.
	 */
	boolean isControlled();
}
