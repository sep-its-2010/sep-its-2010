package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
import sep.conquest.util.Utility;

/**
 * ReturnBehaviour represents a behaviour for robots that have to return to
 * their start-nodes. It extends the Behaviour class for enabling a
 * behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class ReturnBehaviour extends Behaviour {

  /**
   * The constructor enables chain-handling by calling the constructor of the
   * super-class (Behaviour).
   * 
   * @param stateLevel
   *          The level of the state.
   * @param next
   *          A reference to the next behaviour.
   */
  protected ReturnBehaviour(State stateLevel, IBehaviour next) {
    super(stateLevel, next);
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
   */
  public boolean execute(Map<Integer, Integer> map, Puck robot) {
    // If message is expected, Behaviour is not executed.
    if (robot.isMessageExpected()) {
      return false;
    }

    boolean changed = false;

    if (map.isEmpty()) {
      // Get start position of the robot.
      int startKey = LocalLocalizeBehaviour.startPositions.get(robot.getID());
      int[] startPos = Utility.extractCoordinates(startKey);

      // Get recent position of the robot.
      RobotStatus status = robot.getRobotStatus().get(robot.getID());
      int[] pos = status.getPosition();

      // If robot has returned to its initial position, change
      if ((startPos[0] == pos[0]) && (startPos[1] == pos[1])) {
        robot.changeBehaviour(State.FINISH);
      } else {
        map.put(startKey, 1);
        changed = true;
      }
    }
    return super.execute(map, robot) || changed;
  }
}
