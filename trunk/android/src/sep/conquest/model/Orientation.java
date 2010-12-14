package sep.conquest.model;

/**
 * Represents the directions the pucks can be adjusted. The source of the
 * coordinate system is in the left upper corner and the x-coordinate grows
 * downwards. The y-coordinate grows rightwards. The Orientation of the puck is
 * described in the following: 
 * 
 * UP: Means that the puck looks in the
 * direction of shrinking y-coordinates 
 * 
 * DOWN: Means that the puck looks in the
 * direction of growing y-coordinates 
 * 
 * LEFT: Means that the puck looks in
 * the direction of shrinking x-coordinates 
 * 
 * RIGHT: Means that the puck look
 * in the direction of growing x-coordinates
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
	 * Orientation 90 degree left. (growing x-coordinates)
	 */
	LEFT,
	
	/**
   * Orientation 180 degree turned. (shrinking y-coordinates)
   */
  DOWN,

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
		switch(ori) {
		case UP:
			switch (turn) {
			case -1:
				return RIGHT;
			case 0:
				return UP;
			case 1:
				return LEFT;
			case 2:
				return DOWN;
			default: 
				return ori;
			}
		case DOWN:
			switch (turn) {
			case -1:
				return LEFT;
			case 0:
				return DOWN;
			case 1:
				return RIGHT;
			case 2:
				return UP;
			default: 
				return ori;
			}
		case LEFT:
			switch (turn) {
			case -1:
				return UP;
			case 0:
				return LEFT;
			case 1:
				return DOWN;
			case 2:
				return RIGHT;
			default: 
				return ori;
			}	
		case RIGHT:
			switch (turn) {
			case -1:
				return DOWN;
			case 0:
				return RIGHT;
			case 1:
				return UP;
			case 2:
				return LEFT;
			default: 
				return ori;
			}
		default:
			return ori;
		}
	  }
	}
	
	public static Orientation getTurnedOrientation(int[] startNode, int[] endNode) {
		Orientation direction = Orientation.UNKNOWN;

		if (startNode[0] < endNode[0])
			direction = Orientation.LEFT;
		else if (startNode[0] > endNode[0])
			direction = Orientation.RIGHT;
		else if (startNode[1] < endNode[1])
			direction = Orientation.UP;
		else if (startNode[1] > endNode[1])
			direction = Orientation.DOWN;
		
		return direction;
	}
	
	
	
	/**
	 * The method adds a orientation of the robot and a desired direction to
	 * get the turning-command.
	 * 
	 * @param ori The orientation of the robot.
	 * @param dir The direction in which the robot should drive.
	 * @return The resulting turning-direction.
	 */
	public static int addDirection(Orientation ori, Orientation dir) {
		switch (ori) {
		case UP:
			switch (dir) {
			case UP:
				return 0;
			case DOWN:
				return 2;
			case LEFT:
				return -1;
			case RIGHT:
				return 1;
			default:
				return 0;
			}
		case DOWN:
			switch (dir) {
			case UP:
				return 2;
			case DOWN:
				return 0;
			case LEFT:
				return -1;
			case RIGHT:
				return 1;
			default:
				return 0;
			}
		case LEFT:
			switch (dir) {
			case UP:
				return -1;
			case DOWN:
				return 1;
			case LEFT:
				return 0;
			case RIGHT:
				return 2;
			default:
				return 0;
			}
		case RIGHT:
			switch (dir) {
			case UP:
				return 1;
			case DOWN:
				return -1;
			case LEFT:
				return 2;
			case RIGHT:
				return 0;
			default:
				return 0;
			}
		default:
			return 0;
		}
	}
	
	public static Orientation turn(Orientation initial, Orientation dir) {
		switch (initial) {
		case UP:
			switch(dir) {
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				return dir;
			}
		case RIGHT:
			switch(dir) {
			case LEFT:
				return DOWN;
			case RIGHT:
				return UP;
			case UP:
				return RIGHT;
			case DOWN:
				return LEFT;
			default:
				return dir;
			}
		case LEFT:
			switch(dir) {
			case LEFT:
				return UP;
			case RIGHT:
				return DOWN;
			case UP:
				return LEFT;
			case DOWN:
				return RIGHT;
			default:
				return dir;
			}
		default:
			return dir;
		}
	}		
}
