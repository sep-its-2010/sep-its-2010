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
}
