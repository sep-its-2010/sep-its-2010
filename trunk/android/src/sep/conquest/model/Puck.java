package sep.conquest.model;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sep.conquest.model.requests.HelloRequest;
import sep.conquest.model.requests.Request;
import sep.conquest.model.requests.StatusUpdateRequest;
import sep.conquest.util.ConquestLog;

/**
 * Represents an abstract Puck robot.
 * 
 * @author Andreas Poxrucker
 * 
 */
public abstract class Puck implements IComClient, IRobot {

  /**
   * The length of a bluetooth message.
   */
  public static final int MSG_LENGTH = 32;

  /**
   * The position of the first type byte in a Bluetooth request message.
   */
  public static final int TYPE_FIRST_BYTE = 0;

  /**
   * The position of the second type byte in a Bluetooth request message.
   */
  public static final int TYPE_SECOND_BYTE = 1;

  /**
   * The position of the byte containing the node type on the current position.
   */
  public static final int NODE_STATUS_BYTE = 17;

  /**
   * The position of the byte containing the node type in a node hit message.
   */
  public static final int NODE_HIT_BYTE = 2;

  /**
   * The position of the first byte containing the led information.
   */
  public static final int LED_FIRST_BYTE = 2;

  /**
   * The position of the second byte containing the led information.
   */
  public static final int LED_SECOND_BYTE = 3;

  /**
   * Represents the byte-Code for the messageType RESPONSE_OK
   */
  public static final short RES_OK = (short) 0x81FF;

  /**
   * Represents the byte-Code for the messageType RESPONSE_STATUS
   */
  public static final short RES_STATUS = (short) 0x82FF;

  /**
   * Represents the byte-Code for the messageType RESPONSE_HITNODE
   */
  public static final short RES_HITNODE = (short) 0x83FF;

  /**
   * Represents the byte-Code for the messageType RESPONSE_COLLISION
   */
  public static final short RES_COLLISION = (short) 0x84FF;

  /**
   * Represents the byte-Code for the messageType RESPONSE_ABYSS
   */
  public static final short RES_ABYSS = (short) 0x85FF;

  /**
   * Represents the byte code for the message type RESPONSE_REJECT
   */
  public static final short RES_REJECT = (short) 0x86FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_MOVE
   */
  public static final short REQ_MOVE = (short) 0x04FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_RESET
   */
  public static final short REQ_RESET = (short) 0x01FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_SET_LED
   */
  public static final short REQ_SETLED = (short) 0x06FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_SET_SPEED
   */
  public static final short REQ_SETSPEED = (short) 0x05FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_STATUS
   */
  public static final short REQ_STATUS = (short) 0x02FF;

  /**
   * Represents the byte-Code for the messageType REQUEST_TURN
   */
  public static final short REQ_TURN = (short) 0x03FF;

  public static final int COLLISION_REACT_STEPS = 50;

  /**
   * Global unique id.
   */
  private UUID id;

  /**
   * Name to display in Activities.
   */
  private String name;

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
   * Counts the steps since the last collision to the needed number of steps.
   */
  private int collisionReactCount = 0;

  /**
   * The nodes with the last collision (until it's recovered).
   */
  private ConcurrentLinkedQueue<Integer> collisionNodes = new ConcurrentLinkedQueue<Integer>();
  
  /**
   * Indicates whether the collision have changed.
   */
  private boolean collisionsChanged = false;
  
  /**
   * Indicates whether the robot expects a message from the socket.
   */
  protected boolean expectMessage = false;

  /**
   * Constructor initializing ID, local map, own state, logic thread and
   * broadcast message Handler.
   * 
   * @param id
   *          The unique universally id of the Puck.
   * @param robotName
   *          The name of the robot.
   */
  public Puck(UUID id, String robotName) {
    // Set ID.
    this.id = id;

    // Set name.
    name = robotName;

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
   * Indicates whether a socket-message is expected.
   * 
   * @return True, if a message is expected, otherwise false.
   */
  public boolean isMessageExpected() {
    return expectMessage;
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
   *          The sender of the broadcast message.
   * @param request
   *          The message which has to be delivered.
   */
  public void deliver(IRequest request) {
    logicThread.addMessage(request);
  }

  /**
   * The method sends a given request via broadcast to the other members.
   * 
   * @param request
   *          The message to be sent.
   */
  public void broadcast(Request request) {
    ComManager.getInstance().broadcast(request);
  }

  /**
   * Change the behaviour due to the state change.
   * 
   * @param state
   */
  public void changeBehaviour(State state) {
    // Get current robot state.
    RobotStatus status = getRobotStatus().get(id);

    // If there is a real state transition, change state and behaviour and
    // announce changes via broadcast.
    if (state != status.getState()) {
      status.setState(state);
      logicThread.changeBehaviour(state);
      broadcast(new StatusUpdateRequest(id, new UUID[0], status));
    }
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
   * Puck.
   * 
   * @param command
   *          The command to sent to the Puck.
   */
  public void driveCommand(int command) {
    switch (command) {
    case 0:
      this.forward();
      break;
    case 2:
      this.turn();
      break;
    case -1:
      this.left();
      break;
    case 1:
      this.right();
      break;
    default:
      ConquestLog.addMessage(this, "Error in driveCommand()");
    }
  }

  /**
   * Returns the unique id of the robot.
   * 
   * @return The id of the robot.
   */
  public UUID getID() {
    return this.id;
  }

  /**
   * Return the name of the robot.
   * 
   * @return The name of the robot.
   */
  public String getName() {
    return name;
  }

  /**
   * This method sends a message in form of a byte-array via socket.
   * 
   * @param buffer
   *          The message that will be sent.
   */
  public abstract boolean writeSocket(byte[] buffer);

  /**
   * This method read if an incoming message has arrived at the socket.
   * 
   * @return Returns the message that was sent by an e-puck roboter.
   */
  public abstract byte[] readSocket();

  // TODO Add reset, status and set_led to irobot interface!
  /**
   * Sends forward command to Puck.
   */
  public void forward() {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_MOVE & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_MOVE >> 8) & 0xff);
    writeSocket(request);
  }

  /**
   * Sends left command to Puck.
   */
  public void left() {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_TURN & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_TURN >> 8) & 0xff);
    request[2] = (byte) -1;
    writeSocket(request);
  }

  /**
   * Sends right command to Puck.
   */
  public void right() {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_TURN & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_TURN >> 8) & 0xff);
    request[2] = (byte) 1;
    writeSocket(request);
  }

  /**
   * Sends speed setting request to Puck.
   */
  public void setSpeed(SpeedLevel level) {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_SETSPEED & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_SETSPEED >> 8) & 0xff);
    request[2] = (byte) level.getSpeed();
    writeSocket(request);
  }

  /**
   * Sends turn request to Puck.
   */
  public void turn() {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_TURN & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_TURN >> 8) & 0xff);
    request[2] = (byte) 2;
    writeSocket(request);
  }

  /**
   * Enables/Disables manual control of Puck.
   * 
   * @param enable
   *          True to activate manual control, false otherwise.
   */
  public void setControlled(boolean enable) {
    // Set robot controlled.
    controlled = enable;
  }

  /**
   * Sets the LEDs of the robot. The LEDs to set are enumerated from 0 to 9
   * (0-7: LED ring starting in camera direction, 8: ground LED, 9: front LED).
   * Each LED can be set be setting to corresponding bit in leds argument.
   * 
   * @param leds
   *          The number containing those bits set, where the corresponding LEDs
   *          should be set.
   */
  public void setLED(short leds) {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_SETLED & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_SETLED >> 8) & 0xff);
    request[LED_FIRST_BYTE] = (byte) (leds & 0xff);
    request[LED_SECOND_BYTE] = (byte) ((leds >> 8) & 0xff);
    writeSocket(request);
  }

  /**
   * Returns the current steps after a collision.
   * 
   * @return The number of steps after a collision.
   */
  public int getCollisionReactCount() {
    return collisionReactCount;
  }

  /**
   * Sets the number of steps after a collision.
   * 
   * @param collisionReactCount
   *          The number of steps.
   */
  public void setCollisionReactCount(int collisionReactCount) {
    this.collisionReactCount = collisionReactCount;
  }

   /**
    * Adds a node with the last collision. 
    * 
	* @param collisionNode the collisionNode to set
	*/
	public void addCollisionNode(Integer collisionNode) {
		this.collisionNodes.add(collisionNode);
	}
	/**
	 * Returns the number of collisions on the node.
	 * 
	 * @return the number of collisions.
	 */
	public int numberOfCollisions(Integer node) {
		int no = 0;
		for (Integer item: collisionNodes) {
			if (item.equals(node))
				no++;
		}
		return no;
	}
	
	/**
	 * Removes all elements from the collision-array.
	 */
	public void removeCollisions() {
		collisionNodes.clear();
	}
	
	/**
	 * Returns whether collisions exist.
	 * 
	 * @return Returns true, if collisions exist.
	 */
	public boolean collisionsExist() {
		return (!collisionNodes.isEmpty());
	}
	
	public boolean collisionChanged() {
		boolean tmp = collisionsChanged;
		collisionsChanged = false;
		return tmp;
	}

  /**
   * Sends status request message to Puck.
   */
  public void requestStatus() {
    byte[] request = new byte[MSG_LENGTH];
    request[TYPE_FIRST_BYTE] = (byte) (REQ_STATUS & 0xff);
    request[TYPE_SECOND_BYTE] = (byte) ((REQ_STATUS >> 8) & 0xff);
    writeSocket(request);
  }

  /**
   * Returns whether Puck is currently under manual control.
   * 
   * @return True, if Puck is controlled, false otherwise.
   */
  public boolean isControlled() {
    return controlled;
  }

  /**
   * Destroys the Puck. Shuts down the LogicThread.
   */
  public void destroy() {
    logicThread.destroy();
    executor.shutdown();
  }
}