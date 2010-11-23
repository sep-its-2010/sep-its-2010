package sep.conquest.model;

/**
 * The RobotState represents the current status of a specific Puck. It contains
 * the position, the orientation, the indicator whether the robot is moving as
 * well as the current state.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class RobotStatus {

	/**
	 * The current position of the robot within the map.
	 */
	private int[] position;

	/**
	 * The current orientation of the robot within the map.
	 */
	private Orientation orientation;

	/**
	 * Indicates if the robot is currently moving.
	 */
	private boolean moving;

	/**
	 * The current state of the robot.
	 */
	private State state;

	/**
	 * The constructor takes every needed parameter to fill the ModelData
	 * Container.
	 * 
	 * @param pos
	 *            The position of the robot.
	 * @param orientation
	 *            The Orientation of the robot.
	 * @param moving
	 *            Indicates if the robot is moving.
	 * @param state
	 *            The current state of the robot.
	 */
	public RobotStatus(int[] position, Orientation orientation, boolean moving,
			State state) {
		this.position = position;
		this.orientation = orientation;
		this.moving = moving;
		this.state = state;
	}

	/**
	 * The constructor with initial values.
	 */
	public RobotStatus() {
		position = new int[2];
		position[0] = position[1] = 0;
		orientation = Orientation.UNKNOWN;
		moving = false;
		state = State.IDLE;
	}

	/**
	 * Returns the current position of the robot.
	 * 
	 * @return An int-array with x and y position.
	 */
	public int[] getPosition() {
		return position;
	}

	/**
	 * Returns the current orientation of the robot.
	 * 
	 * @return The orientation data.
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Indicates if the robot is currently moving.
	 * 
	 * @return A boolean value which indicates the moving state of the robot.
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * Returns the current state of the robot.
	 * 
	 * @return The state value.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the position of the robot.
	 * 
	 * @param position
	 *            The position (x and y value).
	 */
	public void setPosition(int[] position) {
		this.position = position;
	}

	/**
	 * Sets the orientation of the robot.
	 * 
	 * @param orientation
	 *            The orientation.
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * Sets the state of the robot.
	 * 
	 * @param state
	 *            The state.
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Sets the moving-state of the robot.
	 * 
	 * @param moving
	 *            Indicates if the robot is moving.
	 */
	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	/**
	 * Sets the complete status of the robot.
	 * 
	 * @param pos
	 *            The position of the robot.
	 * @param orientation
	 *            The Orientation of the robot.
	 * @param moving
	 *            Indicates if the robot is moving.
	 * @param state
	 *            The current state of the robot.
	 */
	public void setRobotStatus(RobotStatus status) {
		this.position = status.getPosition();
		this.orientation = status.getOrientation();
		this.moving = status.isMoving();
		this.state = status.getState();
	}
}
