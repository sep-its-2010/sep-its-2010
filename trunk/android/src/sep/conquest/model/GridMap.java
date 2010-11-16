package sep.conquest.model;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * The class GridMap manages the saving of nodes which have been explored by the
 * e-puck roboter. Nodes of the type GraphNode can be added to the structure and the
 * access is managed with a binary tree. Additionaly the class GridMap has got a
 * attribute frontierList which saves the unexplored nodes with explored
 * neighbours.
 * 
 * @author Florian Lorenz
 */
public class GridMap {
	/**
	 * Saves all instances of the class MapNode, which haven't been explored
	 * yet, but which has got at least one visited neighbour of the type GraphNode
	 */
	private LinkedList<GraphNode> frontierList = new LinkedList<GraphNode>();

	/**
	 * Saves all references of the instances of the class GraphNode, ordered in a
	 * binary tree.
	 */
	private TreeMap<Integer, GraphNode> mapTree = new TreeMap<Integer, GraphNode>();

	/**
	 * default constructor
	 */
	public GridMap() {

	}

	/**
	 * Creates an instance of the type GraphNode, inserts it in the attribute
	 * mapTree and if necessary sets references on neighbours. With the assigned
	 * enumeration new frontierNodes (nodes which hasen't been visited yet) will
	 * be created an add to the structure.
	 */
	public void addNode(int x, int y, NodeType status) {

	}

	/**
	 * Makes a deep copy of the attribute frontierList using a private method
	 * 
	 * @return returns the deep copy of the attribute frontierList
	 */
	public LinkedList<GraphNode> getFrontierList() {
		return null;
	}

	/**
	 * Makes a copy of the attribute mapTree using the private method cloneMapTreeIntoList
	 * and saves the nodes of the tree as instances of the class MapNodes 
	 * in a LinkedList
	 * 
	 * @return returns a LinkedList which contains all Nodes saved in the
	 *         GridMap
	 */
	public LinkedList<MapNode> getMapAsList() {
		return null;
	}

	/**
	 * Makes a deep copy of the frontierList and saves the GraphNodes in a LinkedList
	 * 
	 * @return returns a LinkedList which is a copy of frontierList
	 */
	private LinkedList<GraphNode> cloneFrontierList() {
		return null;
	}

	/**
	 * Iterates trough the TreeMap mapTree, creates MapNodes using the GraphNodes
	 * and saves them into a LinkedList
	 * 
	 * @return return a LinkedList which contains all nodes saved in the GridMap
	 */
	private LinkedList<MapNode> cloneMapTreeIntoList() {
		return null;
	}

	/**
	 * Searches whether a node with the coordinates x and y is in the TreeMap
	 * mapTree
	 * 
	 * @param x
	 *            is the x-coordinate
	 * @param y
	 *            is the y-coordinate
	 * @return returns the node if it exists in the TreeMap mapTree, otherwise
	 *         returns null
	 */
	private MapNode getNode(int x, int y) {
		return null;
	}

	/**
	 * The private method setNeighbours sets the references the neighbours of
	 * the node with the coordinates x and y. If a neighbournode does not exists
	 * the reference is set to null.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	private void setNeighbours(int x, int y) {

	}

	/**
	 * The private method setFrontierNodes checks if new frontierNodes has to be
	 * created at the node with the coordinates x and y and sets the
	 * neighbourreferences
	 * 
	 * @param x
	 *            the x-coordinate of the node
	 * @param y
	 *            the y-coordinate of the node
	 */
	private void setFrontierNodes(int x, int y) {

	}

	/**
	 * changeState changes the state of a node and increments the visitCounter.
	 * The method is called when a node already exists in the structure.
	 * 
	 * @param x the x-coordinate of the node      
	 * @param y	the y-coordinate of the node        
	 * @param newNodeType the actual NodeType
	 * 				
	 */
	private void changeState(int x, int y, NodeType newNodeType) {

	}

}
