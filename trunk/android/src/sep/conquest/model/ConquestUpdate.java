package sep.conquest.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * ConquestUpdate will be used for update messages from the Environment class
 * (observable) to registered observer.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class ConquestUpdate implements Cloneable {

  /**
   * The list of map-nodes of the global map.
   */
  private List<MapNode> mapList;

  /**
   * The status of every robot on the map.
   */
  private Map<UUID, RobotStatus> robots;

  /**
   * The names of the robots.
   */
  private Map<UUID, String> robotNames = new TreeMap<UUID, String>();

  /**
   * The borders of the map.
   */
  private int[] borders;

  /**
   * The constructor expects a list of map-nodes as well as the status of every
   * robot on the map.
   * 
   * @param mapList
   *          The list of map-nodes.
   * @param robots
   *          The status of the robots, which are distinguished by the id.
   */
  public ConquestUpdate(List<MapNode> mapList, int[] borders,
      Map<UUID, RobotStatus> robots) {
    this.mapList = mapList;
    this.robots = robots;
    this.borders = borders;
    ComManager comMan = ComManager.getInstance();
    for (UUID key : robots.keySet()) {
      Puck robot = (Puck) comMan.getClient(key);
      robotNames.put(key, robot.getName());
    }
  }

  /**
   * Sets a new status for a specific (id) robot.
   * 
   * @param id
   *          The id of the robot.
   * @param status
   *          The status of the robot.
   */
  public void setRobotStatus(UUID id, RobotStatus status) {
    if (robots.containsKey(id))
      robots.get(id).setRobotStatus(status);
    else
      robots.put(id, status);
  }

  /**
   * Sets a new list of map-nodes for the update.
   * 
   * @param mapList
   */
  public void setMapList(LinkedList<MapNode> mapList) {
    this.mapList = mapList;
  }

  /**
   * Returns the list of map-nodes.
   * 
   * @return The map-nodes.
   */
  public List<MapNode> getMapList() {
    return mapList;
  }

  /**
   * Returns the status of every robot in a map
   * 
   * @return The map of robot-status.
   */
  public Map<UUID, RobotStatus> getRobotStatus() {
    return robots;
  }

  /**
   * Returns a single status of a specific robot.
   * 
   * @param id
   *          The id of the robot.
   * @return The status of the robot.
   */
  public RobotStatus getRobotStatus(UUID id) {
    return robots.get(id);
  }

  /**
   * Sets new borders.
   * 
   * @param borders
   *          The Borders.
   */
  public void setBorders(int[] borders) {
    this.borders = borders;
  }

  /**
   * Returns the name of a specific robot if available. Otherwise it returns
   * null.
   * 
   * @param id
   *          The id of the robot.
   * @return The name of the robot.
   */
  public String getRobotName(UUID id) {
    return robotNames.get(id);
  }

  /**
   * Returns the borders.
   * 
   * @return The borders.
   */
  public int[] getBorders() {
    return borders;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public ConquestUpdate clone() {
    Map<UUID, RobotStatus> copyMap = new TreeMap<UUID, RobotStatus>();
    Set<UUID> keys = robots.keySet();
    for (UUID key : keys) {
      copyMap.put(key, robots.get(key).clone());
    }
    
    LinkedList<MapNode> copyList = new LinkedList<MapNode>();
    for (MapNode node : mapList) {
      copyList.add(node);
    }
    return new ConquestUpdate(copyList, borders, copyMap);
  }
}
