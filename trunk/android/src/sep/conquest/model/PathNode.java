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
	 * The successor of the node.
	 */
	private PathNode next;
	
	/**
	 * The navigation-costs of the node.
	 */
	private int costs;
	
	/**
	 * The constructor takes a MapNode, its costs according to the navigation
	 * algorithm and a predecessor node.
	 * 
	 * @param node The MapNode.
	 * @param next The next PathNode instance.
	 * @param costs The navigation-costs for this node.
	 */
	public PathNode(MapNode node, PathNode next, int costs) {
		this.node = node;
		this.next = next;
		this.costs = costs;
	}
	
	/**
	 * Returns the PathNode
	 * 
	 * @return The PathNode.
	 */
	public MapNode getNode() {
		return node;
	}
	
	/**
	 * Returns the next PathNode.
	 * 
	 * @return The next PathNode.
	 */
	public PathNode getNext() {
		return next;
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
	 * Sets a new successor PathNode.
	 * 
	 * @param prev The successor node.
	 */
	public void setNext(PathNode next) {
		this.next = next;
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
