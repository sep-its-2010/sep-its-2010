package sep.conquest.model;

public class SimRobotStatus {

  /**
   * The initial position of the robot.
   */
  private final int[] initialPosition;

  /**
   * The initial orientation of the robot.
   */
  private final Orientation initialOrientation;

  /**
   * The current position of the robot.
   */
  private int[] position;
  
  /**
   * The timestamp when status was created or reseted.
   */
  private long timestamp;

  /**
   * The current orientation if the robot.
   */
  private Orientation orientation;

  /**
   * Constructor.
   * 
   * Creates a new robot status by setting its initial position and orientation.
   * 
   * @param initialPosition The initial position of the robot.
   * @param initialOrientation The initial orientation of the robot.
   */
  public SimRobotStatus(int[] initialPosition, Orientation initialOrientation) {
    // If one of the arguments equals null, throw exception.
    if ((initialPosition != null) && (initialOrientation != null)) {
      timestamp = System.currentTimeMillis();
      this.initialOrientation = initialOrientation;
      this.initialPosition = initialPosition.clone();
      position = initialPosition.clone();
      orientation = initialOrientation;
    } else {
      throw new IllegalArgumentException(
          "Position or orientation can not be null");
    }
  }

  /**
   * Returns the current position of the robot.
   * 
   * @return The current position of the robot.
   */
  public int[] getPosition() {
    return position;
  }

  /**
   * Sets the current position of the robot to a new position.
   * 
   * @param newPosition The new position of the robot.
   */
  public void setPosition(int[] newPosition) {
    // If new position is invalid throw exception. Otherwise save new position.
    if ((newPosition != null) && (newPosition.length == 2)) {
      position[0] = newPosition[0];
      position[1] = newPosition[1];
    } else {
      throw new IllegalArgumentException(
          "New position is null or has invalid length");
    }
  }

  /**
   * Returns the current orientation of the robot.
   * 
   * @return The current orientation of the robot.
   */
  public Orientation getOrientation() {
    return orientation;
  }

  /**
   * Sets the current orientation of the robot to a new orientation.
   * 
   * @param newOrientation The new orientation of the robot.
   */
  public void setOrientation(Orientation newOrientation) {
    // If new orientation is invalid, throw exception.
    if (newOrientation != null) {
      orientation = newOrientation;
    } else {
      throw new IllegalArgumentException("New orientation can not be null");
    }
  }
  
  /**
   * Returns the initial timestamp of the robot.
   * 
   * @return The timestamp when robot was created or last reseted.
   */
  public int getSystemUpTime() {
    return (int) ((timestamp - System.currentTimeMillis()) / 10);
  }

  /**
   * Resets the state to its initial values.
   */
  public void reset() {
    timestamp = System.currentTimeMillis();
    position[0] = initialPosition[0];
    position[1] = initialPosition[1];
    orientation = initialOrientation;
  }
}