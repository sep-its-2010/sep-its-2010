package sep.conquest.model;

/**
 * The class GraphNode inherits of the class MapNode and saves additionally
 * the references to its neighbours. This is necessary for the A*-Algorithm
 * 
 * @author Florian Lorenz
 */
public class GraphNode extends MapNode {
	
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
