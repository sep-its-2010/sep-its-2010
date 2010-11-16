package sep.conquest.model;

/**
 * The class realizes an A*-algorithm for finding the shortest path between
 * nodes.
 * 
 * @author Andreas Wilhelm
 *
 */
public class AStarPathFinder implements IPathFinder {
	
    // local declarations
    /**
     * The INSTANCE attribute represents the single instance to use the
     * singleton pattern.
     */
    private static final AStarPathFinder INSTANCE = new AStarPathFinder();
    
    /**
     * The private constructor to realize the singleton pattern.
     */
    private AStarPathFinder(){ }
    
    /**
     * The getInstance method returns the singleton object of the ComManager
     * class.
     * 
     * @return The singleton instance of AStarPathFinder.
     */
    public static AStarPathFinder getInstance() {
        return INSTANCE;
    } 	

	/* (non-Javadoc)
	 * @see sep.conquest.model.IPathFinder#caluclateCosts(java.lang.Object, java.lang.Object)
	 */
	public int caluclateCosts(MapNode from, MapNode to) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IPathFinder#find(java.lang.Object, java.lang.Object[])
	 */
	public PathNode[] find(MapNode start, MapNode[] destinations) {
		// TODO Auto-generated method stub
		return null;
	}
}
