package sep.conquest.model;

import java.util.TreeMap;
import java.util.UUID;

/**
 * The Simulator class represents a virtual socket for a proxy-robot within the
 * e-puck-conquest application. It is used to simulate messages for the
 * navigation-algorithms. Therefore a internal stored map is used.
 * 
 * @author Andreas Wilhelm
 *
 */
public class Simulator {

    /**
     * The static instance to implement the singleton pattern.
     */
    private static final Simulator INSTANCE = new Simulator();
    
    /**
     * The private constructor to realize the singleton pattern.
     */
    private Simulator(){ }
    
    /**
     * The getInstance method returns the singleton object of the Simulator
     * class.
     * 
     * @return the singleton instance of Simulator.
     */
    public static Simulator getInstance() {
        return INSTANCE;
    }	
	
	/**
	 * The loaded map within the simulator.
	 */
	private GridMap loadedMap;
	
	/**
	 * The initial positions of the robots.
	 */
	private TreeMap<UUID, int[]> initialPositions;
	
	/**
	 * The constructor expects the map and all initial positions for the robots.
	 * 
	 * @param map The map which should be used.
	 * @param initialPositions The initial positions of the robots.
	 */
	public Simulator(GridMap map, TreeMap<UUID, int[]> initialPositions) {
		this.loadedMap = map;
		// TODO check positions
		this.initialPositions = initialPositions;
	}
	
	/**
	 * Returns the node-type for a specified position on the map.
	 * 
	 * @param position The position of the node.
	 * @return The node-type.
	 */
	public NodeType getNodeType(int[] position) {
		// TODO analyse map informations
		return NodeType.FRONTIERNODE;
	}
	
	/**
	 * Returns the initial position of a specified robot.
	 * 
	 * @param robot The uuid of the robot.
	 * @return The initial position of the robot.
	 */
	public int[] getInitialPosition(UUID robot) {
		return initialPositions.get(robot);
	}
	
}
