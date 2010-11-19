package sep.conquest.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Represents an abstract Puck robot.
 * 
 * @author Andreas Poxrucker
 * 
 */
public abstract class Puck implements IComClient {

  /**
   * Global unique id.
   */
  private UUID ID;

  /**
   * Local map.
   */
  private GridMap map;

  /**
   * Saves status for each participating Puck.
   */
  private Map<UUID, RobotStatus> states;

  /**
   * Executes logic and behaviour.
   */
  private Thread logicThread;

  /**
   * First Handler to handle broadcast messages.
   */
  private Handler firstBCHandler;

  /**
   * Constructor initializing ID, local map, own state, logic thread and
   * broadcast message Handler.
   * 
   * @param ID The unique universally id of the Puck.
   */
  public Puck(UUID ID) {
    
    // Set ID.
    this.ID = ID;

    // Initialize map.
    map = new GridMap();
    
    // Initialize state map and add own initial state 'Localizing'.
    states = new TreeMap<UUID, RobotStatus>();
    //RobotStatus ownState = new RobotStatus();
    //states.put(ID, ownState);
    
    // firstBCHandler = new PositionUpdateHandler();
  }
  
  /**
   * The method delivers a message from a specific sender.
   * 
   * @param sender The sender of the broadcast message.
   * @param request The message which has to be delivered.
   */
  public void deliver(IComClient sender, IRequest request){
	  
  }
  
  public UUID getID() {
		return this.ID;
	}
  
  /**
   * This method sends a message in form of a byte-array via socket.
   * @param buffer The message that will be sent.
   */
  public abstract void writeSocket(byte[] buffer);
  
  /**
   * This method read if an incoming message has arrived at the socket.
   * @return Returns the message that was sent by an e-puck roboter.
   */
  public abstract byte[] readSocket();

}
