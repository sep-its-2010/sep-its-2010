package sep.conquest.model.behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sep.conquest.model.AStarPathFinder;
import sep.conquest.model.PathNode;
import sep.conquest.model.Puck;
import sep.conquest.model.State;
import sep.conquest.util.Utility;

/**
 * RemovePathlessBehaviour removes frontier nodes that are not reachable by the
 * current position.
 * 
 * @author Andreas Wilhelm
 */
public final class RemovePathlessBehaviour extends Behaviour {

  /**
   * The constructor enables chain-handling by calling the constructor of the
   * super-class (Behaviour).
   * 
   * @param stateLevel
   *          The level of the state.
   * @param next
   *          A reference to the next behaviour.
   */
  protected RemovePathlessBehaviour(State stateLevel, IBehaviour next) {
    super(stateLevel, next);
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
   */
  public boolean execute(Map<Integer, Integer> map, Puck robot) {
    // If Puck is currently waiting for a response message, don't execute
    // the behaviour.
    if (robot.isMessageExpected()) {
      return false;
    }

    // If there are no FrontierNodes left, change to RETURN behaviour.
    if (map.isEmpty()) {
      robot.changeBehaviour(State.RETURN);
    }

    // Compute and save all nodes that can not be reached and will not be
    // considered in further calculations.
    List<Integer> toRemove = new ArrayList<Integer>();
    for (Integer key : map.keySet()) {
      AStarPathFinder aStar = new AStarPathFinder();
      int[] start = robot.getRobotStatus().get(robot.getID()).getPosition();
      int[][] dest = { Utility.extractCoordinates(key) };
      PathNode[] paths = aStar.find(robot, start, dest);
      if (paths[0] == null) {
        toRemove.add(key);
      }
    }
    // Remove all nodes that can not be reached.
    for (Integer key : toRemove) {
      map.remove(key);
    }

    // Execute next behaviour.
    return super.execute(map, robot);
  }
}
