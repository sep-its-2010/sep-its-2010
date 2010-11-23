package sep.conquest.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
  private UUID id;

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
  * The thread executor for the logic-thread.
  */
  private ExecutorService executor;

  /**
   * First Handler to handle broadcast messages.
   */
   private Handler firstBCHandler;

  /**
   * Constructor initializing ID, local map, own state, logic thread and
   * broadcast message Handler.
   * 
   * @param id The unique universally id of the Puck.
   */
  public Puck(UUID id) {
    
    // Set ID.
    this.id = id;

    // Initialize map.
    map = new GridMap();
    
    // Initialize state map and add own initial state 'Localizing'.
    states = new TreeMap<UUID, RobotStatus>();
    states.put(id, new RobotStatus());
    
    // start the logic thread
    executor = Executors.newSingleThreadExecutor();
    executor.execute(new LogicThread(this));
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
   * The method delivers a message from a specific sender.
   * 
   * @param sender The sender of the broadcast message.
   * @param request The message which has to be delivered.
   */
  public void deliver(IComClient sender, IRequest request){
	  
  }
  
  /* (non-Javadoc)
  * @see sep.conquest.model.IComClient#getID()
  */
  public UUID getID() {
	  return this.id;
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
