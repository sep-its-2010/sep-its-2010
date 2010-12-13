package sep.conquest.model;

/**
 * Represents the directions the pucks can be adjusted. The source of the
 * coordinate system is in the left upper corner and the x-coordinate grows
 * downwards. The y-coordinate grows rightwards. The Orientation of the puck is
 * described in the following: 
 * 
 * UP: Means that the puck looks in the
 * direction of growing y-coordinates 
 * 
 * DOWN: Means that the puck looks in the
 * direction of shrinking y-coordinates 
 * 
 * LEFT: Means that the puck looks in
 * the direction of growing x-coordinates 
 * 
 * RIGHT: Means that the puck look
 * in the direction of shrinking x-coordinates
 * 
 * @author Andreas Poxrucker
 * 
 */
public enum Orientation {
	
	/**
	 * Orientation along initial defined 'up' direction. (growing y-coordinates)
	 */
	UP,

	/**
	 * Orientation 180 degree turned. (shrinking y-coordinates)
	 */
	DOWN,

	/**
	 * Orientation 90 degree left. (growing x-coordinates)
	 */
	LEFT,

	/**
	 * Orientation 90 degree right. (shrinking x-coordinates)
	 */
	RIGHT,

	/**
	 * Orientation is unknown.
	 */
	UNKNOWN;
	
	public static Orientation getTurnedOrientation(int turn, Orientation ori) {
	  if (ori == UNKNOWN) {
	    return UNKNOWN;
	  } else {
	    int newOriOrdinal = (ori.ordinal() + (turn % 4) + 4) % 4;
	    return Orientation.values()[newOriOrdinal];
	  }
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
