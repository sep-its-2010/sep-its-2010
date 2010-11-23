package sep.conquest.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import sep.conquest.util.ConquestLog;

/**
 * The LogicThread class will be used by Puck objects for navigation decisions.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class LogicThread implements Runnable {

	/**
	 * A instance of the AStarPathFinder class for shortest-path-finding.
	 */
	private AStarPathFinder aStar = AStarPathFinder.getInstance();

	/**
	 * A reference to the robot.
	 */
	private Puck robot;

	/**
	 * The current behaviour.
	 */
	private IBehaviour stateBehaviour;

	/**
	 * The constructor of LogicThread assigns the robot as well as its status to
	 * attributes.
	 * 
	 * @param robot
	 */
	public LogicThread(Puck robot) {
		this.robot = robot;
		stateBehaviour = Behaviour
				.getFirstBehaviour(getRobotState().getState());
	}

	/**
	 * Change the behaviour due to the state change.
	 * 
	 * @param state
	 */
	public void changeBehaviour(State state) {
		if (state != getRobotState().getState()) {
			stateBehaviour = Behaviour.getFirstBehaviour(state);
			ConquestLog.addMessage(this, "Behaviour changed to "
					+ stateBehaviour.getClass().toString());
		}
	}

	/**
	 * Returns the current state of the robot.
	 * 
	 * @return The state.
	 */
	private RobotStatus getRobotState() {
		return robot.getRobotStatus().get(robot.getID());
	}

	/**
	 * Initialize a map with the map-information from the robot.
	 * 
	 * @return The initialized map.
	 */
	private Map<int[], Integer> initMap() {
		Map<int[], Integer> map = new TreeMap<int[], Integer>();
		LinkedList<MapNode> mapList = robot.getMap().getMapAsList();
		Iterator<MapNode> it = mapList.iterator();
		while (it.hasNext()) {
			MapNode node = it.next();
			int[] pos = { node.getXValue(), node.getYValue() };
			map.put(pos, 0);
		}
		return map;
	}

	/**
	 * Returns a map-node which has the most beneficial navigation costs.
	 * 
	 * @param map
	 *            The map.
	 * @return The best node.
	 */
	private int[] getBestNode(Map<int[], Integer> map) {
		int bestValue = 0;
		int[] bestNode = { 0, 0 };
		Set<int[]> nodes = map.keySet();
		Iterator<int[]> it = nodes.iterator();
		while (it.hasNext()) {
			int[] node = it.next();
			Integer cost = map.get(node);
			if (bestValue == 0 || cost < bestValue) {
				bestValue = cost;
				bestNode = node;
			}
		}
		return bestNode;
	}

	/**
	 * Drive to a specific node.
	 * 
	 * @param intentNode
	 *            The node.
	 */
	private void driveTo(int[] intentNode) {
		int[][] dest = { intentNode };
		PathNode[] path = aStar.find(getRobotState().getPosition(), dest);

		// makes driving sense?
		if (path.length >= 1) {
			for (int i = 0; i < path.length; i++) {
				MapNode mapNode = (MapNode) path[i].getNode();
				int[] node = { mapNode.getXValue(), mapNode.getYValue() };

				// position on driving-path found => send next command
				if (node.equals(intentNode)) {
					MapNode nextMapNode = (MapNode) path[i + 1].getNode();
					int[] nextNode = { nextMapNode.getXValue(),
							nextMapNode.getYValue() };
					sendCommand(node, nextNode);
				}
			}
		}
	}

	/**
	 * The method takes a start- and a end-node to calculate a corresponding
	 * navigation command to reach the end-node.
	 * 
	 * @param startNode
	 *            The start-node.
	 * @param endNode
	 *            The end-node.
	 */
	private void sendCommand(int[] startNode, int[] endNode) {
		Orientation direction = Orientation.UNKNOWN;

		if (startNode[0] < endNode[0])
			direction = Orientation.RIGHT;
		else if (startNode[0] > endNode[0])
			direction = Orientation.LEFT;
		else if (startNode[1] < endNode[1])
			direction = Orientation.UP;
		else if (startNode[1] > endNode[1])
			direction = Orientation.DOWN;

		Orientation command = Orientation.addDirection(getRobotState()
				.getOrientation(), direction);
		
		robot.driveCommand(command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			if (robot.hasChanged()) {
				Map<int[], Integer> map = initMap();
				map = stateBehaviour.execute(map, robot);
				driveTo(getBestNode(map));
			} // if hasChanged
		} // while (true)
	}

}
