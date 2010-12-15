package sep.conquest.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

import sep.conquest.util.Utility;


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
public class GridMap{
	
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
	private final int MINXVALUEONMAP = 0;
	
	/**
	 * This constant represents the smallest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 1.
	 */
	private final int MINYVALUEONMAP = 1;

	/**
	 * This constant represents the biggest x-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 2.
	 */
	private final int MAXXVALUEONMAP = 2;
	
	/**
	 * This constant represents the biggest y-coordinate of all MapNodes saved
	 * in the attribute mapBorder on position 3.
	 */
	private final int MAXYVALUEONMAP = 3;

	/**
	 * default constructor
	 */
	public GridMap() {
		// initialize the borders of the map
		mapBorders[this.MAXYVALUEONMAP] = Integer.MIN_VALUE;
		mapBorders[this.MAXXVALUEONMAP] = Integer.MIN_VALUE;
		mapBorders[this.MINYVALUEONMAP] = Integer.MAX_VALUE;
		mapBorders[this.MINXVALUEONMAP] = Integer.MAX_VALUE;
	}
	
	/**
	 * toString-Method for debugging. Output: Xvalue, YValue, NodeType
	 */
	public void toStringer() {

		System.out.println("Size of map: " + mapTree.size());

		Collection<GraphNode> it = mapTree.values();
		Iterator<GraphNode> ite = it.iterator();
		while (ite.hasNext()) {
			GraphNode bufferNode = (GraphNode) ite.next();
			System.out.println("X-Value: " + bufferNode.getXValue());
			System.out.println("Y-Value: " + bufferNode.getYValue());
			System.out.println("Node-Type: " + bufferNode.getNodeType());
		}
	}
	
	
	
	
	/**
	 * Creates an instance of the type GraphNode, inserts it in the attribute
	 * mapTree and if necessary sets references on neighbours. With the assigned
	 * enumeration new frontierNodes (nodes which hasen't been visited yet) will
	 * be created and add to the structure.
	 */
	public void addNode(int x, int y, NodeType status) {
		// updates the borders of the map
		this.updateMapBorders(x, y);
		GraphNode existingNode = this.getNode(x, y);
		// If the Node already exists in the structure, existingNode is not null
		if (existingNode != null) {
			// updates the state of the existing node
			this.changeState(x, y, status);
			this.setNeighbours(existingNode);
		} else {
			// creates a new node
			GraphNode newNode = new GraphNode(x, y, status);
			// add to tree
			int key = Utility.makeKey(x, y);
			this.mapTree.put(key, newNode);
			// update the state to increase the visitCounter
			this.changeState(x, y, status);
			// set neighbours and especially the frontiernodes in this private
			// method
			this.setNeighbours(newNode);
		}
	}

	/**
	 * Makes a deep copy of the attribute frontierList using a private method
	 * 
	 * @return The deep copy of the attribute frontierList
	 */
	public LinkedList<GraphNode> getFrontierList() {
		LinkedList<GraphNode> graphNodeList = new LinkedList<GraphNode>();
		// Iterator for the attribute frontierList
		Iterator<GraphNode> it = frontierList.iterator();
		GraphNode bufferNode;
		while (it.hasNext()) {
			// makes a copy of the GraphNode and saves it into the LinkedList
			bufferNode = it.next().clone();
			graphNodeList.add(bufferNode);
		}
		return graphNodeList;
	}

	/**
	 * Makes a copy of the attribute mapTree using the private method
	 * cloneMapTreeIntoList and saves the nodes of the tree as instances of the
	 * class MapNodes in a LinkedList
	 * 
	 * @return LinkedList which contains all Nodes saved in the GridMap
	 */
	public LinkedList<MapNode> getMapAsList() {
		// Iterator for the TreeMap mapTree
		Iterator<Entry<Integer, GraphNode>> it = mapTree.entrySet().iterator();
		LinkedList<MapNode> nodeList = new LinkedList<MapNode>();
		MapNode bufferNode;
		while (it.hasNext()) {
			// copies the GraphNode as an instance of MapNode and saves it in
			// the LinkedList if they aren't a frontierNode
			bufferNode = it.next().getValue().clone();
			if (bufferNode.getNodeType() != NodeType.FRONTIERNODE) {
				nodeList.add(bufferNode);
			}
		}
		return nodeList;
	}

	/**
	 * Iterates trough the given list and adds the single nodes into a GridMap
	 * 
	 * @param nodeList The list with all Nodes in the Graph
	 * @return The new GridMap
	 */
	public GridMap makeMapWithList(LinkedList<MapNode> nodeList){
		GridMap newMap = new GridMap();
		Iterator<MapNode> it = nodeList.iterator();
		while(it.hasNext()){
			MapNode newNode = it.next();
			newMap.addNode(newNode.getXValue(), newNode.getYValue(), newNode.getNodeType());
		}
		return newMap;	
	}
	
	/**
	 * This method returns the smallest and biggest x- and y-coordinates of the
	 * map saved in an Integer-array of length 4.
	 * 
	 * @return The attribute mapBorders which contains the smallest and biggest
	 *         coordinates.
	 *         Position 0: MinXValue
	 *         Position 1: MinYValue
	 *         Position 2: MaxXValue
	 *         Position 3: MaxYValue
	 */
	public int[] getMapBorders() {
		int[] mapBordersToSend = new int[4];
		for (int i = 0; i < 4; i++) {
			mapBordersToSend[i] = this.mapBorders[i];
		}
		return mapBordersToSend;
	}

	/**
	 * This method is called by the public method addNode and updates the
	 * smallest or biggest coordinates if necessary.
	 * 
	 * @param xCoordinate The x-coordinate of the new inserted MapNode
	 * @param yCoordinate The y-coordinate of the new inserted MapNode
	 */
	private void updateMapBorders(int xCoordinate, int yCoordinate) {
		// updates the biggest x-coordinate
		if (xCoordinate > mapBorders[this.MAXXVALUEONMAP]) {
			mapBorders[this.MAXXVALUEONMAP] = xCoordinate;
		}
		// updates the biggest y-coordinate
		if (yCoordinate > mapBorders[this.MAXYVALUEONMAP]) {
			mapBorders[this.MAXYVALUEONMAP] = yCoordinate;
		}
		// updates the smallest x-coordinate
		if (xCoordinate < mapBorders[this.MINXVALUEONMAP]) {
			mapBorders[this.MINXVALUEONMAP] = xCoordinate;
		}
		// updates the smallest y-coordinate
		if (yCoordinate < mapBorders[this.MINYVALUEONMAP]) {
			mapBorders[this.MINYVALUEONMAP] = yCoordinate;
		}
	}

	/**
	 * Searches whether a node with the coordinates x and y exists in the
	 * TreeMap mapTree.
	 * 
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @return The node if it exists in the TreeMap mapTree, otherwise returns
	 *         null
	 */
	public GraphNode getNode(int x, int y) {
		int key = Utility.makeKey(x, y);
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
		NodeType type = newNode.getNodeType();
		switch (type) {
		// There has to be set the right and upper neighbour of the node
		case TOPRIGHTEDGE:
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		// There has to be set the left and upper neighbour of the node
		case TOPLEFTEDGE:
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		// There has to be set the right and lower neighbour of the node
		case BOTTOMRIGHTEDGE:
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			break;
		// There has to be set the left and lower neighbour of the node
		case BOTTOMLEFTEDGE:
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			break;
		// There has to be set the left and right and upper neighbour of the
		// node
		case TOPT:
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		// There has to be set the right and lower and upper neighbour of the
		// node
		case RIGHTT:
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		// There has to be set the left and lower and upper neighbour of the
		// node
		case LEFTT:
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		// There has to be set the left and lower and right neighbour of the
		// node
		case BOTTOMT:
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			break;
		// There has to be set the left and lower and right and upper
		// neighbour of the node
		case CROSS:
			this.searchAndsetNeighbour(newNode.BOTTOMNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.LEFTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.RIGHTNEIGHBOUR, newNode);
			this.searchAndsetNeighbour(newNode.TOPNEIGHBOUR, newNode);
			break;
		case FRONTIERNODE:
			// If four neighbours exists the frontierNode isn't a frontierNode
			// anymore
			break;
		}
	}
	
	/**
	 * This method searches the neighbour of the GraphNode and sets it, if it
	 * exists
	 * 
	 * @param direction The orientation of the neighbour
	 * @param newNode The node with the possible neighbours
	 */
	private void searchAndsetNeighbour(int direction, GraphNode newNode) {
		// Helping variable to save the instance of the actual neighbour node
		GraphNode actNeighbour;
		switch (direction) {
		// Search and set the left neighbour
		case 0:
			actNeighbour = this.mapTree.get(Utility.makeKey(
					newNode.getXValue() - 1, newNode.getYValue()));
			// If the neighbour exists set neighbours
			if (actNeighbour != null) {
				newNode.setNeighbours(newNode.LEFTNEIGHBOUR, actNeighbour);
				actNeighbour
						.setNeighbours(actNeighbour.RIGHTNEIGHBOUR, newNode);
			} else {
				// A new frontierNode has to be created
				this.setFrontierNodes(direction, newNode);
			}
			break;
		// Search and set the right neighbour
		case 1:
			actNeighbour = this.mapTree.get(Utility.makeKey(
					newNode.getXValue() + 1, newNode.getYValue()));
			// If the neighbour exists set neighbours
			if (actNeighbour != null) {
				newNode.setNeighbours(newNode.RIGHTNEIGHBOUR, actNeighbour);
				actNeighbour.setNeighbours(actNeighbour.LEFTNEIGHBOUR, newNode);
			} else {
				// A new frontierNode has to be created
				this.setFrontierNodes(direction, newNode);
			}
			break;
		// Search and set the bottom neighbour
		case 2:
			actNeighbour = this.mapTree.get(Utility.makeKey(newNode.getXValue(),
					newNode.getYValue() + 1));
			// If the neighbour exists set neighbours
			if (actNeighbour != null) {
				newNode.setNeighbours(newNode.BOTTOMNEIGHBOUR, actNeighbour);
				actNeighbour.setNeighbours(actNeighbour.TOPNEIGHBOUR, newNode);
			} else {
				// A new frontierNode has to be created
				this.setFrontierNodes(direction, newNode);
			}
			break;
		// Search and set the upper neighbour
		case 3:
			actNeighbour = this.mapTree.get(Utility.makeKey(newNode.getXValue(),
					newNode.getYValue() - 1));
			// If the neighbour exists set neighbours
			if (actNeighbour != null) {
				newNode.setNeighbours(newNode.TOPNEIGHBOUR, actNeighbour);
				actNeighbour.setNeighbours(actNeighbour.BOTTOMNEIGHBOUR,
						newNode);
			} else {
				// A new frontierNode has to be created
				this.setFrontierNodes(direction, newNode);
			}
			break;
		}
	}
	
	/**
	 * The private method setFrontierNodes checks if new frontierNodes has to be
	 * created at the node with the coordinates x and y and sets the
	 * neighbourreferences.
	 * 
	 * @param x The x-coordinate of the node   
	 * @param y The y-coordinate of the node         
	 */
	private void setFrontierNodes(int direction, GraphNode newNode) {
		NodeType type = NodeType.FRONTIERNODE;
		GraphNode newFrontierNode = null;

		int key;
		switch (direction) {
		// left neighbour
		case 0:
			// Create new frontierNode
			newFrontierNode = new GraphNode(newNode.getXValue() - 1, newNode
					.getYValue(), type);
			// Set new neighbourhood
			newNode.setNeighbours(newNode.LEFTNEIGHBOUR, newFrontierNode);
			newFrontierNode.setNeighbours(newFrontierNode.RIGHTNEIGHBOUR,
					newNode);
			// insert in mapTree
			key = Utility.makeKey(newFrontierNode.getXValue(), newFrontierNode
					.getYValue());
			this.mapTree.put(key, newFrontierNode);
			break;
		// right neighbour
		case 1:
			// Create new frontierNode
			newFrontierNode = new GraphNode(newNode.getXValue() + 1, newNode
					.getYValue(), type);
			// Set new neighbourhood
			newNode.setNeighbours(newNode.RIGHTNEIGHBOUR, newFrontierNode);
			newFrontierNode.setNeighbours(newFrontierNode.LEFTNEIGHBOUR,
					newNode);
			// insert in mapTree
			key = Utility.makeKey(newFrontierNode.getXValue(), newFrontierNode
					.getYValue());
			this.mapTree.put(key, newFrontierNode);
			break;
		// bottom neighbour
		case 2:
			newFrontierNode = new GraphNode(newNode.getXValue(), newNode
					.getYValue() + 1, type);
			// Set new neighbourhood
			newNode.setNeighbours(newNode.BOTTOMNEIGHBOUR, newFrontierNode);
			newFrontierNode
					.setNeighbours(newFrontierNode.TOPNEIGHBOUR, newNode);
			// insert in mapTree
			key = Utility.makeKey(newFrontierNode.getXValue(), newFrontierNode
					.getYValue());
			this.mapTree.put(key, newFrontierNode);
			break;
		// upper neighbour
		case 3:
			newFrontierNode = new GraphNode(newNode.getXValue(), newNode
					.getYValue() - 1, type);
			// Set new neighbourhood
			newNode.setNeighbours(newNode.TOPNEIGHBOUR, newFrontierNode);
			newFrontierNode.setNeighbours(newFrontierNode.BOTTOMNEIGHBOUR,
					newNode);
			// insert in mapTree
			key = Utility.makeKey(newFrontierNode.getXValue(), newFrontierNode
					.getYValue());
			this.mapTree.put(key, newFrontierNode);
			break;
		}
		this.frontierList.add(newFrontierNode);
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
		// Iteration trough the frontierList for removing the node if it
		// exists in the LinkedList
		Iterator<GraphNode> it = frontierList.listIterator();
		while (it.hasNext()) {
			GraphNode itNode = it.next();
			if (itNode.getXValue() == x && itNode.getYValue() == y) {
				it.remove();
			}
		}
		// Update the borders of the map and the NodeType as well as the
		// visitCounter
		this.updateMapBorders(x, y);
		this.getNode(x, y).setNodeType(newNodeType);
		this.getNode(x, y).increaseVisitCounter();
	}

	/**
	 * Saves the coordinates and the status of the non-frontierNodes in a
	 * string-array. These information suffice to save and to recreate the 
	 * GridMap.
	 * 
	 * @return The string with the nodes' information
	 */
	public String[] serializeMapInString() {
		// Number of nodes which are in the structure and NOT frontiernodes
		int sizeOfMap = this.mapTree.size();
		int sizeOfFrontierNodes = this.frontierList.size();
		int realSizeOfNodes = sizeOfMap-sizeOfFrontierNodes;
		String[] returnString = new String[realSizeOfNodes] ;
		// iterate trough the mapTree and saves all non-frontierNodes in a
		// string
		Iterator<Entry<Integer, GraphNode>> it = mapTree.entrySet().iterator();
		MapNode bufferNode;
		int index = 0;
		while (it.hasNext()) {
			bufferNode = it.next().getValue();
			if (bufferNode.getNodeType() != NodeType.FRONTIERNODE) {
				returnString[index] = (String.valueOf(bufferNode.getXValue())
						+ " " + String.valueOf(bufferNode.getYValue()) + " "
						+ String.valueOf(bufferNode.getNodeType()) + "\n");
				index++;
			}
		}
		return returnString;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	//public static final Parcelable.Creator<GridMap> CREATOR = new Parcelable.Creator<GridMap>(){

	/*	public GridMap createFromParcel(Parcel source) {
			GridMap newMap = new GridMap();
			LinkedList<MapNode> newList = new LinkedList<MapNode>();
			source.readList(newList, null);
			for(MapNode noode : newList){
				newMap.addNode(noode.getXValue(), noode.getYValue(), noode.getNodeType());
			}
			return newMap;
		}

		public GridMap[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.getMapAsList());
	}

}
