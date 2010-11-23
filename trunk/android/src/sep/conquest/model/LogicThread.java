package sep.conquest.model;

import sep.conquest.util.ConquestLog;

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
	 * The current behaviour.
	 */
	private IBehaviour stateBehaviour;

	/**
	 * The constructor of LogicThread assigns the robot as well as its status to
	 * attributes.
	 * 
	 * @param robot
	 */
	public LogicThread(Puck robot) {
		this.robot = robot;
		stateBehaviour = Behaviour
				.getFirstBehaviour(getRobotState().getState());
	}

	/**
	 * Change the behaviour due to the state change.
	 * 
	 * @param state
	 */
	private void changeBehaviour(State state) {
		if (state != getRobotState().getState()) {
			stateBehaviour = Behaviour.getFirstBehaviour(state);
			ConquestLog.addMessage(this, "Behaviour changed to "
					+ stateBehaviour.getClass().toString());
		}
	}

	/**
	 * Returns the current state of the robot.
	 * 
	 * @return The state.
	 */
	private RobotStatus getRobotState() {
		return robot.getRobotStatus().get(robot.getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

	}

}
