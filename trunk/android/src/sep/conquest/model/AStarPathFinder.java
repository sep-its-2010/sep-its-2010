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

		PathNode[] paths = new PathNode[destinations.length];
		boolean pathFound;

		// find paths for every destination
		for (int i = 0; i < destinations.length; i++) {
			initialize(robot, start, destinations[i]);
			pathFound = false;
			while (!openList.isEmpty() && !pathFound) {
				AStarNode current = openList.poll();
				if (current.equals(destinations[i])) {
					closeList.add(current);
					paths[i] = getPath(current);
					pathFound = true;
				} else {
					expandNode(robot, current, destinations[i]);
					closeList.add(current);
				}
			}
		}
		return paths;
	}
	
	/**
	 * Initializes a new search for a specific start-destination path.
	 * 
	 * @param robot The robot.
	 * @param start The start-node.
	 * @param destination The destination-node.
	 */
	private void initialize(Puck robot, MapNode start, MapNode destination) {
		if (closeList.isEmpty()) {
			AStarNode first = new AStarNode(null, start, 0);
			openList.add(first);
		} else {
			if (closeList.contains(destination))
				for (AStarNode node: closeList) {
					if (node.equals(destination))
						openList.add(node);
				}		
			for (AStarNode node: closeList)
				node.setEstimatedCosts(estimateCosts(node.getNode(), destination));
			for (AStarNode node: openList)
				node.setEstimatedCosts(estimateCosts(node.getNode(), destination));
		}
	}

	/**
	 * Returns the path to a given destination.
	 * 
	 * @param destination
	 *            The destination.
	 * @return The path.
	 */
	private PathNode getPath(AStarNode destination) {

		AStarNode current = destination;
		PathNode path = null;
		PathNode next = null;

		while (current != null) {
			path = new PathNode(current.getNode(), next, current.getCosts());
			next = path;
			current = current.getPredecessor();
		}
		return path;
	}

	/**
	 * The find-method searches for the shortest paths from a specific start
	 * -node to a number of many other nodes. This version uses x- and y-
	 * positions for the nodes.
	 * 
	 * @param robot
	 *            The robot.
	 * @param start
	 *            The start-node.
	 * @param destinations
	 *            The destination-nodes.
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
		GraphNode tmp = (GraphNode) currentNode.getNode();
		/*neighbours[Orientation.RIGHT.ordinal()] = map.getNode(currentNode
				.getNode().getXValue() + 1, currentNode.getNode().getYValue());
		neighbours[Orientation.LEFT.ordinal()] = map.getNode(currentNode
				.getNode().getXValue() - 1, currentNode.getNode().getYValue());
		neighbours[Orientation.UP.ordinal()] = map.getNode(currentNode.getNode()
				.getXValue(), currentNode.getNode().getYValue() + 1);
		neighbours[Orientation.DOWN.ordinal()] = map.getNode(currentNode
				.getNode().getXValue(), currentNode.getNode().getYValue() - 1);
		*/
		neighbours[0] = tmp.getNeighbours()[3];
		neighbours[1] = tmp.getNeighbours()[0];
		neighbours[2] = tmp.getNeighbours()[2];
		neighbours[3] = tmp.getNeighbours()[1];
		return  neighbours;
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
	private void expandNode(Puck robot, AStarNode currentNode, MapNode dest) {

		GraphNode[] neighbors = getNeighbors(robot.getMap(), currentNode);

		for (GraphNode neighbor : neighbors) {
			if (neighbor != null) {
				if (!closeList.contains(neighbor)) {
					int costs = currentNode.getCosts()
							+ calculateNodeCosts(robot, neighbor);

					// if a better path already exists => continue
					if (openList.contains(neighbor)) {
						for (AStarNode openNode : openList) {
							if (openNode.equals(neighbor))
								if (costs < openNode.getCosts())
									openNode.setCosts(costs);
						}
					} else {
						AStarNode tmp = new AStarNode(currentNode, neighbor,
								costs);
						tmp.setEstimatedCosts(estimateCosts(tmp.getNode(),
										dest));
						openList.add(tmp);
					}
				}
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

	/**
	 * Estimates the driving costs between to nodes by calculating the step
	 * distance.
	 * 
	 * @param node
	 *            The start-node.
	 * @param target
	 *            The target-node.
	 * @return The calculated costs.
	 */
	private int estimateCosts(MapNode node, MapNode target) {
		int x = Math.abs(node.getXValue() - target.getXValue());
		int y = Math.abs(node.getYValue() - target.getYValue());
		return (x + y) * 10;
	}
}
