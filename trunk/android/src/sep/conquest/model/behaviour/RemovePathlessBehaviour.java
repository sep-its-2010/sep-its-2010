package sep.conquest.model.behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sep.conquest.model.AStarPathFinder;
import sep.conquest.model.PathNode;
import sep.conquest.model.Puck;
import sep.conquest.model.State;
import sep.conquest.util.Utility;


/**
 * RemovePathlessBehaviour removes frontier nodes that are not reachable by
 * the current position.
 * 
 * @author Andreas Wilhelm
 */
public final class RemovePathlessBehaviour extends Behaviour {

	/**
	 * The constructor enables chain-handling by calling the constructor of the
	 * super-class (Behaviour).
	 * 
	 * @param stateLevel
	 *            The level of the state.
	 * @param next
	 *            A reference to the next behaviour.
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
		
		if (robot.isMessageExpected())
			return false;		
		
		List<Integer> toRemove = new ArrayList<Integer>();
		for (Integer key: map.keySet()) {
			AStarPathFinder aStar = new AStarPathFinder();
			int[] start = robot.getRobotStatus().get(robot.getID()).getPosition();
			int[][] dest = {Utility.extractCoordinates(key)};
			PathNode[] paths = aStar.find(robot, start, dest);
			if (paths[0] == null)
				toRemove.add(key);
		}
		
		for (Integer key: toRemove)
			map.remove(key);
		
		return super.execute(map, robot);	
	}
}
