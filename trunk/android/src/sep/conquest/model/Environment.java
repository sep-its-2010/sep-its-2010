package sep.conquest.model;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * The class Environment represents the model corresponding to the Model-View-
 * Controller-pattern. It is used as interface between robots and the
 * graphical user interface. Every <code>Activity</code> has to register in
 * order to be notified. 
 * 
 * @author Andreas Wilhelm
 */
public class Environment extends Observable implements IComClient {
	
	/**
	 * The local id of the Environment.
	 */
	private UUID id;
    
    /**
     * The static instance to implement the singleton pattern.
     */
    private static final Environment INSTANCE = new Environment();
    
    /**
     * The private constructor to realize the singleton pattern. It will create
     * a local id and registers itself at the communication manager
     */
    private Environment(){
    	this.id = UUID.randomUUID();
    	// TODO register at the communication manager
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
     * Initiate a drive-command to a specific robot by broadcast. In order to
     * do this a new request object will be created and sent.
     * 
     * @param id The ID of the robot.
     * @param command The drive command to send.
     */
    public void driveCommand(UUID id, Drive command) {
    	UUID[] receiver = {id};    	
    	ComManager comManager = ComManager.getInstance();
        DriveRequest driveReq = new DriveRequest(id, receiver, command);
        comManager.broadcast(this, driveReq);
    }
    
    /**
     * Sets the speed of a specific robot by broadcast. In order to do this a
     * new request object will be created and sent.
     * 
     * @param id The ID of the robot.
     * @param speed The speed level of the robots.
     */
    public void setSpeed(UUID id, SpeedLevel speed) { }
    
    /**
     * Enables (or disables) a specific robot to be controlled by the user.
     * 
     * @param id The UUID of the specific robot.
     * @param cotnrolled <code>true</code> if the robot should be controlled,
     *  otherwise false.
     */
    public void setControlled(UUID id, boolean enabled) { }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IComClient#getID()
     */
    public UUID getID() {
    	return id;
    }
    

    /* (non-Javadoc)
     * @see sep.conquest.model.IComClient#deliver(sep.conquest.model.IComClient)
     */
    public void deliver(IComClient sender, IRequest request) {
        // TODO request-handler
    }  
    
    /* (non-Javadoc)
	 * @see java.util.Observable#addObserver(java.util.Observer)
	 */
	@Override
	public void addObserver(Observer observer) {
		super.addObserver(observer);
		notifyObservers();
	}
}
