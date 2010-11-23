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
	UNKNOWN;

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
