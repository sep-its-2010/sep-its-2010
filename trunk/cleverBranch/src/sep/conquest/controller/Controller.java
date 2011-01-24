package sep.conquest.controller;

import java.util.UUID;

import sep.conquest.model.Environment;
import sep.conquest.model.Orientation;
import sep.conquest.model.SpeedLevel;

/**
 * The Controller class represents the controller corresponding to the model-
 * view-controller pattern. It is a unified interface between each
 * <code>Activity</code>
 * 
 * @author Andreas Wilhelm
 */
public class Controller {

  /**
   * The singleton instance for the Controller class.
   */
  private static final Controller INSTANCE = new Controller();

  /**
   * The private constructor to realize the singleton pattern. It also binds the
   * reference to the environment (<code>Model</code>).
   */
  private Controller() {
  }

  /**
   * The getInstance method returns the singleton object of the Controller
   * class.
   * 
   * @return the singleton instance of Environment.
   */
  public static Controller getInstance() {
    return INSTANCE;
  }

  /**
   * This method returns the environment, so the <code>Activities</code> are
   * allowed to register at the model.
   * 
   * @param env
   *          the environment.
   */
  public Environment getEnv() {
    return Environment.getInstance();
  }

  /**
   * Initiates a left-command at the environment for a specific robot.
   * 
   * @param ID
   *          The ID of the robot.
   */
  public void left(UUID ID) {
    Environment.getInstance().driveCommand(ID, Orientation.LEFT);
  }

  /**
   * Initiates a right-command at the environment for a specific robot.
   * 
   * @param ID
   *          The ID of the robot.
   */
  public void right(UUID ID) {
    Environment.getInstance().driveCommand(ID, Orientation.RIGHT);
  }

  /**
   * Initiates a forward-command at the environment for a specific robot.
   * 
   * @param ID
   *          the ID of the robot.
   */
  public void forward(UUID ID) {
    Environment.getInstance().driveCommand(ID, Orientation.UP);
  }

  /**
   * Initiates a turn-command at the environment for a specific robot.
   * 
   * @param ID
   *          The ID of the robot.
   */
  public void turn(UUID ID) {
    Environment.getInstance().driveCommand(ID, Orientation.DOWN);
  }

  /**
   * Sets the speed of a specific robot at the environment.
   * 
   * @param ID
   *          The ID of the robot.
   * @param speed
   *          The speed of the robot.
   */
  public void setSpeed(UUID ID, SpeedLevel speed) {
    Environment.getInstance().setSpeed(ID, speed);
  }

  /**
   * Sets a specific robot to be controlled by the user.
   * 
   * @param ID
   *          the ID of the robot.
   * @param enabled
   *          true, if the robot should be controlled, otherwise false.
   */
  public void setControlled(UUID ID, boolean enabled) {
    Environment.getInstance().setControlled(ID, enabled);
  }

  /**
   * Resets the Environment.
   */
  public void destroy() {
    Environment.getInstance().destroy();
  }
}