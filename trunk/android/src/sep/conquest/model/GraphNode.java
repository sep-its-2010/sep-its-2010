package sep.conquest.model;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The class GraphNode inherits of the class MapNode and saves additionally
 * the references to its neighbours in an array. This is necessary for the 
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
	public  final int BOTTOMNEIGHBOUR = 2;
	
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
	public GraphNode(int newXValue, int newYValue, NodeType newNodeState){
		super(newXValue,newYValue,newNodeState);
	}
	
	/**
	 * This method returns a reference on the direct neighbours of the node.
	 * 
	 * @return The attribute neighbours.
	 */
	public GraphNode[] getNeighbours(){
	 return this.neighbours;
	}
	
	/**
	 * Sets the neighbours in the attribute neighbours
	 * 
	 * @param index The Position of the neighbour
	 * @param neighbour The reference to the neighbour
	 */
	public void setNeighbours(int index, GraphNode neighbour){
		this.neighbours[index] = neighbour;
	}
	
	/**
	 * Makes a deep copy of the GraphNode and returns the result
	 * 
	 * @return The copy of the GraphNode
	 */
	  public GraphNode clone() {
	    try {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
	      ObjectOutputStream oos = new ObjectOutputStream(baos);
	      oos.writeObject(this);
	      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	      ObjectInputStream ois = new ObjectInputStream(bais);
	      GraphNode deepCopy = (GraphNode) ois.readObject();
	      return deepCopy;
	    } catch (IOException e) {
	      e.printStackTrace();
	    } catch(ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
}
