package sep.conquest.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sep.conquest.model.requests.HelloRequest;
import sep.conquest.model.requests.MessageType;
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
  
  public static final int MSG_LENGTH = 32;
  
  public static final int TYPE_FIRST_BYTE = 0;
  public static final int TYPE_SECOND_BYTE = 1;
  public static final int NODE_BYTE = 17;

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
   * Represents the byte-Code for the messageType REQUEST_MOVE
   */
  public static final short REQ_MOVE = (short) 0x04FF;
	/**
	 * Indicates whether an OK-message was received.
	 */
	private boolean okRcvd = false;
	

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

  /**
   * The length of a bluetooth message.
   */
  protected static final int MSGLENGTH = 32;

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
   * Indicates whether the robot expects a message from the socket.
   */
  protected boolean expectMessage = false;

  /**
   * The bluetooth message.
   */
  protected byte[] btMessage = new byte[MSGLENGTH];

  /**
   * The length of the current message in buffer.
   */
  protected int btMessageLen = 0;

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
	StackTraceElement[] trace =  Thread.currentThread().getStackTrace();
	ConquestLog.addMessage(this, trace[trace.length-1].toString());
	ConquestLog.addMessage(this, trace[trace.length-2].toString());
	ConquestLog.addMessage(this, trace[trace.length-3].toString());
	ConquestLog.addMessage(this, trace[trace.length-4].toString());
	ConquestLog.addMessage(this, trace[trace.length-5].toString());
	  
    RobotStatus status = getRobotStatus().get(id);
    if (state != status.getState()) {
      status.setState(state);
      logicThread.changeBehaviour(state);
      StatusUpdateRequest req = new StatusUpdateRequest(id, null, status);
      broadcast(req);
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
   * puck.
   * 
   * @param direction
   */
  public void driveCommand(int turns) {
    switch (turns) {
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

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IComClient#getID()
   */
  public UUID getID() {
    return this.id;
  }

  public String getName() {
    return name;
  }

  /**
   * This method sends a message in form of a byte-array via socket.
   * 
   * @param buffer
   *          The message that will be sent.
   */
  public boolean writeSocket(byte[] buffer) {
    if (!expectMessage) {
      expectMessage = true;
      btMessageLen = 0;
      btMessage = new byte[MSGLENGTH];
      return true;
    } else
      return false;
  }

  /**
   * This method read if an incoming message has arrived at the socket.
   * 
   * @return Returns the message that was sent by an e-puck roboter.
   */
  public abstract byte[] readSocket();

  // TODO Add reset, status and set_led to irobot interface!
  public void forward() {
    byte[] request = new byte[32];
    request[0] = (byte) (MessageType.REQUEST_MOVE.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_MOVE.getTypeCode() >> 8) & 0xff);

    writeSocket(request);
  }

  public void left() {
    byte[] request = new byte[32];
    request[0] = (byte) (MessageType.REQUEST_TURN.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_TURN.getTypeCode() >> 8) & 0xff);
    request[2] = (byte) -1;

    writeSocket(request);
  }

  public void right() {
    byte[] request = new byte[32];
    request[0] = (byte) (MessageType.REQUEST_TURN.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_TURN.getTypeCode() >> 8) & 0xff);
    request[2] = (byte) 1;

    writeSocket(request);
  }

  public void setSpeed(SpeedLevel level) {
    byte[] request = new byte[32];
    request[0] = (byte) (MessageType.REQUEST_SET_SPEED.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_SET_SPEED.getTypeCode() >> 8) & 0xff);
    request[2] = (byte) level.getSpeed();

    writeSocket(request);
  }

  public void turn() {
    byte[] request = new byte[32];
    request[0] = (byte) (MessageType.REQUEST_TURN.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_TURN.getTypeCode() >> 8) & 0xff);
    request[2] = (byte) 2;

    writeSocket(request);
  }

  public void setControlled(boolean enabled) {
    byte[] request = new byte[32];
    controlled = enabled;
    request[0] = (byte) (MessageType.REQUEST_SET_LED.getTypeCode() & 0xff);
    request[1] = (byte) ((MessageType.REQUEST_SET_LED.getTypeCode() >> 8) & 0xff);
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

	/**
	 * Sets the ok-received flag.
	 * 
	 * @param okRcvd
	 */
	public void setOkRcvd(boolean okRcvd) {
		this.okRcvd = okRcvd;
	}

	/**
	 * Indicates whether an ok-message was received.
	 * 
	 * @return
	 */
	public boolean isOkRcvd() {
		return okRcvd;
	}
	
	/**
	 * Destroys the Puck.
	 */
	public void destroy() { 
		executor.shutdown();
		try {
			this.finalize();
		} catch (Throwable e) {
			throw new IllegalStateException("Error! Couldn't destroy Puck " + name);
		}
	}
}
