package sep.conquest.model;

/**
 * This class represents a node within the A*-algorithm for determining the
 * shortest path between two nodes. It contains the MapNode attribute, as well
 * as a reference to the previous node and the total costs.
 * 
 * @author Andreas Wilhelm
 *
 */
public class PathNode {
	
	/**
	 * The internal representation of the PathNode entity.
	 */
	private MapNode node;
	
	/**
	 * The predecessor of the node.
	 */
	private PathNode prev;
	
	/**
	 * The navigation-costs of the node.
	 */
	private int costs;
	
	/**
	 * The constructor takes a MapNode, its costs according to the navigation
	 * algorithm and a predecessor node.
	 * 
	 * @param node The MapNode.
	 * @param prev The previous PathNode instance.
	 * @param costs The navigation-costs for this node.
	 */
	public PathNode(MapNode node, PathNode prev, int costs) {
		this.node = node;
		this.prev = prev;
		this.costs = costs;
	}
	
	/**
	 * Returns the PathNode
	 * 
	 * @return The PathNode.
	 */
	public Object getNode() {
		return node;
	}
	
	/**
	 * Returns the previous PathNode.
	 * 
	 * @return The previous PathNode.
	 */
	public PathNode getPrev() {
		return prev;
	}
	
	/**
	 * Returns the navigation-costs of the PathNode.
	 * 
	 * @return The costs.
	 */
	public int getCosts() {
		return costs;
	}
	
	/**
	 * Sets a new predecessor PathNode.
	 * 
	 * @param prev The predecessor node.
	 */
	public void setPrev(PathNode prev) {
		this.prev = prev;
	}
	
	/**
	 * Sets the navigation-costs for the node.
	 * 
	 * @param costs The navigation-costs.
	 */
	public void setCosts(int costs) {
		this.costs = costs;
	}

}
