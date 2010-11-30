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
   * The timer period.
   */
  private static final long PERIOD = 1000;

  /**
   * The loaded map within the simulator.
   */
  private GridMap map;

  /**
   * Saves the request messages of the VirtualPucks.
   */
  private Queue<IRequest> messageQueue;

  /**
   * Executes request handling.
   */
  private Timer timer;

  /**
   * Indicates whether simulator has been stopped and must be reseted.
   */
  private boolean stopped;

  /**
   * The initial positions of the robots.
   */
  private TreeMap<UUID, int[]> initialpositions;

  /**
   * The output buffer for each VirtualPuck.
   */
  private TreeMap<UUID, byte[]> outputBuffer;

  /**
   * The constructor expects the map and all initial positions for the robots.
   * 
   * @param map
   *          The map which should be used.
   * @param initialPositions
   *          The initial positions of the robots.
   */
  public Simulator(GridMap exMap, TreeMap<UUID, int[]> initialRobotPositions) {
    map = exMap;
    initialpositions = initialRobotPositions;
    messageQueue = new ConcurrentLinkedQueue<IRequest>();
  }

  /**
   * Adds a new request message to the simulator message queue.
   * 
   * @param request
   *          The request to add.
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