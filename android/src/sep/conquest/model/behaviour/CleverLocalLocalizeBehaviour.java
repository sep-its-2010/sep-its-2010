package sep.conquest.model.behaviour;

import java.util.Map;
import java.util.UUID;

import sep.conquest.model.ComManager;
import sep.conquest.model.IComClient;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RealPuck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
import sep.conquest.model.requests.LocalizeCollisionRequest;
import sep.conquest.model.requests.LocalizeFinishedRequest;
import sep.conquest.model.requests.LocalizePassRequest;
import sep.conquest.util.Utility;

/**
 * LocalLocalizeBehaviour represents a behaviour for localizing adjacent robots.
 * It extends the Behaviour class for enabling a behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class CleverLocalLocalizeBehaviour extends Behaviour {
	
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int RIGHT = 2;
	private static final int LEFT = 3;	
	
	private enum LocalizeSteps {
		INITIALIZE,
		REQUEST_STATUS,
		EXPLORE,
		PASS_ON;
	}
	
	private LocalizeSteps currentStep = LocalizeSteps.INITIALIZE;

	private static UUID[] notYetLocalized = null;

	private static UUID currentPuck;
	
	private boolean[] possibleDirections = new boolean[4];
	
	private int exploredDirections = 0;
	
	private UUID passedRobot = null;
	

	/**
	 * The constructor enables chain-handling by calling the constructor of the
	 * super-class (Behaviour).
	 * 
	 * @param stateLevel
	 *            The level of the state.
	 * @param next
	 *            A reference to the next behaviour.
	 */
	protected CleverLocalLocalizeBehaviour(State stateLevel, IBehaviour next) {
		super(stateLevel, next);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
	 */
	public boolean execute(Map<Integer, Integer> map, Puck robot) {
		
		RealPuck puck = (RealPuck) robot;
		
		switch(currentStep) {
		case INITIALIZE:
			initialize(robot);
			currentStep = LocalizeSteps.REQUEST_STATUS;
			break;
			
		case REQUEST_STATUS:
			if (currentPuck == robot.getID() || puck.localizeSuccessor != null) {
				RobotStatus status = robot.getRobotStatus().get(robot.getID());
				
				// position wasn't set externally => first robot
				if (!puck.isPositionSet()) {
					status.getPosition()[0] = 0;
					status.getPosition()[1] = 0;
				}
				robot.requestStatus();
				currentStep = LocalizeSteps.EXPLORE;
			}
			break;
			
		case EXPLORE:
			if (!robot.isMessageExpected()) {
				RobotStatus status = robot.getRobotStatus().get(robot.getID());
				
				// set initial directions
				if (exploredDirections == 0)
					possibleDirections = getPossibleDirections(status.getNodeType());
				
				// collision happened => send collision request
				boolean[] sensorArray = status.getSensorCollisionArray();
				if (anyTrue(sensorArray)) {
					int[] pos = status.getPosition();
					Orientation tOrientation = Orientation.turn(status.getOrientation());
					LocalizeCollisionRequest req = new LocalizeCollisionRequest(
							robot.getID(), null, Utility.makeKey(pos[0], pos[1]),
							tOrientation);
					robot.broadcast(req);
					
					for (int i = 0; i < sensorArray.length; i++)
						sensorArray[i] = false;
				
				// try to explore all possible directions
				} else if (exploredDirections < 4) {
					if (possibleDirections[exploredDirections]) {
						int command = 0;
						switch (exploredDirections) {
						case UP:
							command = Orientation.addGlobalDirection(status.getOrientation(), Orientation.UP);
							break;
						case DOWN:
							command = Orientation.addGlobalDirection(status.getOrientation(), Orientation.DOWN);
							break;
						case RIGHT:
							command = Orientation.addGlobalDirection(status.getOrientation(), Orientation.RIGHT);
							break;			
						case LEFT:
							command = Orientation.addGlobalDirection(status.getOrientation(), Orientation.UP);
							break;				
						}
						robot.driveCommand(command);						
					}
					exploredDirections++;
				
				// exploration finished => pass to next robot
				} else {
					currentStep = LocalizeSteps.PASS_ON;
				}				
			}
			break;
			
		case PASS_ON:
			if (!robot.isMessageExpected()) {
				UUID next[] = {puck.nextLocalizers.getFirst()};
				if (next == null) {
					if (puck.localizeSuccessor != null) {
						UUID[] receiver = {puck.localizeSuccessor};
						LocalizeFinishedRequest req = new LocalizeFinishedRequest(puck.getID(), receiver);
						robot.broadcast(req);
					}
					
					robot.changeBehaviour(State.EXPLORE);
				} else if (next[0] != passedRobot) {
					LocalizePassRequest req = new LocalizePassRequest(robot.getID(), null);
					robot.broadcast(req);
					passedRobot = next[0];	
				}
			}
			break;
		}

		return super.execute(map, robot);
	}
	
	private boolean anyTrue(boolean[] array) {
		for (boolean val: array)
			if (val)
				return true;
		return false;
	}
	
	/**
	 * Returns the possible path-directions of a given nodetype.
	 * 
	 * @param nodeType The nodetype.
	 * @return The possible directions as boolean array.
	 */
	private boolean[] getPossibleDirections(NodeType nodeType) {
		boolean[] possibleDirections = new boolean[4];
		
		switch (nodeType) {
		case TOPLEFTEDGE:
			possibleDirections[DOWN] = true;
			possibleDirections[RIGHT] = true;
			break;
		case TOPRIGHTEDGE:
			possibleDirections[DOWN] = true;
			possibleDirections[LEFT] = true;
			break;	
		case BOTTOMLEFTEDGE:
			possibleDirections[UP] = true;
			possibleDirections[RIGHT] = true;
			break;	
		case BOTTOMRIGHTEDGE:
			possibleDirections[UP] = true;
			possibleDirections[LEFT] = true;
			break;
		case TOPT:
			possibleDirections[DOWN] = true;
			possibleDirections[LEFT] = true;
			possibleDirections[RIGHT] = true;
			break;	
		case RIGHTT:
			possibleDirections[LEFT] = true;
			possibleDirections[UP] = true;
			possibleDirections[DOWN] = true;
			break;	
		case BOTTOMT:
			possibleDirections[UP] = true;
			possibleDirections[LEFT] = true;
			possibleDirections[RIGHT] = true;
			break;	
		case LEFTT:
			possibleDirections[RIGHT] = true;
			possibleDirections[UP] = true;
			possibleDirections[DOWN] = true;
			break;	
		case CROSS:
			possibleDirections[UP] = true;
			possibleDirections[DOWN] = true;
			possibleDirections[LEFT] = true;
			possibleDirections[RIGHT] = true;
			break;				
		}

		
		return possibleDirections;
	}

	/**
	 * Initialize robot-array with not yet localized agents and first one.
	 */
	private void initialize(Puck robot) {
		if (notYetLocalized == null) {
			notYetLocalized = new UUID[robot.getRobotStatus().size()];
			ComManager comManager = ComManager.getInstance();
			String smallestName = null;
			int i = 0;
			for (UUID id : robot.getRobotStatus().keySet()) {
				notYetLocalized[i++] = id;
				IComClient client = comManager.getClient(id);
				if (client.getClass().getName() == "sep.conquest.model.RealPuck") {
					RealPuck puck = (RealPuck) client;
					if (smallestName == null
							|| puck.getName().compareTo(smallestName) < 0) {
						smallestName = puck.getName();
						currentPuck = id;
					}
				}
			}
		}
	}
}
