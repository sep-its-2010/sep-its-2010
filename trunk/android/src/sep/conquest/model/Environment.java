package sep.conquest.model;

import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.model.requests.ControlledRequest;
import sep.conquest.model.requests.DriveRequest;
import sep.conquest.model.requests.SpeedRequest;

/**
 * The class Environment represents the model corresponding to the Model-View-
 * Controller-pattern. It is used as interface between robots and the graphical
 * user interface. Every <code>Activity</code> has to register in order to be
 * notified.
 * 
 * @author Andreas Wilhelm
 */
public class Environment extends Observable implements IComClient {

  /**
   * The static instance to implement the singleton pattern.
   */
  private static Environment INSTANCE = new Environment();

  /**
   * The local id of the Environment.
   */
  private UUID id;

  /**
   * The communication manager to realize broadcast-communication.
   */
  private IComMan comManager;

  /**
   * The global map.
   */
  private GridMap gridMap;

  /**
   * The update message to notify every observer who's registered.
   */
  private ConquestUpdate update;

  /**
   * The handler for incoming broadcast-messages.
   */
  private Handler handler;

  /**
   * The private constructor to realize the singleton pattern. It will create a
   * local id and registers itself at the communication manager. Additionally it
   * creates a new GridMap that contains the global map information.
   */
  private Environment() {
    this.id = UUID.randomUUID();
    comManager = ComManager.getInstance();
    comManager.addClient(id, this);
    gridMap = new GridMap();
    update = new ConquestUpdate(gridMap.getMapAsList(),
        gridMap.getMapBorders(), new TreeMap<UUID, RobotStatus>());
    handler = HandlerFactory.getEnvironmentBCChain(this);
  }

  /**
   * The getInstance method returns the singleton object of the Environment
   * class.
   * 
   * @return The singleton instance of Environment.
   */
  public static Environment getInstance() {
    return INSTANCE;
  }

  /**
   * Initiate a drive-command to a specific robot by broadcast. In order to do
   * this a new request object will be created and sent.
   * 
   * @param id
   *          The ID of the robot.
   * @param command
   *          The drive command to send.
   */
  public void driveCommand(UUID id, Orientation command) {
    UUID[] receiver = { id };
    DriveRequest driveReq = new DriveRequest(this.getID(), receiver, command);
    comManager.broadcast(driveReq);
  }

  /**
   * Sets the speed of a specific robot by broadcast. In order to do this a new
   * request object will be created and sent.
   * 
   * @param id
   *          The ID of the robot.
   * @param speed
   *          The speed level of the robots.
   */
  public void setSpeed(UUID id, SpeedLevel speed) {
    UUID[] receiver = { id };
    SpeedRequest speedReq = new SpeedRequest(getID(), receiver, speed);
    comManager.broadcast(speedReq);
  }

  /**
   * Enables (or disables) a specific robot to be controlled by the user.
   * 
   * @param id
   *          The UUID of the specific robot.
   * @param cotnrolled
   *          <code>true</code> if the robot should be controlled, otherwise
   *          false.
   */
  public void setControlled(UUID id, boolean enabled) {
    UUID[] receiver = { id };
    ControlledRequest controlledReq = new ControlledRequest(getID(), receiver,
        enabled);
    comManager.broadcast(controlledReq);
  }

  /**
   * Returns the global map.
   * 
   * @return The global map.
   */
  public GridMap getMap() {
    return gridMap;
  }

  /**
   * Returns the current status of every robot.
   * 
   * @return A ConquestUpdate containing information about each robot.
   */
  public ConquestUpdate getStatus() {
    return update;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IComClient#getID()
   */
  public UUID getID() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IComClient#deliver(sep.conquest.model.IComClient)
   */
  public void deliver(IRequest request) {
    handler.handleRequest(request);
    sendUpdate();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observable#addObserver(java.util.Observer)
   */
  @Override
  public void addObserver(Observer observer) {
    super.addObserver(observer);
    sendUpdate();
  }

  /**
   * Sends an update message to the observers.
   */
  public void sendUpdate() {
    setChanged();
    synchronized (update) {
      update.setMapList(gridMap.getMapAsList());
      update.setBorders(gridMap.getMapBorders());
    }
    notifyObservers(update.clone());
  }

  /**
   * Loads a given map.
   * 
   * @param gridMap
   *          The map that should been loaded.
   */
  public void loadMap(GridMap gridMap) {
    this.gridMap = gridMap;
  }

  /**
   * Destroys all running LogicThreads and finally resets itself.
   */
  public void destroy() {
    comManager.removeClient(id);
    IComClient[] clients = comManager.getClients();

    // Iterate over all clients and destroy them.
    for (IComClient client : clients) {
      client.destroy();
      comManager.removeClient(client.getID());
    }
    INSTANCE = new Environment();
  }
}
