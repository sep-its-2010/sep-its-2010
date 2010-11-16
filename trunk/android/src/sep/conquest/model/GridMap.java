package sep.conquest.model;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * The class GridMap manages the saving of nodes which have been explored by the
 * e-puck. Nodes of the type Graphnode can be added to the structure and the
 * access is managed with a binary tree. Additionaly the class GridMap has got a
 * attribute frontierList which saves the unexplored nodes with explored
 * neighbours.
 * 
 * @author Florian Lorenz
 */
public class GridMap {
	/**
	 * Saves all instances of the class MapNode, which haven't not been explored
	 * yet, but which has got at least one visited neighbour of the type MapNode
	 */
	private LinkedList<MapNode> frontierList = new LinkedList<MapNode>();

	/**
	 * Saves all references of the instances of the class MapNode, ordered in a
	 * binary tree
	 */
	private TreeMap<Integer, MapNode> mapTree = new TreeMap<Integer, MapNode>();

	/**
	 * default constructor
	 */
	public GridMap() {

	}

	/**
	 * Creates an instance of the type MapNode, inserts it in the attribute
	 * mapTree and if necessary sets references on neighbours. With the assigned
	 * enumeration new frontiernodes (nodes which hasen't been visited yet) will
	 * be created an add to the structure.
	 */
	public void addNode(int x, int y, NodeType status) {

	}

	/**
	 * Makes a deep copy of the attribut frontierList with a private method
	 * 
	 * @return returns the deep copy of the attribut frontierList
	 */
	public LinkedList<MapNode> getFrontierList() {

	}

	/**
	 * Makes a deep copy of the attribut mapTree with a private method and saves
	 * it in a LinkedList
	 * 
	 * @return returns a LinkedList which contents all Nodes saved in the
	 *         GridMap
	 */
	public LinkedList<OtherNode> getMapAsList() {

	}

	/**
	 * Makes a deep copy of the frontierList and saves the nodes in a LinkedList
	 * 
	 * @return returns a LinkedList which is a copy of frontierList
	 */
	private LinkedList<MapNode> cloneFrontierList() {

	}

	/**
	 * Iterates trough the TreeMap mapTree and saves the nodes into a LinkedList
	 * 
	 * @return return a LinkedList which contains all nodes saved in the Gridmap
	 */
	private LinkedList<OtherNode> cloneMapTreeIntoList() {

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
	 * @param x
	 *            the x-coordinate of the node
	 * @param y
	 *            the y-coordinate of the node
	 * @param state
	 */
	private void changeState(int x, int y, Status state) {

	}

}
