package sep.conquest.model;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Andreas Wilhelm
 *
 */
public class ModelData implements Iterable<RobotStatus> {
	
	private GridMap map;
	private TreeMap<UUID, RobotStatus> robotStatus;

	public ModelData(GridMap map, TreeMap<UUID, RobotStatus> robotStatus) {
		this.map = map;
		this.robotStatus = robotStatus;
	}
	
	public ModelData(GridMap map) {
		this.map = map;
	}
	
	public void addRobotStatus(UUID id, RobotStatus status) {
		robotStatus.put(id, status);
	}

	/**
	 * 
	 * 
	 * @return the map
	 */
	public GridMap getMap() {
		return map;
	}

	public Iterator<RobotStatus> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
