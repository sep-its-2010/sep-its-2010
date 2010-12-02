package sep.conquest.model;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;

/**
 * The Simulator class represents a virtual socket for a proxy-robot within the
 * e-puck-conquest application. It is used to simulate messages for the
 * navigation-algorithms. Therefore a internal stored map is used.
 * 
 * @author Andreas Poxrucker
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
   * The initial positions of the robots.
   */
  private Map<UUID, int[]> initialPositions;
  
  /**
   * The positions of the robots.
   */
  private Map<UUID, int[]> positions;

  /**
   * Indicates whether simulator has been stopped and must be reseted.
   */
  private boolean stopped;

  /**
   * The output buffer for each VirtualPuck.
   */
  private Map<UUID, byte[]> outputBuffer;

  /**
   * The first Handler to handle requests of VirtualPucks.
   */
  private Handler firstHandler;

  /**
   * Constructor requiring a map to explore and the initial positions of the
   * robots.
   */
  public Simulator(GridMap map, Map<UUID, int[]> initialPositions) {

    // Check, if map or inital positions equal null.
    // If not, initialize simulator. Otherwise throw exception.
    if ((map != null) && (initialPositions != null)) {
      this.map = map;
      this.initialPositions = initialPositions;
      messageQueue = new ConcurrentLinkedQueue<IRequest>();
      outputBuffer = new TreeMap<UUID, byte[]>();
      positions = new TreeMap<UUID, int[]>();
      firstHandler = HandlerFactory.getSimMsgChain(this);

      // Simulator needs as many output buffers as initial positions.
      Set<UUID> ids = initialPositions.keySet();
      
      // Add new output buffer for each initial position. If one of the ids
      // equals null, throw exception.
      for (UUID id : ids) {
        if (id != null) {
          outputBuffer.put(id, new byte[0]);
          positions.put(id, initialPositions.get(id).clone());
        } else {
          throw new IllegalStateException("UUID equals null");
        }
      }
    } else {
      throw new IllegalArgumentException("Map or position array equals null");
    }
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
   * Returns the output buffer for a certain UUID.
   * 
   * @param The
   *          id of the registered robot.
   * @return The output buffer byte array of length zero, if there is no
   *         message, or of length 32 if there is a message.
   */
  public byte[] readBuffer(UUID id) {
    if (outputBuffer.containsKey(id)) {
      return outputBuffer.get(id);
    } else {
      throw new IllegalStateException("Unknown id asked to read output buffer");
    }
  }
  
  public int[] getPosition(UUID id) {
    return null;
  }
  
  public void writeBuffer(UUID id, byte[] msg) {
    // Check, if there is an output buffer for id and if length of message
    // equals 32 byte. It not, throw exception.
    if (outputBuffer.containsKey(id) && (msg.length == 32)) {
      outputBuffer.put(id, msg);
    } else {
      throw new IllegalStateException("Unknown id asked to write output buffer");
    }
  }

  /**
   * Starts the execution of the simulator.
   */
  public void start() {
    // If simulator was previously paused and not stopped, create new timer and
    // resume to step by step execution.
    // Else reset simulator and start new execution.
    if (!stopped) {
      timer = new Timer();
      timer.scheduleAtFixedRate(new SimulatorTask(), 0, PERIOD);
    } else {
      reset();
      timer = new Timer();
      timer.schedule(new SimulatorTask(), 0, PERIOD);
    }
  }

  /**
   * Stops the execution of the simulator.
   */
  public void stop() {
    timer.cancel();

    // Next start action will cause simulator reset.
    stopped = true;
  }

  /**
   * Pauses the execution of the simulator.
   */
  public void pause() {
    timer.cancel();

    // Next start action will not cause simulator reset.
    stopped = false;
  }

  /**
   * Executes the next step.
   */
  public void step() {
    // If request message queue is not empty, poll first message and pass it
    // to the first Handler.
    if (!messageQueue.isEmpty()) {
      IRequest request = messageQueue.poll();
      firstHandler.handleRequest(request);
    }
  }

  /**
   * Resets the simulator.
   * 
   * Resets the request message queue and the output buffer.
   */
  public void reset() {
    // Reset message queue
    messageQueue = new ConcurrentLinkedQueue<IRequest>();

    // Reset output buffers by setting arrays of length zero.
    Set<UUID> ids = outputBuffer.keySet();

    for (UUID id : ids) {
      outputBuffer.put(id, new byte[0]);
      positions.put(id, initialPositions.get(id).clone());
    }
  }

  /**
   * Class SimulatorTask used to execute the simulator step by step controlled
   * by a timer.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private class SimulatorTask extends TimerTask {

    /**
     * Executes one step of the simulation.
     */
    @Override
    public void run() {
      step();
    }
  }
}