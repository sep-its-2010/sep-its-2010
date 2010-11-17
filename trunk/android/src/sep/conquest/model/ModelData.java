package sep.conquest.model;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The ModelData contains the information-data of the Environment class on
 * update events. It will be sent to the registered <code>Activities</code>
 * according to the model-view-controller pattern.
 *  
 * @author Andreas Wilhelm
 *
 */
public class ModelData implements Iterable<RobotStatus> {
	
	/**
	 * A local copy of the global map.
	 */
	private GridMap map;
	
	/**
	 * A <code>TreeMap</code> which contains the information of all robots.
	 */
	private TreeMap<UUID, RobotStatus> robotStatus;

	/**
	 * This constructor takes the map and the robot-informations.
	 * 
	 * @param map The global map.
	 * @param robotStatus The robot-informations.
	 */
	public ModelData(GridMap map, TreeMap<UUID, RobotStatus> robotStatus) {
		this.map = map;
		this.robotStatus = robotStatus;
	}
	
	/**
	 * This constructor takes only a map, the robot-informations have to be
	 * submitted afterwards by the <code>addRobotStatus</code> method.
	 * 
	 * @param map The global map.
	 */
	public ModelData(GridMap map) {
		this.map = map;
	}
	
	/**
	 * The method adds a new status-information of a specific robot to the
	 * <code>ModelData</code>.
	 * 
	 * @param id The id of the robot.
	 * @param status The status of the robot.
	 */
	public void addRobotStatus(UUID id, RobotStatus status) {
		robotStatus.put(id, status);
	}

	/**
	 * Returns the stored map.
	 * 
	 * @return The map.
	 */
	public GridMap getMap() {
		return map;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<RobotStatus> iterator() {
		return new RobotStatusIterator(robotStatus);
	}
}
