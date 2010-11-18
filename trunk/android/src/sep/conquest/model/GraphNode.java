package sep.conquest.model;

/**
 * The class GraphNode inherits of the class MapNode and saves additionally
 * the references to its neighbours in an array. This is necessary for the 
 * A*-Algorithm
 * 
 * @author Florian Lorenz
 */
public class GraphNode extends MapNode {
	
	/**
	 * An unique serialVersionUID to identify the class
	 */
	private static final long serialVersionUID = 5636347010678057109L;

	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 0 represents the left neighbour
	 */
	public final static int LEFTNEIGHBOUR = 0;
	
	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 1 represents the right neighbour
	 */
	public final static int RIGHTNEIGHBOUR = 1;
	
	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 2 represents the lower neighbour
	 */
	public final static int BOTTOMNEIGHBOUR = 2;
	
	/**
	 * This constant exists for the access to the attribute neighbours. Position
	 * 3 represents the upper neighbour
	 */
	public final static int TOPNEIGHBOUR = 3;
	
	/**
	 * Saves the references on the direct neighbours of the node in an array
	 */
	private MapNode[] neighbours = new MapNode[4];
	
	/**
	 * Constructor creates an instance of the GraphNode and needs the coordinates
	 * and the NodeType of the new node.
	 * @param newXValue Is the x-coordinate of the new Node
	 * @param newYValue Is the y-coordinate of the new Node
	 * @param newNodeState Is the NodeType of the new Node
	 */
	public GraphNode(int newXValue, int newYValue, NodeType newNodeState){
		super(newXValue,newYValue,newNodeState);
	}
	
	/**
	 * This method returns a reference on the direct neighbours of the node
	 * @return Returns the attribute neighbours
	 */
	public MapNode[] getNeighbours(){
	 return this.neighbours;
	}
}
