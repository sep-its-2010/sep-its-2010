package sep.conquest.model;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

/**
 * The class realizes an A*-algorithm for finding the shortest path between
 * nodes.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class AStarPathFinder implements IPathFinder {

	/**
	 * The list of known, but not yet calculated nodes.
	 */
	private PriorityQueue<AStarNode> openList = new PriorityQueue<AStarNode>();

	/**
	 * The list of calculated nodes.
	 */
	private PriorityQueue<AStarNode> closeList = new PriorityQueue<AStarNode>();

	/**
	 * The constructor.
	 */
	public AStarPathFinder() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IPathFinder#caluclateCosts(java.lang.Object,
	 * java.lang.Object)
	 */
	public int caluclateCosts(Puck robot, MapNode from, MapNode to) {

		MapNode[] dest = { to };
		PathNode[] path = find(robot, from, dest);
		if (path != null) {
			return path.length;
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IPathFinder#find(java.lang.Object,
	 * java.lang.Object[])
	 */
	public PathNode[] find(Puck robot, MapNode start, MapNode[] destinations) {

		// add first node
		AStarNode first = new AStarNode(null, start, 0);
		openList.add(first);

		while (!openList.isEmpty()) {
			AStarNode current = openList.poll();
			if (current.equals(start)) {
				closeList.add(current);
				PathNode[] pathNodes = (PathNode[]) closeList.toArray();
				return pathNodes;
			}
			expandNode(robot, current);
			closeList.add(current);
		}

		return null;
	}

	/**
	 * The find-method searches for the shortest paths from a specific start
	 * -node to a number of many other nodes. This version uses x- and y-
	 * positions for the nodes.
	 * 
	 * @param robot The robot.
	 * @param start The start-node.
	 * @param destinations The destination-nodes.
	 * @return A list of paths to the destination-nodes.
	 */
	public PathNode[] find(Puck robot, int[] start, int[][] destinations) {

		MapNode startNode = robot.getMap().getNode(start[0], start[1]);
		assert startNode != null;
		
		MapNode[] destNodes = new MapNode[destinations.length];

		for (int i = 0; i < destinations.length; i++) {
			destNodes[i] = robot.getMap().getNode(destinations[i][0],
					destinations[i][1]);
			assert destNodes[i] != null;
		}

		return find(robot, startNode, destNodes);
	}

	/**
	 * Returns the neighbors of a node.
	 * 
	 * @param map
	 *            The map of the nodes.
	 * @param currentNode
	 *            The current node.
	 * @return
	 */
	private GraphNode[] getNeighbors(GridMap map, AStarNode currentNode) {

		GraphNode[] neighbours = new GraphNode[4];

		neighbours[Orientation.RIGHT.getNum()] = map.getNode(currentNode
				.getNode().getXValue() + 1, currentNode.getNode().getYValue());
		neighbours[Orientation.LEFT.getNum()] = map.getNode(currentNode
				.getNode().getXValue() - 1, currentNode.getNode().getYValue());
		neighbours[Orientation.UP.getNum()] = map.getNode(currentNode.getNode()
				.getXValue(), currentNode.getNode().getYValue() + 1);
		neighbours[Orientation.DOWN.getNum()] = map.getNode(currentNode
				.getNode().getXValue(), currentNode.getNode().getYValue() - 1);

		return neighbours;
	}

	/**
	 * Expand the current node and (in case of a new node) updates the open list
	 * of nodes.
	 * 
	 * @param robot
	 *            The robot.
	 * @param currentNode
	 *            The current node.
	 */
	private void expandNode(Puck robot, AStarNode currentNode) {

		GraphNode[] neighbors = getNeighbors(robot.getMap(), currentNode);

		for (GraphNode neighbor : neighbors) {
			if (neighbor != null) {
				if (closeList.contains(neighbor))
					continue;

				int costs = currentNode.getCosts()
						+ calculateNodeCosts(robot, neighbor);

				// if a better path already exists => continue
				if (openList.contains(neighbor)) {
					for (AStarNode openNode : openList) {
						if (openNode.equals(neighbor)) {
							if (costs >= openNode.getCosts())
								continue;
							else {
								openNode.setCosts(costs);
							}
						}
					}
				} else
					openList.add(new AStarNode(currentNode, neighbor, costs));
			}
		}
	}

	/**
	 * The method calculates costs for a specific node.
	 * 
	 * @param robot
	 *            The robot.
	 * @param node
	 *            The node.
	 * @return
	 */
	private int calculateNodeCosts(Puck robot, GraphNode node) {

		int costs = 10; // add 10 for distance-driving-costs

		// add additionally costs due to obstacles (other robots)
		Set<UUID> keys = robot.getRobotStatus().keySet();
		for (UUID key : keys) {
			if (!key.equals(robot.getID())) {
				RobotStatus status = robot.getRobotStatus().get(key);
				if (status.getPosition()[0] == node.getXValue()
						&& status.getPosition()[1] == node.getYValue())
					costs += 50;
			}
		}

		return costs;
	}
}
