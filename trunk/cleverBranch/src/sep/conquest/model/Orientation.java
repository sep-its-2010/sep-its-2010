package sep.conquest.model;

/**
 * Represents the directions the pucks can be adjusted. The source of the
 * coordinate system is in the left upper corner and the x-coordinate grows
 * downwards. The y-coordinate grows rightwards. The Orientation of the puck is
 * described in the following:
 * 
 * UP: Puck looks in the direction of growing y-coordinates
 * 
 * DOWN: Puck looks in the direction of shrinking y-coordinates
 * 
 * LEFT: Puck looks in the direction of shrinking x-coordinates
 * 
 * RIGHT: Puck looks in the direction of growing x-coordinates
 * 
 * @author Andreas Poxrucker
 * 
 */
public enum Orientation {

  /**
   * Orientation along initial defined 'up' direction. (growing y-coordinates)
   */
  UP(0),

  /**
   * Orientation 90 degree left. (shrinking x-coordinates)
   */
  LEFT(-1),

  /**
   * Orientation 180 degree turned. (shrinking y-coordinates)
   */
  DOWN(2),

  /**
   * Orientation 90 degree right. (growing x-coordinates)
   */
  RIGHT(1),

  /**
   * Orientation is unknown.
   */
  UNKNOWN(4);

  /**
   * Represents UP command.
   */
  private static final int C_UP = 0;
  
  /**
   * Represents LEFT command.
   */
  private static final int C_LEFT = -1;
  
  /**
   * Represents right command.
   */
  private static final int C_RIGHT = 1;
  
  /**
   * Represents down command.
   */
  private static final int C_DOWN = 2;

  /**
   * The number associated with each orientation.
   */
  private int orientation;

  /**
   * The private constructor to enable orientation values.
   * 
   * @param orientation
   */
  private Orientation(int orientation) {
    this.orientation = orientation;
  }

  /**
   * Returns the orientation value.
   * 
   * @return The orientation value.
   */
  public int getOrientation() {
    return orientation;
  }

  public static Orientation getTurnedOrientation(int turn, Orientation ori) {

    if (ori == UNKNOWN) {
      return UNKNOWN;
    } else {
      switch (ori) {
      case UP:
        switch (turn) {
        case C_LEFT:
          return RIGHT;
        case C_UP:
          return UP;
        case C_RIGHT:
          return LEFT;
        case C_DOWN:
          return DOWN;
        default:
          return ori;
        }
      case DOWN:
        switch (turn) {
        case C_LEFT:
          return LEFT;
        case C_UP:
          return DOWN;
        case C_RIGHT:
          return RIGHT;
        case C_DOWN:
          return UP;
        default:
          return ori;
        }
      case LEFT:
        switch (turn) {
        case C_LEFT:
          return UP;
        case C_UP:
          return LEFT;
        case C_RIGHT:
          return DOWN;
        case C_DOWN:
          return RIGHT;
        default:
          return ori;
        }
      case RIGHT:
        switch (turn) {
        case C_LEFT:
          return DOWN;
        case C_UP:
          return RIGHT;
        case C_RIGHT:
          return UP;
        case C_DOWN:
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
      direction = Orientation.RIGHT;
    else if (startNode[0] > endNode[0])
      direction = Orientation.LEFT;
    else if (startNode[1] < endNode[1])
      direction = Orientation.UP;
    else if (startNode[1] > endNode[1])
      direction = Orientation.DOWN;

    return direction;
  }

  /**
   * The method adds a orientation of the robot and a desired direction to get
   * the turning-command.
   * 
   * @param ori
   *          The orientation of the robot.
   * @param dir
   *          The direction in which the robot should drive.
   * @return The resulting turning-direction.
   */
  public static int addGlobalDirection(Orientation ori, Orientation dir) {
    switch (ori) {
    case UP:
      switch (dir) {
      case UP:
        return UP.getOrientation();
      case DOWN:
        return DOWN.getOrientation();
      case LEFT:
        return LEFT.getOrientation();
      case RIGHT:
        return RIGHT.getOrientation();
      default:
        return dir.getOrientation();
      }
    case DOWN:
      switch (dir) {
      case UP:
        return DOWN.getOrientation();
      case DOWN:
        return UP.getOrientation();
      case LEFT:
        return RIGHT.getOrientation();
      case RIGHT:
        return LEFT.getOrientation();
      default:
        return dir.getOrientation();
      }
    case LEFT:
      switch (dir) {
      case UP:
        return RIGHT.getOrientation();
      case DOWN:
        return LEFT.getOrientation();
      case LEFT:
        return UP.getOrientation();
      case RIGHT:
        return DOWN.getOrientation();
      default:
        return dir.getOrientation();
      }
    case RIGHT:
      switch (dir) {
      case UP:
        return LEFT.getOrientation();
      case DOWN:
        return RIGHT.getOrientation();
      case LEFT:
        return DOWN.getOrientation();
      case RIGHT:
        return UP.getOrientation();
      default:
        return dir.getOrientation();
      }
    default:
      return dir.getOrientation();
    }
  }

  public static Orientation turn(Orientation initial, Orientation dir) {
    switch (initial) {
    case UP:
      switch (dir) {
      case LEFT:
        return RIGHT;
      case RIGHT:
        return LEFT;
      default:
        return dir;
      }
    case RIGHT:
      switch (dir) {
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
      switch (dir) {
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

  /**
   * Turn a direction by 180°.
   * 
   * @param dir
   *          The direction.
   * @return The turned direction.
   */
  public static Orientation turn(Orientation dir) {
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
      return UP;
    }
  }

  /**
   * The method adds a orientation of the robot and a desired direction to get
   * the turning-command.
   * 
   * @param ori
   *          The orientation of the robot.
   * @param dir
   *          The direction in which the robot should drive.
   * @return The resulting turning-direction.
   */
  public static int addLocalDirection(Orientation ori, Orientation dir) {
    switch (ori) {
    case UP:
      switch (dir) {
      case UP:
        return UP.getOrientation();
      case DOWN:
        return DOWN.getOrientation();
      case LEFT:
        return RIGHT.getOrientation();
      case RIGHT:
        return LEFT.getOrientation();
      default:
        return UP.getOrientation();
      }
    case DOWN:
      switch (dir) {
      case UP:
        return DOWN.getOrientation();
      case DOWN:
        return UP.getOrientation();
      case LEFT:
        return LEFT.getOrientation();
      case RIGHT:
        return RIGHT.getOrientation();
      default:
        return DOWN.getOrientation();
      }
    case LEFT:
      switch (dir) {
      case UP:
        return LEFT.getOrientation();
      case DOWN:
        return RIGHT.getOrientation();
      case LEFT:
        return UP.getOrientation();
      case RIGHT:
        return DOWN.getOrientation();
      default:
        return UP.getOrientation();
      }
    case RIGHT:
      switch (dir) {
      case UP:
        return RIGHT.getOrientation();
      case DOWN:
        return LEFT.getOrientation();
      case LEFT:
        return DOWN.getOrientation();
      case RIGHT:
        return UP.getOrientation();
      default:
        return UP.getOrientation();
      }
    default:
      return UP.getOrientation();
    }
  }

  /**
   * Returns the Orientation of a specified integer value.
   * 
   * @param ori
   *          The orientation as integer value.
   * @return The returned Orientation.
   */
  public static Orientation getOrientation(int ori) {
    switch (ori) {
    case C_DOWN:
      return DOWN;
    case C_UP:
      return UP;
    case C_LEFT:
      return LEFT;
    case C_RIGHT:
      return RIGHT;
    default:
      return UP;
    }
  }
}