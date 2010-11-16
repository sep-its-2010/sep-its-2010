package sep.conquest.model;

/**
 * The class MapNode represents a node on the pitch. It saves the coordinates, the type
 * of the node and how often an e-puck roboter visited the node.
 * 
 * @author Florian Lorenz
 */
public class MapNode {
	
	/**
	 * Saves the x-coordinate of the node
	 */
	private int xValue;
	
	/**
	 * Saves the y-coordinate of the node
	 */
	private int yValue;
	
	/**
	 * Saves how often an e-puck Roboter visited the node
	 */
	private int visitCounter;
	
	/**
	 * Saves the type of the node, e.g. cross, unvisited..
	 */
	private NodeType nodeState;
	
	/**
	 * default constructor
	 */
	public MapNode(){
		
	}
	
	/**
	 * This method returns the x-coordinate of the node
	 * @return returns the attribute xValue
	 */
	public int getXValue(){
		
	}
	
	/**
	 * This method returns the y-coordinate of the node
	 * @return returns the attribute yValue
	 */
	public int getYValue(){
		
	}
	
	/**
	 * This method returns the type of the node
	 * @return returns the attribute nodeType
	 */
	public NodeType getNodeType(){
		
	}
	
	/**
	 * This method returns how often an e-puck visited the node
	 * @return returns the attribute  
	 */
	public int getVisitCounter(){
		
	}
	
	/**
	 * This method changes the x-coordinate of the node
	 * @param newXValue is the new x-coordinate
	 */
	public void setXValue(int newXValue){
		
	}
	
	/**
	 * This method changes the y-coordinate of the node
	 * @param newYValue is the new y-coordinate
	 */
	public void setYValue(int newYValue){
		
	}
	
	/**
	 * This method changes the type of the node
	 * @param newNodeType is the new nodeType
	 */
	public void setNodeType(NodeType newNodeType){
		
	}
	
	/**
	 * This method is called when an e-puck drives over the node. It increases the
	 * attribut visitCounter
	 */
	public void increaseVisitCounter(){
		
	}
}
