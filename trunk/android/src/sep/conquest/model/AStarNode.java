package sep.conquest.model;

/**
 * An AStarNode is a node-entity which is needed for the A*-algorithm in the
 * class AStarPathFinder. Every instance has a reference to its predecessor
 * as well as an attribute for the navigation-costs.
 * 
 * @author Andreas Wilhelm
 *
 */
public class AStarNode implements Comparable<AStarNode>{
	
	/**
	 * The predecessor of the node.
	 */
	private AStarNode predecessor;
	
	/**
	 * The node, represented by a MapNode.
	 */
	private MapNode node;
	
	/**
	 * The navigation costs to the node.
	 */
	private int costs;
	
	/**
	 * The constructor expects a predecessor node, the MapNode instance and
	 * the corresponding navigation-costs.
	 * 
	 * @param predecessor The predecessor entity.
	 * @param node The MapNode instance.
	 * @param costs The navigation costs.
	 */
	public AStarNode(AStarNode predecessor, MapNode node, int costs) {
		this.setPredecessor(predecessor);
		this.node = node;
		this.setCosts(costs);
	}

	/**
	 * Sets a new predecessor.
	 * 
	 * @param predecessor The predecessor node.
	 */
	public void setPredecessor(AStarNode predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * Returns the predecessor.
	 * 
	 * @return The predecessor node.
	 */
	public AStarNode getPredecessor() {
		return predecessor;
	}

	/**
	 * Returns the MapNode entry.
	 * 
	 * @return The node.
	 */
	public MapNode getNode() {
		return node;
	}

	/**
	 * Sets a new value for the navigation-costs.
	 * 
	 * @param costs The navigation-costs.
	 */
	public void setCosts(int costs) {
		this.costs = costs;
	}

	/**
	 * Returns the navigation-costs.
	 * 
	 * @return The navigation-costs.
	 */
	public int getCosts() {
		return costs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(Object o) {
		return (getNode().hashCode() == o.hashCode());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		return getNode().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AStarNode arg0) {
		if (this.getCosts() < arg0.getCosts())
			return -1;
		else if (this.getCosts() > arg0.getCosts())
			return 1;
		else
			return 0;
	}
}
