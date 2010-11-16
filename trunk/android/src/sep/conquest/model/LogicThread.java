package sep.conquest.model;

/**
 * The LogicThread class will be used by Puck objects for navigation decisions.
 * 
 * @author Andreas Wilhelm
 *
 */
public class LogicThread implements Runnable {
	
	/**
	 * A instance of the AStarPathFinder class for shortest-path-finding.
	 */
	private AStarPathFinder aStar = AStarPathFinder.getInstance();
	
	/**
	 * The current 
	 */
	private IBehaviour stateBehaviour;
	
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // TODO Auto-generated method stub

    }

}
