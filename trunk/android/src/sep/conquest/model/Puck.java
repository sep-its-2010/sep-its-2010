package sep.conquest.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sep.conquest.model.requests.HelloRequest;
import sep.conquest.model.requests.PuckMessageType;
import sep.conquest.util.ConquestLog;

/**
 * Represents an abstract Puck robot.
 * 
 * @author Andreas Poxrucker
 * 
 */
public abstract class Puck implements IComClient, IRobot {

	/**
	 * Global unique id.
	 */
	private UUID id;

	/**
	 * Local map.
	 */
	private GridMap map = new GridMap();

	/**
	 * Saves status for each participating Puck.
	 */
	private Map<UUID, RobotStatus> states;

	/**
	 * Executes logic and behaviour.
	 */
	private LogicThread logicThread;

	/**
	 * The thread executor for the logic-thread.
	 */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * Indicates whether the robot is controlled by the user.
	 */
	private boolean controlled = false;

	/**
	 * Constructor initializing ID, local map, own state, logic thread and
	 * broadcast message Handler.
	 * 
	 * @param id
	 *            The unique universally id of the Puck.
	 */
	public Puck(UUID id) {

		// Set ID.
		this.id = id;

		// initialize state map and add own initial state 'Localizing'.
		states = new TreeMap<UUID, RobotStatus>();
		states.put(id, new RobotStatus());

		// start the logic thread
		logicThread = new LogicThread(this);
		executor.execute(logicThread);
	}

	/**
	 * Returns the state-map of the robots.
	 * 
	 * @return The map of robot-states.
	 */
	public Map<UUID, RobotStatus> getRobotStatus() {
		return states;
	}

	/**
	 * Initiate a hello-message-chain.
	 */
	public void sendHello() {
		ComManager comMan = ComManager.getInstance();
		HelloRequest request = new HelloRequest(getID());
		comMan.broadcast(request);
	}

	/**
	 * The method delivers a message from a specific sender and puts it on the
	 * LogicThread-queue.
	 * 
	 * @param sender
	 *            The sender of the broadcast message.
	 * @param request
	 *            The message which has to be delivered.
	 */
	public void deliver(IRequest request) {
		logicThread.addMessage(request);
	}

	/**
	 * Returns the map of the robot.
	 * 
	 * @return The map.
	 */
	public GridMap getMap() {
		return map;
	}

	/**
	 * Translates a direction-command in a specific drive-call of the concrete
	 * puck.
	 * 
	 * @param direction
	 */
	public void driveCommand(Orientation direction) {
		switch (direction) {
		case UP:
			this.forward();
			break;
		case DOWN:
			this.turn();
			break;
		case LEFT:
			this.left();
			break;
		case RIGHT:
			this.right();
			break;
		default:
			ConquestLog.addMessage(this, "Error in driveCommand()");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IComClient#getID()
	 */
	public UUID getID() {
		return this.id;
	}

	/**
	 * This method sends a message in form of a byte-array via socket.
	 * 
	 * @param buffer
	 *            The message that will be sent.
	 */
	public abstract void writeSocket(byte[] buffer);

	/**
	 * This method read if an incoming message has arrived at the socket.
	 * 
	 * @return Returns the message that was sent by an e-puck roboter.
	 */
	public abstract byte[] readSocket();

	// TODO Add reset, status and set_led to irobot interface!
	public void forward() {
		byte[] request = new byte[32];
		request[0] = (byte) (PuckMessageType.MOVE.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.MOVE.getTypeCode() >> 8) & 0xff);

		writeSocket(request);
	}

	public void left() {
		byte[] request = new byte[32];
		request[0] = (byte) (PuckMessageType.TURN.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.TURN.getTypeCode() >> 8) & 0xff);
		request[2] = (byte) 1; // turn right

		writeSocket(request);
	}

	public void right() {
		byte[] request = new byte[32];
		request[0] = (byte) (PuckMessageType.TURN.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.TURN.getTypeCode() >> 8) & 0xff);
		request[2] = (byte) -1; // turn left

		writeSocket(request);
	}

	public void setSpeed(SpeedLevel level) {
		byte[] request = new byte[32];
		request[0] = (byte) (PuckMessageType.SET_SPEED.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.SET_SPEED.getTypeCode() >> 8) & 0xff);
		request[2] = (byte) level.getSpeed();

		writeSocket(request);
	}

	public void turn() {
		byte[] request = new byte[32];
		request[0] = (byte) (PuckMessageType.TURN.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.TURN.getTypeCode() >> 8) & 0xff);
		request[2] = (byte) 2;

		writeSocket(request);
	}

	public void setControlled(boolean enabled) {
		byte[] request = new byte[32];
		controlled = enabled;
		request[0] = (byte) (PuckMessageType.SET_LED.getTypeCode() & 0xff);
		request[1] = (byte) ((PuckMessageType.SET_LED.getTypeCode() >> 8) & 0xff);
		request[2] = (byte) 10;

		writeSocket(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IRobot#isControlled()
	 */
	public boolean isControlled() {
		return controlled;
	}
}
