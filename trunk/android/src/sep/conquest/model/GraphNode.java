package sep.conquest.model;


import java.io.Serializable;

/**
 * The class GraphNode inherits of the class MapNode and saves additionally the
 * references to its neighbours in an array. This is necessary for the
 * A*-Algorithm.
 * 
 * @author Florian Lorenz
 */
public class GraphNode extends MapNode implements Serializable {
	
	/**
	 * An unique serialVersionUID to identify the class.
	 */
	private static final long serialVersionUID = 5636347010678057109L;

	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 0 represents the left neighbour.
	 */
	public final int LEFTNEIGHBOUR = 0;

	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 1 represents the right neighbour.
	 */
	public final int RIGHTNEIGHBOUR = 1;

	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 2 represents the lower neighbour.
	 */
	public final int BOTTOMNEIGHBOUR = 2;
	
	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 3 represents the upper neighbour.
	 */
	public final int TOPNEIGHBOUR = 3;

	/**
	 * Saves the references on the direct neighbours of the node in an array.
	 */
	private GraphNode[] neighbours = new GraphNode[4];

	/**
	 * Constructor creates an instance of the GraphNode and needs the
	 * coordinates and the NodeType of the new node.
	 * 
	 * @param newXValue Is the x-coordinate of the new Node.
	 * @param newYValue Is the y-coordinate of the new Node.
	 * @param newNodeState Is the NodeType of the new Node.
	 */
	public GraphNode(int newXValue, int newYValue, NodeType newNodeState) {
		super(newXValue, newYValue, newNodeState);
	}

	/**
	 * This method returns a reference on the direct neighbours of the node.
	 * 
	 * @return The attribute neighbours.
	 */
	public GraphNode[] getNeighbours() {
		return this.neighbours;
	}
	
	/**
	 * Sets the neighbours in the attribute neighbours.
	 * 
	 * @param index The Position of the neighbour
	 * @param neighbour The reference to the neighbour
	 */
	public void setNeighbours(int index, GraphNode neighbour) {
		this.neighbours[index] = neighbour;
	}
	
	/**
	 * Creates a new instance of the class GraphNode with the same attributes
	 * and returns it.
	 * 
	 * @return A deep copy of the GraphNode
	 */
	@Override
	public GraphNode clone() {
		GraphNode nodeCopy = new GraphNode(this.getXValue(),this.getYValue(), this.getNodeType());
		int i = this.getVisitCounter();
		for(int j = 0; j<i;j++){
			nodeCopy.increaseVisitCounter();
		}
		return nodeCopy;
	}
}
