package sep.conquest.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * The class GridMap manages the saving of nodes which have been explored by the
 * e-puck roboter. Nodes of the type GraphNode can be added to the structure and 
 * the access is managed with a binary tree. Additionally the class GridMap has
 * got a attribute frontierList which saves the unexplored nodes with explored
 * neighbours. GridMap implements the interface Serializable to serialize the 
 * data structure.
 * 
 * @author Florian Lorenz
 */
public class GridMap implements Serializable{
	
	/**
	 * An unique serialVersionUID to identify the class
	 */
	private static final long serialVersionUID = -7600637766534768588L;

	/**
	 * Saves all instances of the class MapNode, which haven't been explored
	 * yet, but which has got at least one visited neighbour of the type 
	 * GraphNode
	 */
	private LinkedList<GraphNode> frontierList = new LinkedList<GraphNode>();

	/**
	 * Saves all references of the instances of the class GraphNode, ordered in 
	 * a binary tree.
	 */
	private TreeMap<Integer, GraphNode> mapTree = new TreeMap<Integer, 
																GraphNode>();
	
	/**
	 * Saves the smallest and biggest x- and y-coordinates of the MapNodes in 
	 * the class GridMap. 
	 */
	private int[] mapBorders = new int[4];
	
	/**
	 * This constant represents the smallest x-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 0.
	 */
	private final static int minXValueOnMap = 0;
	
	/**
	 * This constant represents the smallest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 1.
	 */
	private final static int minYValueOnMap = 1;
	
	/**
	 * This constant represents the biggest x-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 2.
	 */
	private final static int maxXValueOnMap = 2;
	
	/**
	 * This constant represents the biggest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 3.
	 */
	private final static int maxYValueOnMap = 3;
	
	/**
	 * default constructor
	 */
	public GridMap() {
		
	}

	/**
	 * Creates an instance of the type GraphNode, inserts it in the attribute
	 * mapTree and if necessary sets references on neighbours. With the assigned
	 * enumeration new frontierNodes (nodes which hasen't been visited yet) will
	 * be created and add to the structure.
	 */
	public void addNode(int x, int y, NodeType status) {

	}

	/**
	 * Makes a deep copy of the attribute frontierList using a private method
	 * 
	 * @return Returns the deep copy of the attribute frontierList
	 */
	public LinkedList<GraphNode> getFrontierList() {
		return null;
	}

	/**
	 * Makes a copy of the attribute mapTree using the private method 
	 * cloneMapTreeIntoList and saves the nodes of the tree as instances of the
	 * class MapNodes in a LinkedList
	 * 
	 * @return Returns a LinkedList which contains all Nodes saved in the
	 *         GridMap
	 */
	public LinkedList<MapNode> getMapAsList() {
		LinkedList<MapNode> mapNodeList = new LinkedList<MapNode>();
		return mapNodeList;
	}

	/**
	 * This method returns the smallest and biggest x- and y-coordinates of
	 * the map saved in an int-array of length 4.
	 * @return Returns the attribute mapBorders which contains the smallest and
	 * biggest coordinates. 
	 */
	public int[] getMapBorders(){
		return this.mapBorders;
	}
	
	/**
	 * This method is called by the public method addNode and updates the 
	 * smallest or biggest coordinates if necessary.
	 * @param xCoordinate Is the x-coordinate of the new inserted MapNode
	 * @param yCoordinate Is the y-coordinate of the new inserted MapNode
	 */
	private void updateMapBorders(int xCoordinate, int yCoordinate){
		
	}
	
	/**
	 * Makes a deep copy of the frontierList and saves the GraphNodes in a 
	 * LinkedList
	 * 
	 * @return Returns a LinkedList which is a copy of frontierList
	 */
	private LinkedList<GraphNode> cloneFrontierList() {
		LinkedList<GraphNode> graphNodeList = new LinkedList<GraphNode>();
		return graphNodeList;
	}

	/**
	 * Iterates trough the TreeMap mapTree, creates MapNodes using the 
	 * GraphNodes and saves them into a LinkedList
	 * 
	 * @return Return a LinkedList which contains all nodes saved in the GridMap
	 */
	private LinkedList<MapNode> cloneMapTreeIntoList() {
		return null;
	}

	/**
	 * Searches whether a node with the coordinates x and y exists in the 
	 * TreeMap mapTree
	 * 
	 * @param x Is the x-coordinate 
	 * @param y Is the y-coordinate          
	 * @return Returns the node if it exists in the TreeMap mapTree, otherwise
	 *         returns null
	 */
	private MapNode getNode(int x, int y) {
		return null;
	}

	/**
	 * The private method setNeighbours sets the references the neighbours of
	 * the node with the coordinates x and y. If a neighbournode does not exist,
	 * the reference is set to null.
	 * 
	 * @param x Is the x-coordinate     
	 * @param y Is the y-coordinate     
	 */
	private void setNeighbours(int x, int y) {

	}

	/**
	 * The private method setFrontierNodes checks if new frontierNodes has to be
	 * created at the node with the coordinates x and y and sets the
	 * neighbourreferences
	 * 
	 * @param x Is the x-coordinate of the node   
	 * @param y Is the y-coordinate of the node         
	 */
	private void setFrontierNodes(int x, int y) {

	}

	/**
	 * changeState changes the state of a node and increments the visitCounter.
	 * The method is called when a node already exists in the structure.
	 * 
	 * @param x Is the x-coordinate of the node      
	 * @param y	Is the y-coordinate of the node        
	 * @param newNodeType Is the actual NodeType
	 * 				
	 */
	private void changeState(int x, int y, NodeType newNodeType) {

	}

}
