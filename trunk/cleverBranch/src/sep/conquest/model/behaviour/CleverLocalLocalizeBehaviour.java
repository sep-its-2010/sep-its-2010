package sep.conquest.model.behaviour;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import sep.conquest.model.ComManager;
import sep.conquest.model.IComClient;
import sep.conquest.model.IRSensor;
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
	private static final int RIGHT = 1;
	private static final int LEFT = 2;
	private static final int DOWN = 3;
	
	private enum LocalizeSteps {
		INITIALIZE,
		REQUEST_STATUS,
		EXPLORE,
		PASS_ON;
	}
	
	private enum DriveSteps {
		EXPLORE,
		TURN,
		RETURN
	}
	
	private DriveSteps currentDrive = DriveSteps.EXPLORE;
	
	private LocalizeSteps currentStep = LocalizeSteps.INITIALIZE;

	private static UUID[] notYetLocalized = null;

	private static UUID currentPuck;
	
	private boolean[] possibleDirections = new boolean[4];
	
	private int exploredDirections = 0;
	
	private UUID passedRobot = null;
	
	private boolean[] sensorArray = new boolean[IRSensor.values().length];	
	
	private Orientation direction = Orientation.UP;
	
	private int command = 0;

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
			if (currentPuck == robot.getID())
				execute(map, robot);
			break;
			
		case REQUEST_STATUS:
			if (puck.collisionDir != Orientation.UNKNOWN) {
				if (!robot.isMessageExpected()) {
					RobotStatus status = puck.getRobotStatus().get(
							puck.getID());

					if (!Arrays.equals(sensorArray, status.getSensorCollisionArray())) {
						int[] pos = Utility.extractCoordinates(puck.collisionPos);
						switch (puck.collisionDir) {
						case UP:
							pos[1]++;
							break;

						case DOWN:
							pos[1]--;
							break;

						case RIGHT:
							pos[0]++;
							break;

						case LEFT:
							pos[1]--;
							break;
						}
						status.setPosition(pos);
						currentStep = LocalizeSteps.EXPLORE;
					}
					sensorArray = status.getSensorCollisionArray().clone();
					puck.collisionDir = Orientation.UNKNOWN;
				}
			}
			if (currentPuck == robot.getID() || puck.localizeSuccessor != null) {
				RobotStatus status = robot.getRobotStatus().get(robot.getID());
				
				// position wasn't set externally => first robot
				if (!puck.isPositionSet()) {
					status.getPosition()[0] = 0;
					status.getPosition()[1] = 0;
					robot.requestStatus();
				}
				
				currentStep = LocalizeSteps.EXPLORE;
			}
			break;
			
		case EXPLORE:
			if (!robot.isMessageExpected()) {
				RobotStatus status = robot.getRobotStatus().get(robot.getID());
				
				// set initial directions
				if (!anyTrue(possibleDirections))
					possibleDirections = getPossibleDirections(status.getNodeType());
				
				// collision happened => send collision request

				if (!Arrays.equals(sensorArray, status.getSensorCollisionArray())) {
					int[] pos = status.getPosition();
					direction = Orientation.turn(direction);
					LocalizeCollisionRequest req = new LocalizeCollisionRequest(
							robot.getID(), new UUID[0], Utility.makeKey(pos[0], pos[1]),
							direction);
					robot.broadcast(req);
					
					sensorArray = status.getSensorCollisionArray().clone();
					exploredDirections++;
					
				// try to explore all possible directions
				} else if (exploredDirections < 4) {
					if (possibleDirections[exploredDirections]) {
						switch (currentDrive) {
						case EXPLORE:
							switch (exploredDirections) {
							case UP:
								command = Orientation.addGlobalDirection(direction, Orientation.UP);
								direction = Orientation.UP;
								break;
							case DOWN:
								command = Orientation.addGlobalDirection(direction, Orientation.DOWN);
								direction = Orientation.DOWN;								
								break;
							case RIGHT:
								command = Orientation.addGlobalDirection(direction, Orientation.RIGHT);
								direction = Orientation.RIGHT;
								break;			
							case LEFT:
								command = Orientation.addGlobalDirection(direction, Orientation.LEFT);
								direction = Orientation.LEFT;
								break;				
							}
							currentDrive = DriveSteps.TURN;
							break;
							
						case TURN:
							if (command == 0) {
								command = 2;
								direction = Orientation.turn(direction);
								currentDrive = DriveSteps.RETURN;
							} else {
								command = 0;
							}
							
							break;
							
						case RETURN:
							command = 0;
							currentDrive = DriveSteps.EXPLORE;
							exploredDirections++;
							break;
						}
						robot.driveCommand(command);						
					} else {
						exploredDirections++;
						execute(map, robot);
					}
						
					
				
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
					LocalizePassRequest req = new LocalizePassRequest(robot.getID(), new UUID[0]);
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
	
	private Orientation getOrientation(int orientation) {
		switch (orientation) {
		case UP:
			return Orientation.UP;
		case DOWN:
			return Orientation.DOWN;	
		case RIGHT:
			return Orientation.RIGHT;	
		case LEFT:
			return Orientation.LEFT;	
		}
		return Orientation.UNKNOWN;
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
				if (client instanceof RealPuck) {
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
