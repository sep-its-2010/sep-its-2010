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
	private final static int MINXVALUEONMAP = 0;
	
	/**
	 * This constant represents the smallest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 1.
	 */
	private final static int MINYVALUEONMAP = 1;
	
	/**
	 * This constant represents the biggest x-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 2.
	 */
	private final static int MAXXVALUEONMAP = 2;
	
	/**
	 * This constant represents the biggest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 3.
	 */
	private final static int MAXYVALUEONMAP = 3;
	
	/**
	 * default constructor
	 */
	public GridMap() {
		//init the borders of the map 
		mapBorders[this.MAXYVALUEONMAP]=0;
		mapBorders[this.MAXXVALUEONMAP]=0;
		mapBorders[this.MINYVALUEONMAP]=0;
		mapBorders[this.MINXVALUEONMAP]=0;
	}

	/**
	 * Creates an instance of the type GraphNode, inserts it in the attribute
	 * mapTree and if necessary sets references on neighbours. With the assigned
	 * enumeration new frontierNodes (nodes which hasen't been visited yet) will
	 * be created and add to the structure.
	 */
	public void addNode(int x, int y, NodeType status) {
		//updates the borders of the map
		this.updateMapBorders(x, y);
		MapNode existingNode = this.getNode(x, y);
		//If the Node already exists in the structure, existingNode is not null
		if(existingNode != null){
			//updates the state of the existing node
			this.changeState(x, y, status);
		} else {
			//creates a new node
			GraphNode newNode = new GraphNode(x,y,status);
			//add to tree
			int key = this.makeKey(x,y);
			this.mapTree.put(key, newNode);
			//set neighbours
			this.setNeighbours(newNode);
			//set frontiernodes
			this.setFrontierNodes(x,y);
		}
	}

	/**
	 * Makes a deep copy of the attribute frontierList using a private method
	 * 
	 * @return The deep copy of the attribute frontierList
	 */
	public LinkedList<GraphNode> getFrontierList() {
		return null;
	}

	/**
	 * Makes a copy of the attribute mapTree using the private method 
	 * cloneMapTreeIntoList and saves the nodes of the tree as instances of the
	 * class MapNodes in a LinkedList
	 * 
	 * @return LinkedList which contains all Nodes saved in the
	 *         GridMap
	 */
	public LinkedList<MapNode> getMapAsList() {
		LinkedList<MapNode> mapNodeList = new LinkedList<MapNode>();
		return mapNodeList;
	}

	/**
	 * This method returns the smallest and biggest x- and y-coordinates of
	 * the map saved in an int-array of length 4.
	 * @return The attribute mapBorders which contains the smallest and
	 * biggest coordinates. 
	 */
	public int[] getMapBorders(){
		return this.mapBorders;
	}
	
	/**
	 * This method is called by the public method addNode and updates the 
	 * smallest or biggest coordinates if necessary.
	 * @param xCoordinate The x-coordinate of the new inserted MapNode
	 * @param yCoordinate The y-coordinate of the new inserted MapNode
	 */
	private void updateMapBorders(int xCoordinate, int yCoordinate){
		//updates the biggest x-coordinate
		if(xCoordinate > mapBorders[this.MAXXVALUEONMAP]){
			mapBorders[this.MAXXVALUEONMAP] = xCoordinate;
		}
		//updates the biggest y-coordinate
		if(yCoordinate > mapBorders[this.MAXYVALUEONMAP]){
			mapBorders[this.MAXYVALUEONMAP] = yCoordinate;
		}
		//updates the smallest x-coordinate
		if(xCoordinate < mapBorders[this.MINXVALUEONMAP]){
			mapBorders[this.MINXVALUEONMAP] = xCoordinate;
		}
		//updates the smallest y-coordinate
		if(yCoordinate < mapBorders[this.MINYVALUEONMAP]){
			mapBorders[this.MINYVALUEONMAP] = yCoordinate;
		}
	}
	
	/**
	 * Makes a deep copy of the frontierList and saves the GraphNodes in a 
	 * LinkedList
	 * 
	 * @return LinkedList which is a copy of frontierList
	 */
	private LinkedList<GraphNode> cloneFrontierList() {
		LinkedList<GraphNode> graphNodeList = new LinkedList<GraphNode>();
		return graphNodeList;
	}

	/**
	 * Iterates trough the TreeMap mapTree, creates MapNodes using the 
	 * GraphNodes and saves them into a LinkedList
	 * 
	 * @return LinkedList which contains all nodes saved in the GridMap
	 */
	private LinkedList<MapNode> cloneMapTreeIntoList() {
		return null;
	}

	/**
	 * Searches whether a node with the coordinates x and y exists in the 
	 * TreeMap mapTree
	 * 
	 * @param x The x-coordinate 
	 * @param y The y-coordinate          
	 * @return The node if it exists in the TreeMap mapTree, otherwise
	 *         returns null
	 */
	private GraphNode getNode(int x, int y) {
		int key = this.makeKey(x,y);
		return mapTree.get(key);
	}

	/**
	 * The private method setNeighbours sets the references the neighbours of
	 * the node with the coordinates x and y. If a neighbournode does not exist,
	 * the reference is set to null.
	 * 
	 * @param newNode The Node which gets new neighbours    
	 */
	private void setNeighbours(GraphNode newNode) {
		//Is the actual neighbour
		GraphNode actNeighbour;
		//Search and set the bottom neighbour
		actNeighbour = this.mapTree.get(
					this.makeKey(newNode.getXValue(),newNode.getYValue()+1));
		//If the neighbour exists set neighbours
		if(actNeighbour != null){
			newNode.setNeighbours(newNode.BOTTOMNEIGHBOUR,actNeighbour);
			actNeighbour.setNeighbours(actNeighbour.TOPNEIGHBOUR,newNode);
		}
		
		//Search and set the top neighbour
		actNeighbour = this.mapTree.get(
					this.makeKey(newNode.getXValue(),newNode.getYValue()-1));
		//If the neighbour exists set neighbours
		if(actNeighbour != null){
			newNode.setNeighbours(newNode.TOPNEIGHBOUR,actNeighbour);
			actNeighbour.setNeighbours(actNeighbour.BOTTOMNEIGHBOUR,newNode);
		}
		
		//Search and set the left neighbour
		actNeighbour = this.mapTree.get(
					this.makeKey(newNode.getXValue()-1,newNode.getYValue()));
		//If the neighbour exists set neighbours
		if(actNeighbour != null){
			newNode.setNeighbours(newNode.LEFTNEIGHBOUR,actNeighbour);
			actNeighbour.setNeighbours(actNeighbour.RIGHTNEIGHBOUR,newNode);
		}
		
		//Search and set the right neighbour
		actNeighbour = this.mapTree.get(
					this.makeKey(newNode.getXValue()+1,newNode.getYValue()));
		//If the neighbour exists set neighbours
		if(actNeighbour != null){
			newNode.setNeighbours(newNode.RIGHTNEIGHBOUR,actNeighbour);
			actNeighbour.setNeighbours(actNeighbour.LEFTNEIGHBOUR,newNode);
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int makeKey(int x, int y){
		return (x ^ y << 16) | (x & 0xFFFF);
	}

	/**
	 * The private method setFrontierNodes checks if new frontierNodes has to be
	 * created at the node with the coordinates x and y and sets the
	 * neighbourreferences
	 * 
	 * @param x The x-coordinate of the node   
	 * @param y The y-coordinate of the node         
	 */
	private void setFrontierNodes(int x, int y) {

	}

	/**
	 * changeState changes the state of a node and increments the visitCounter.
	 * The method is called when a node already exists in the structure.
	 * 
	 * @param x The x-coordinate of the node      
	 * @param y	The y-coordinate of the node        
	 * @param newNodeType The actual NodeType
	 * 				
	 */
	private void changeState(int x, int y, NodeType newNodeType) {
		this.getNode(x,y).setNodeType(newNodeType);
		this.getNode(x,y).increaseVisitCounter();
	}

}
