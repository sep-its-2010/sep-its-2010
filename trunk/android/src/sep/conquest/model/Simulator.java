package sep.conquest.model;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.util.Utility;

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
  private Map<UUID, SimRobot> robots;

  /**
   * Contains the ids of all robots and is used to iterate over them.
   */
  private UUID[] robotIds;

  /**
   * The iteration index.
   */
  private int index;

  /**
   * Indicates whether simulator has been stopped and must be reseted.
   */
  private boolean stopped;

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
      robots = new TreeMap<UUID, SimRobot>();
      firstHandler = HandlerFactory.getSimMsgChain(this);
      robotIds = new UUID[initialPositions.size()];

      // Simulator needs as many output buffers as initial positions.
      Set<UUID> ids = initialPositions.keySet();

      // Add new output buffer and robot state for each initial position.
      // If one of the ids equals null, throw exception.
      int i = 0;
      for (UUID id : ids) {
        if (id != null) {
          int[] pos = initialPositions.get(id);
          robotIds[i] = id;
          i++;
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
          robots.put(id, new SimRobot(pos, or));
        } else {
          throw new IllegalArgumentException("UUID equals null");
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
    if (request != null) {
      UUID sender = request.getSender();
      robots.get(sender).addRequest(request);
    } else {
      throw new IllegalArgumentException("Request must not equal null");
    }
  }

  public void clearRequest(UUID id) {
    if ((id != null) && robots.containsKey(id)) {
      robots.get(id).clearRequest();
    } else {
      throw new IllegalArgumentException(
          "Asked to clear input buffer of invalid id");
    }
  }

  /**
   * Resets a robot to its initial state (position + orientation).
   * 
   * @param id
   *          The id of the robot to reset.
   */
  public void resetRobotState(UUID id) {
    if ((id != null) && robots.containsKey(id)) {
      robots.get(id).reset();
    } else {
      throw new IllegalArgumentException("Asked to reset an unknown id");
    }
  }

  /**
   * Returns the type of node at a certain position
   * 
   * @param position
   *          The position where the type of node is requested.
   * @return The type of node at position.
   */
  public NodeType getNodeType(int x, int y) {
    GraphNode node = map.getNode(x, y);

    // If node equals null, then invalid position has been passed.
    if (node != null) {
      return node.getNodeType();
    } else {
      throw new IllegalArgumentException(
          "Asked for type of node at invalid position");
    }
  }

  /**
   * Returns the current position of a robot.
   * 
   * @param id
   *          The robot whose position is requested.
   * @return The requested position.
   */
  public int[] getPosition(UUID id) {
    if ((id != null) && robots.containsKey(id)) {
      return robots.get(id).getPosition();
    } else {
      throw new IllegalArgumentException("Asked for position of unknown id");
    }
  }

  /**
   * Sets the position of a robot.
   * 
   * @param id
   *          The robot whose position is to set.
   * @param newPosition
   *          The new position of the robot.
   */
  public void setPosition(UUID id, int[] newPos) {
    // Validate id and the new position.
    if ((id != null) && robots.containsKey(id) || (newPos.length != 2)
        || (map.getNode(newPos[0], newPos[1]) == null)) {
      robots.get(id).setPosition(newPos);
    } else {
      throw new IllegalArgumentException("Asked to set position of unknown id");
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
    if ((id != null) && robots.containsKey(id)) {
      return robots.get(id).getOrientation();
    } else {
      throw new IllegalArgumentException("Asked for orientation of unknown id");
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
    if ((id != null) && robots.containsKey(id) && (newOrientation != null)) {
      robots.get(id).setOrientation(newOrientation);
    } else {
      throw new IllegalArgumentException(
          "Asked to set orientation of unknown id");
    }
  }

  /**
   * Moves a robot one step further.
   * 
   * If movement ends on coordinates divisible by three, a HitNode message is
   * written on the corresponding output buffer.
   * 
   * If movement can not be accomplished, a collision message is written on the
   * corresponding output buffer.
   * 
   * @param id
   *          The id of the robot that should be moved.
   */
  public void moveRobot(UUID id) {
    // Get current position and orientation of the sender robot.
    int[] pos = getPosition(id);
    int newX = pos[0];
    int newY = pos[1];
    Orientation ori = getOrientation(id);
    int turnCount = 0;

    // Update current position of the robot.
    switch (ori) {
    case UP:
      newY--;
      turnCount = 0;
      break;
    case DOWN:
      newY++;
      turnCount = 2;
      break;
    case LEFT:
      newX--;
      turnCount = 1;
      break;
    case RIGHT:
      newX++;
      turnCount = 3;
      break;
    }

    // Check, if new position can not be attended because of a collision.
    if (collision(newX, newY)) {
      collide(id);
      setOrientation(id, Orientation
          .getTurnedOrientation(2, getOrientation(id)));
    } else {
      // Assign new position.
      pos[0] = newX;
      pos[1] = newY;

      // If new position is on a node (i.e. coordinates are divisible by three)
      // write HitNode message and set moving state false.
      if ((newX % 3 == 0) && (newY % 3 == 0)) {
        // Message that is written on the output buffer
        byte[] response = new byte[32];

        // If number of collisions during one move is divisible by two
        // robot has attended its intended node.
        // Otherwise robot has returned to its initial position.
        if (getNumberOfCollisions(id) % 2 == 0) {
          // Get NodeType of new position.
          NodeType node = getNodeType(pos[0] / 3, pos[1] / 3);

          // Turns the corners and T-crosses, so they can be added to the map
          NodeType finalNodeType = Utility.turnAround(turnCount, node);

          // Write message type "node hit" to first two bytes.
          response[0] = (byte) (Puck.RES_HITNODE & 0xFF);
          response[1] = (byte) ((Puck.RES_HITNODE >> 8) & 0xFF);

          // Write node type to third byte.
          response[2] = (byte) finalNodeType.ordinal();
        } else {
          // Write message type "collision" to first two bytes.
          response[0] = (byte) (Puck.RES_COLLISION & 0xFF);
          response[1] = (byte) ((Puck.RES_COLLISION >> 8) & 0xFF);
        }
        writeBuffer(id, response);

        // Set moving state false.
        setMoving(id, false);
      }
    }
  }

  /**
   * Returns whether there will be a collision on position x,y.
   * 
   * @param x
   *          The x coordinate of the position.
   * @param y
   *          The y coordinate of the position.
   * 
   * @return True, if there is a collision, false otherwise.
   */
  private boolean collision(int x, int y) {
    for (UUID robId : robotIds) {
      SimRobot rob = robots.get(robId);
      int[] pos = rob.getPosition();
      if (pos[0] == x && pos[1] == y) {
        return true;
      }
    }
    return false;
  }

  /**
   * Increases the number of collisions that a certain robot suffers while
   * moving from one node to the next.
   * 
   * @param id
   *          The robot that collided.
   */
  public void collide(UUID id) {
    if ((id != null) && robots.containsKey(id)) {
      robots.get(id).collide();
    } else {
      throw new IllegalArgumentException("Asked to set collsions of illegal id");
    }
  }

  /**
   * Returns the number of collision a robot has suffered while moving from a
   * node to the next.
   * 
   * @param id
   *          The robot of interest.
   * 
   * @return The number of collisions.
   */
  public int getNumberOfCollisions(UUID id) {
    if ((id != null) && robots.containsKey(id)) {
      return robots.get(id).getNumberOfCollisions();
    } else {
      throw new IllegalArgumentException("Asked to get collsions of illegal id");
    }
  }

  /**
   * Returns the system up time of a certain robot.
   * 
   * @param id
   *          The id of the robot.
   * @return The system up time of the robot.
   */
  public int getSystemUpTime(UUID id) {
    if (robots.containsKey(id)) {
      return robots.get(id).getSystemUpTime();
    } else {
      throw new IllegalArgumentException(
          "Asked for system up time of unknown id");
    }
  }

  /**
   * Sets the moving state of a certain robot.
   * 
   * @param id
   *          The id of the robot.
   * @param moving
   *          True, if robot is moving, false otherwise.
   */
  public void setMoving(UUID id, boolean moving) {
    if (robots.containsKey(id)) {
      robots.get(id).setMoving(moving);
    } else {
      throw new IllegalArgumentException(
          "Asked to set moving state of unknown id");
    }
  }

  /**
   * Returns the output buffer for a certain UUID.
   * 
   * @param id
   *          The registered robot.
   * @return The output buffer byte array of length zero, if there is no
   *         message, or of length 32 if there is a message.
   */
  public byte[] readBuffer(UUID id) {
    if (robots.containsKey(id)) {
      byte[] msg = robots.get(id).readBuffer();
      return msg;
    } else {
      throw new IllegalArgumentException(
          "Unknown id asked to read output buffer");
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
    if (robots.containsKey(id)) {
      if (msg.length == 32) {
        robots.get(id).writeBuffer(msg);
      } else {
        throw new IllegalArgumentException("Message length is not equal to 32");
      }
    } else {
      throw new IllegalArgumentException(
          "Unknown id asked to write output buffer");
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
    // Pick next id and get the corresponding robot.
    UUID id = robotIds[index];
    SimRobot robot = robots.get(id);

    // If robot is currently moving, make one drive step.
    // If robot is not moving, check input buffer for new request message.
    if (robot.isMoving()) {
      moveRobot(id);
    } else if (robot.hasRequest()) {
      IRequest request = robot.getRequest();

      // Handle message
      if (!firstHandler.handleRequest(request)) {
        throw new IllegalArgumentException(
            "Could not handle message. Message type unknown");
      }
    }
    // Increase index.
    index = (index + 1) % robotIds.length;
  }

  /**
   * Resets the simulator.
   * 
   * Resets the request message queue, the output buffer and the robot states.
   */
  private void reset() {
    Set<UUID> ids = robots.keySet();

    // Reset output buffers by setting arrays of length zero and reset robot
    // states.
    for (UUID id : ids) {
      resetRobotState(id);
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