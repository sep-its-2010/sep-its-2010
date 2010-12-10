package sep.conquest.model;

public class SimRobot {

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
   * Indicates whether robot is moving.
   */
  private boolean isMoving = false;
  
  /**
   * Indicates, how many collisions have happened until robot
   * reaches the next node.
   */
  private int collisionCounter = 0;

  /**
   * Used to write messages back to the corresponding VirtualPuck.
   */
  private byte[] outputBuffer;

  /**
   * Used to save incoming request.
   */
  private IRequest inputBuffer;

  /**
   * Constructor.
   * 
   * Creates a new robot status by setting its initial position and orientation.
   * 
   * @param initialPosition
   *          The initial position of the robot.
   * @param initialOrientation
   *          The initial orientation of the robot.
   */
  public SimRobot(int[] initialPosition, Orientation initialOrientation) {
    // If one of the arguments equals null, throw exception.
    if ((initialPosition != null) && (initialOrientation != null)) {
      timestamp = System.currentTimeMillis();
      this.initialOrientation = initialOrientation;
      this.initialPosition = initialPosition.clone();
      position = initialPosition.clone();
      orientation = initialOrientation;
      outputBuffer = new byte[0];
    } else {
      throw new IllegalArgumentException(
          "Position or orientation can not be null");
    }
  }

  /**
   * Returns message from the output buffer.
   * 
   * When message is read, buffer is reseted to length zero;
   * 
   * @return The message from the buffer or an empty array, if there is no
   *         message.
   */
  public byte[] readBuffer() {
    byte[] message = outputBuffer.clone();
    outputBuffer = new byte[0];
    return message;
  }

  /**
   * Writes a message on the output buffer.
   * 
   * @param message
   *          The message to write on the output buffer.
   */
  public void writeBuffer(byte[] message) {
    if (message.length == 32) {
      outputBuffer = message.clone();
    } else {
      throw new IllegalArgumentException("Illegal message length");
    }
  }

  /**
   * Adds a new request message
   * @param request
   */
  public void addRequest(IRequest request) {
    if (request != null) {
      inputBuffer = request;
    } else {
      throw new IllegalArgumentException("Request must not equal null");
    }
  }
  
  /**
   * Returns request message from the input buffer.
   * 
   * @return The request message from the input buffer (can be null);
   */
  public IRequest getRequest() {
    return inputBuffer;
  }
  
  /**
   * Resets the input buffer to null.
   */
  public void clearRequest() {
    inputBuffer = null;
  }

  /**
   * Returns whether robot has a new request.
   * 
   * @return True, if there is a new message, false otherwise.
   */
  public boolean hasRequest() {
    return inputBuffer != null;
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
   * @param newPosition
   *          The new position of the robot.
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
   * @param newOrientation
   *          The new orientation of the robot.
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
   * Returns whether robot is currently moving.
   * 
   * @return True, if robot is currently moving, false otherwise.
   */
  public boolean isMoving() {
    return isMoving;
  }

  /**
   * Set moving state of robot.
   * 
   * @param moving
   *          The moving state of the robot.
   */
  public void setMoving(boolean moving) {
    isMoving = moving;
    
    // If robot is not moving because it has reached a node
    // reset collision counter.
    if (!moving) {
      collisionCounter = 0;
    }
  }
  
  /**
   * Increases the number of collisions that have occurred while
   * not standing on a node.
   */
  public void collide() {
    collisionCounter++;
  }
  
  /**
   * Returns the number of collisions that have occurred while
   * not standing on a node.
   * 
   * @return The number of collisions.
   */
  public int getNumberOfCollisions() {
    return collisionCounter;
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
    outputBuffer = new byte[0];
    inputBuffer = null;
    isMoving = false;
    collisionCounter++;
  }
}