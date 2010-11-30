package sep.conquest.model;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	 * The timer period.
	 */
	private static final long PERIOD = 1000;

	/**
	 * The loaded map within the simulator.
	 */
	private static GridMap map;

	/**
	 * Saves the request messages of the VirtualPucks.
	 */
	private static Queue<IRequest> messageQueue;

	/**
	 * Executes request handling.
	 */
	private static Timer timer;

	/**
	 * Indicates whether simulator has been stopped and must be reseted.
	 */
	private static boolean stopped;

	/**
	 * The initial positions of the robots.
	 */
	private static TreeMap<UUID, int[]> initialPositions;

	/**
	 * The output buffer for each VirtualPuck.
	 */
	private static TreeMap<UUID, byte[]> outputBuffer;

	/**
	 * The getInstance method returns the singleton object of the Simulator
	 * class.
	 * 
	 * @return The singleton instance of Simulator.
	 */
	public static Simulator getInstance() {
		if (map == null || initialPositions == null)
			throw new IllegalStateException(
					"Error! Simulator wasn't initialized!");
		return INSTANCE;
	}

	/**
	 * The private constructor to realize a singleton behavior.
	 */
	private Simulator() {
		messageQueue = new ConcurrentLinkedQueue<IRequest>();
	}
	
	/**
	 * Sets a global map on which the calculations are executed.
	 * 
	 * @param map The new map.
	 */
	public void setMap(GridMap map) {
		Simulator.map = map;
	}
	
	/**
	 * Sets the initial positions of the robots.
	 * 
	 * @param initialPositions The initial positions.
	 */
	public void setInitialPositions(TreeMap<UUID, int[]> initialPositions) {
		Simulator.initialPositions = initialPositions;
	}
	

	/**
	 * Adds a new request message to the simulator message queue.
	 * 
	 * @param request
	 *            The request to add.
	 */
	public void addRequest(IRequest request) {
		messageQueue.offer(request);
	}

	/**
	 * Stops the execution of the simulator.
	 */
	public void stop() {
		timer.cancel();
		stopped = true;
	}

	public void pause() {
		timer.cancel();
		stopped = false;
	}

	/**
	 * Starts the execution of the simulator.
	 */
	public void start() {
		if (!stopped) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new SimulatorTask(), 0, PERIOD);
		} else {

		}
	}

	/**
	 * Executes
	 */
	public void step() {
		handleNextRequest();
	}

	private void handleNextRequest() {
		if (!messageQueue.isEmpty()) {
			IRequest request = messageQueue.poll();

		}
	}

	private class SimulatorTask extends TimerTask {

		public void run() {
			step();
		}
	}
}