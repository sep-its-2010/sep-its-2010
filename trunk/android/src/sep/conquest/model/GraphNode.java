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
	 * Saves the references on the direct neighbours of the node
	 */
	private MapNode[] neighbours = new MapNode[4];
	
	/**
	 * This method returns a reference on the direct neighbours of the node
	 * @return The attribute neighbours
	 */
	public MapNode[] getNeighbours(){
	 return null;
	}
}
