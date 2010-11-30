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
	 * @return 
	 */
	UP(0),

	/**
	 * Orientation 180 degree turned.
	 */
	DOWN(1),

	/**
	 * Orientation 90 degree left.
	 */
	LEFT(2),

	/**
	 * Orientation 90 degree right.
	 */
	RIGHT(3),

	/**
	 * Orientation is unknown.
	 */
	UNKNOWN(4);

	/**
	 * The figure for the orientation.
	 */
	private int num;
	
	/**
	 * A private constructor to realize figures within Orientation.
	 * 
	 * @param num The figure.
	 */
	private Orientation(int num) {
		this.num = num;
	}
	
	/**
	 * Returns the number for the corresponding enum-object.
	 * 
	 * @return The number.
	 */
	public int getNum() {
		return num;
	}	
	
	/**
	 * The method adds a orientation of the robot and a desired direction to
	 * get the turning-command.
	 * 
	 * @param ori The orientation of the robot.
	 * @param dir The direction in which the robot should drive.
	 * @return The resulting turning-direction.
	 */
	public static Orientation addDirection(Orientation ori, Orientation dir) {
		switch (ori) {
		case UP:
			return dir;
		case DOWN:
			switch (dir) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				return dir;
			}
		case LEFT:
			switch (dir) {
			case UP:
				return RIGHT;
			case DOWN:
				return LEFT;
			case LEFT:
				return UP;
			case RIGHT:
				return DOWN;
			default:
				return dir;
			}
		case RIGHT:
			switch (dir) {
			case UP:
				return LEFT;
			case DOWN:
				return RIGHT;
			case LEFT:
				return DOWN;
			case RIGHT:
				return UP;
			default:
				return dir;
			}
		default:
			return dir;
		}
	}
}
