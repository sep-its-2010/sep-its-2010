package sep.conquest.model;

/**
 * @author Andreas Wilhelm
 *
 */
public class RobotStatus {
	
	/**
	 * The current position of the robot within the map.
	 */
	private final int[] POSITION;
	
	/**
	 * The current orientation of the robot within the map.
	 */
	private final Orientation ORIENTATION;
	
	/**
	 * Indicates if the robot is currently moving.
	 */
	private final boolean MOVING;
	
	/**
	 * The current state of the robot.
	 */
	private final State STATE;
	
	/**
	 * The constructor takes every needed parameter to fill the ModelData 
	 * Container.
	 * 
	 * @param pos The position of the robot.
	 * @param orientation The Orientation of the robot.
	 * @param moving Indicates if the robot is moving.
	 * @param state The current state of the robot.
	 */
	public RobotStatus(int[] pos, Orientation orientation, boolean moving, 
			State state) {
		POSITION = pos;
		ORIENTATION = orientation;
		MOVING = moving;
		STATE = state;
	}
	
	/**
	 * Returns the current position of the robot.
	 * 
	 * @return An int-array with x and y position.
	 */
	public int[] getPosition() {
		return POSITION;
	}
	
	/**
	 * Returns the current orientation of the robot.
	 * 
	 * @return The orientation data.
	 */
	public Orientation getOrientation() {
		return ORIENTATION;
	}
	
	/**
	 * Indicates if the robot is currently moving.
	 * 
	 * @return A boolean value which indicates the moving state of the robot.
	 */
	public boolean isMoving() {
		return MOVING;
	}
	
	/**
	 * Returns the current state of the robot.
	 * 
	 * @return The state value.
	 */
	public State getState() {
		return STATE;
	}
}
