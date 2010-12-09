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
  private final GridMap map;

  /**
   * Executes request handling.
   */
  private Timer timer;

  /**
   * The status of the robots.
   */
  private Map<UUID, SimRobotStatus> robotStates;

  /**
   * Indicates whether simulator has been stopped and must be reseted.
   */
  private boolean stopped;

  /**
   * The output buffer for each VirtualPuck.
   */
  private Map<UUID, byte[]> outputBuffer;

  /**
   * The input buffer for each VirtualPuck.
   */
  private Queue<IRequest> inputBuffer;

  /**
   * The first Handler to handle requests of VirtualPucks.
   */
  private Handler firstHandler;

  /**
   * Constructor requiring a map to explore and the initial positions of the
   * robots.
   */
  public Simulator(GridMap exMap, Map<UUID, int[]> initialPositions,
      Map<UUID, Orientation> initialOrientations) {

    // Check, if map or inital positions equal null.
    // If not, initialize simulator. Otherwise throw exception.
    if ((exMap != null) && (initialPositions != null)
        && (initialOrientations != null)) {
      map = exMap;
      inputBuffer = new ConcurrentLinkedQueue<IRequest>();
      outputBuffer = new TreeMap<UUID, byte[]>();
      robotStates = new TreeMap<UUID, SimRobotStatus>();
      firstHandler = HandlerFactory.getSimMsgChain(this);

      // Simulator needs as many output buffers as initial positions.
      Set<UUID> ids = initialPositions.keySet();

      // Add new output buffer and robot state for each initial position.
      // If one of the ids equals null, throw exception.
      for (UUID id : ids) {
        if (id != null) {
          outputBuffer.put(id, new byte[0]);
          int[] pos = initialPositions.get(id);

          // Check, if initial position is invalid.
          if ((pos.length != 2) || (map.getNode(pos[0], pos[1]) == null)) {
            throw new IllegalArgumentException(
                "Illegal initial position passed");
          }
          Orientation or = initialOrientations.get(id);

          // Check, if initial orientation is invalid.
          if (or == null) {
            throw new IllegalArgumentException(
                "Illegal initial orientation passed");
          }
          robotStates.put(id, new SimRobotStatus(pos, or));
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
    inputBuffer.add(request);
  }

  /**
   * Resets a robot to its initial state (position + orientation).
   * 
   * @param id
   *          The id of the robot to reset.
   */
  public void resetRobotState(UUID id) {
    if (robotStates.containsKey(id)) {
      robotStates.get(id).reset();
    } else {
      throw new IllegalStateException("Asked to reset an unknown id");
    }
  }

  /**
   * Returns the type of node at a certain position
   * 
   * @param position
   *          The position where the type of node is requested.
   * @return The type of node at position.
   */
  public NodeType getNodeType(int[] position) {
    if (position == null) {
      throw new IllegalStateException("Invalid position");
    } else {
      GraphNode node = map.getNode(position[0], position[1]);

      // If node equals null, then invalid position has been passed.
      if (node != null) {
        return node.getNodeType();
      } else {
        throw new IllegalStateException(
            "Asked for type of not at invalid position");
      }
    }
  }

  /**
   * Returns the current position of a robot.
   * 
   * @param id
   *          The id of the robot whose position is requested.
   * @return The requested position.
   */
  public int[] getPosition(UUID id) {
    if (robotStates.containsKey(id)) {
      return robotStates.get(id).getPosition();
    } else {
      throw new IllegalStateException("Asked for position of unknown id");
    }
  }

  /**
   * Sets the position of a robot.
   * 
   * @param id
   *          The id of the robot whose position is to set.
   * @param newPosition
   *          The new position of the robot.
   */
  public void setPosition(UUID id, int[] newPosition) {
    if (robotStates.containsKey(id)) {
      robotStates.get(id).setPosition(newPosition);
    } else {
      throw new IllegalStateException("Asked to set position of unknown id");
    }
  }

  /**
   * Returns the current orientation of a robot.
   * 
   * @param id
   *          The id of the robot whose orientation is requested.
   * @return The requested orientation.
   */
  public Orientation getOrientation(UUID id) {
    if (robotStates.containsKey(id)) {
      return robotStates.get(id).getOrientation();
    } else {
      throw new IllegalStateException("Asked for orientation of unknown id");
    }
  }

  /**
   * Sets the orientation of a robot.
   * 
   * @param id
   *          The id of the robot whose orientation is to set.
   * @param newOrientation
   *          The new orientation of the robot.
   */
  public void setOrientation(UUID id, Orientation newOrientation) {
    if (robotStates.containsKey(id)) {
      robotStates.get(id).setOrientation(newOrientation);
    } else {
      throw new IllegalStateException("Asked to set orientation of unknown id");
    }
  }

  public int getSystemUpTime(UUID id) {
    if (robotStates.containsKey(id)) {
      return robotStates.get(id).getSystemUpTime();
    } else {
      throw new IllegalStateException("Asked for system up time of unknown id");
    }
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
      byte[] msg = outputBuffer.get(id);
      outputBuffer.put(id, new byte[0]);
      return msg;
    } else {
      throw new IllegalStateException("Unknown id asked to read output buffer");
    }
  }

  /**
   * Writes a message of 32 bytes on the output buffer associated with the id.
   * If there is no output buffer for id or message length is not 32 bytes an
   * exception is thrown.
   * 
   * @param id
   *          The id to whose output buffer the message should be written.
   * @param msg
   *          The message to write on the output buffer.
   */
  public void writeBuffer(UUID id, byte[] msg) {
    // Check, if there is an output buffer for id and if length of message
    // equals 32 byte. It not, throw exception.
    if (outputBuffer.containsKey(id)) {
      if (msg.length == 32) {
        outputBuffer.put(id, msg);
      } else {
        throw new IllegalArgumentException("Message length is not equal to 32");
      }
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
    IRequest request = inputBuffer.poll();
    if (request != null)
    	firstHandler.handleRequest(request);
  }

  /**
   * Resets the simulator.
   * 
   * Resets the request message queue, the output buffer and the robot states.
   */
  public void reset() {
    Set<UUID> ids = outputBuffer.keySet();
    inputBuffer.clear();

    // Reset output buffers by setting arrays of length zero and reset robot
    // states.
    for (UUID id : ids) {
      outputBuffer.put(id, new byte[0]);
      robotStates.get(id).reset();
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