package sep.conquest.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.util.ConquestLog;

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
	private ConcurrentLinkedQueue<IRequest> bcQueue;

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
	private Map<int[], Integer> initMap() {
		Map<int[], Integer> map = new TreeMap<int[], Integer>();
		LinkedList<MapNode> mapList = robot.getMap().getMapAsList();
		Iterator<MapNode> it = mapList.iterator();
		while (it.hasNext()) {
			MapNode node = it.next();
			int[] pos = { node.getXValue(), node.getYValue() };
			map.put(pos, 0);
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
	private int[] getBestNode(Map<int[], Integer> map) {
		int bestValue = 0;
		int[] bestNode = { 0, 0 };
		Set<int[]> nodes = map.keySet();
		Iterator<int[]> it = nodes.iterator();
		while (it.hasNext()) {
			int[] node = it.next();
			Integer cost = map.get(node);
			if (bestValue == 0 || cost < bestValue) {
				bestValue = cost;
				bestNode = node;
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
	private void driveTo(int[] intentNode) {
		int[][] dest = { intentNode };
		PathNode[] path = aStar.find(getRobotState().getPosition(), dest);

		// makes driving sense?
		if (path.length >= 1) {
			for (int i = 0; i < path.length; i++) {
				MapNode mapNode = (MapNode) path[i].getNode();
				int[] node = { mapNode.getXValue(), mapNode.getYValue() };

				// position on driving-path found => send next command
				if (node.equals(intentNode)) {
					MapNode nextMapNode = (MapNode) path[i + 1].getNode();
					int[] nextNode = { nextMapNode.getXValue(),
							nextMapNode.getYValue() };
					sendCommand(node, nextNode);
				}
			}
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
	private void writeSocket(byte[] buffer) {
		btMessage = new byte[32];
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
				bcHandler.handleRequest(bcQueue.poll());
				changed = true;
			}
			
			// execute behaviours on changed state
			if (changed) {
				Map<int[], Integer> map = initMap();
				map = stateBehaviour.execute(map, robot);
				driveTo(getBestNode(map));
			} else
				Thread.yield();
		}
	}

}
