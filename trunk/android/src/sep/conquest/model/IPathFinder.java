package sep.conquest.model;

/**
 * The interface has to be implemented in order to realize a shortest-path
 * algorithm.
 * 
 * @author Andreas Wilhelm
 *
 */
public interface IPathFinder {
	
	/**
	 * The find-method searches for the shortest paths from a specific start
	 * -node to a number of many other nodes.
	 * 
	 * @param start The start-node.
	 * @param destinations The destination-nodes.
	 * @return A list of paths to the destination-nodes.
	 */
	PathNode[] find(MapNode start, MapNode[] destinations);

	/**
	 * The method calculates the navigation-costs from a specific node to an
	 * adjacent neighbor.
	 * 
	 * @param from The starting node.
	 * @param to The destination node.
	 * @return The navigation-costs.
	 */
	int caluclateCosts(MapNode from, MapNode to);
	
}
