package sep.conquest.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import sep.conquest.model.behaviour.Behaviour;
import sep.conquest.model.behaviour.BehaviourFactory;
import sep.conquest.model.behaviour.IBehaviour;
import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.util.ConquestLog;
import sep.conquest.util.Utility;

/**
 * The LogicThread class will be used by Puck objects for navigation decisions.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class LogicThread implements Runnable {

	/**
	 * The length of a bluetooth message.
	 */
	private static final int MSGLENGTH = 32;

	/**
	 * A instance of the AStarPathFinder class for shortest-path-finding.
	 */
	private AStarPathFinder aStar = new AStarPathFinder();

	/**
	 * A reference to the robot.
	 */
	private Puck robot;

	/**
	 * The current behaviour.
	 */
	private IBehaviour stateBehaviour;

	/**
	 * First Handler to handle broadcast messages.
	 */
	private Handler bcHandler;

	/**
	 * First Handler to handle bluetooth-messages.
	 */
	private Handler btHandler;

	/**
	 * The queue for broadcast messages.
	 */
	private ConcurrentLinkedQueue<IRequest> bcQueue = 
		new ConcurrentLinkedQueue<IRequest>();

	/**
	 * Indicates whether a bluetooth messages is expected.
	 */
	private boolean expectBTMessage = false;

	/**
	 * The bluetooth message.
	 */
	private byte[] btMessage = new byte[MSGLENGTH];
	
	/**
	 * The length of the current message in buffer.
	 */
	private int btMessageLen = 0;

	/**
	 * The constructor of LogicThread assigns the robot as well as its status to
	 * attributes.
	 * 
	 * @param robot
	 */
	public LogicThread(Puck robot) {
		this.robot = robot;
		State state = robot.getRobotStatus().get(robot.getID()).getState();		
		BehaviourFactory.createBehaviourChain(state);		
		stateBehaviour = Behaviour
				.getFirstBehaviour(getRobotState().getState());
		bcHandler = HandlerFactory.getPuckBCChain(this);
		btHandler = HandlerFactory.getPuckBTChain(this);
	}

	/**
	 * Puts a request message of the broadcast-communication on the queue.
	 * 
	 * @param request
	 *            The new request message.
	 */
	public void addMessage(IRequest request) {
		bcQueue.add(request);
	}

	/**
	 * Change the behaviour due to the state change.
	 * 
	 * @param state
	 */
	public void changeBehaviour(State state) {
		if (state != getRobotState().getState()) {
			BehaviourFactory.createBehaviourChain(state);
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
	
	/**
	 * Returns the robot of the logic thread.
	 * 
	 * @return The Puck.
	 */
	public Puck getRobot() {
		return robot;
	}

	/**
	 * Initialize a map with the map-information from the robot.
	 * 
	 * @return The initialized map.
	 */
	private Map<Integer, Integer> initMap() {
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		LinkedList<MapNode> mapList = robot.getMap().getMapAsList();
		Iterator<MapNode> it = mapList.iterator();
		while (it.hasNext()) {
			MapNode node = it.next();
			map.put(Utility.makeKey(node.getXValue(), node.getYValue()), 0);
		}
		return map;
	}

	/**
	 * Returns a map-node which has the most beneficial navigation costs.
	 * 
	 * @param map
	 *            The map.
	 * @return The best node.
	 */
	private int[] getBestNode(Map<Integer, Integer> map) {
		int bestValue = 0;
		int[] bestNode = { 0, 0 };
		Set<Integer> nodes = map.keySet();
		for (Integer node: nodes) {
			Integer cost = map.get(node);
			if (bestValue == 0 || cost < bestValue) {
				bestValue = cost;
				bestNode = Utility.extractCoordinates(node);
			}
		}
		return bestNode;
	}

	/**
	 * Drive to a specific node.
	 * 
	 * @param intentNode
	 *            The node.
	 */
	public void driveTo() {
		int[][] dest = { getRobotState().getIntentPosition() };
		PathNode[] path = aStar.find(robot, getRobotState().getPosition(), dest);

		if (path != null) {
			MapNode nextMapNode = (MapNode) path[1].getNode();
			int[] node = { nextMapNode.getXValue(), nextMapNode.getYValue() };
			sendCommand(getRobotState().getPosition(), node);
		}
	}

	/**
	 * The method takes a start- and a end-node to calculate a corresponding
	 * navigation command to reach the end-node.
	 * 
	 * @param startNode
	 *            The start-node.
	 * @param endNode
	 *            The end-node.
	 */
	private void sendCommand(int[] startNode, int[] endNode) {
		Orientation direction = Orientation.UNKNOWN;

		if (startNode[0] < endNode[0])
			direction = Orientation.RIGHT;
		else if (startNode[0] > endNode[0])
			direction = Orientation.LEFT;
		else if (startNode[1] < endNode[1])
			direction = Orientation.UP;
		else if (startNode[1] > endNode[1])
			direction = Orientation.DOWN;

		Orientation command = Orientation.addDirection(getRobotState()
				.getOrientation(), direction);

		robot.driveCommand(command);
	}

	/**
	 * Returns a bluetooth message if message-receiving is expected and the 
	 * byte-length of the message on the socket has reached the desired length.
	 * 
	 * @return The message. If no message exists, an empty byte-array will be
	 * 	returned.
	 */
	private IRequest getBTMessage() {
		
		// if a message is expected => read socket
		if (expectBTMessage) {
			byte[] msg = robot.readSocket();
			
			// check message length
			if ((btMessageLen + msg.length) > MSGLENGTH)
				throw new IllegalArgumentException("Message from socket is " +
						"longer than " + MSGLENGTH + " bytes!");
			

			for (int i = 0; i < msg.length; i++) {
				btMessage[btMessageLen] = msg[i];
				btMessageLen++;
			}
		}
		
		// 
		if (MSGLENGTH == btMessageLen) {
			expectBTMessage = false;
			return new PuckRequest(btMessage);
		} else
			return null;
	}
	
	/**
	 * Writes a message to the socket and enables the receiving capability.
	 * 
	 * @param buffer The message to send via a socket.
	 */
	public void writeSocket(byte[] buffer) {
		btMessage = new byte[MSGLENGTH];
		btMessageLen = 0;
		expectBTMessage = true;
		
		robot.writeSocket(buffer);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			boolean changed = false;
			
			// handle bluetooth messages			
			if (getBTMessage() != null) {
				btHandler.handleRequest(getBTMessage());
				changed = true;
			}
			
			// handle broadcast messages
			if (!bcQueue.isEmpty()) {
				IRequest req = bcQueue.poll();
				bcHandler.handleRequest(req);
				ConquestLog.addMessage(this, "Robot: " + robot.getID() + " Sender: " + req.getSender());
				changed = true;
			}
			
			// execute behaviours on changed state
			if (changed) {
				Map<Integer, Integer> map = initMap();
				map = stateBehaviour.execute(map, robot);
				getRobotState().setIntentPosition(getBestNode(map));
				driveTo();
			} else
				Thread.yield();
		}
	}

}
