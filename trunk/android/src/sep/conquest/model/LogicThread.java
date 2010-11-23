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
	 * A reference to the robot.
	 */
	private Puck robot;
	
	/**
	 * The status of the robot.
	 */
	private RobotStatus state;
	
	/**
	 * The current 
	 */
	private IBehaviour stateBehaviour;
	
	/**
	 * The constructor of LogicThread assigns the robot as well as its status
	 * to attributes.
	 * 
	 * @param robot
	 */
	public LogicThread(Puck robot) {
		this.robot = robot;
		state = this.robot.getRobotStatus().get(this.robot.getID());
	}
	
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // TODO Auto-generated method stub

    }

}
