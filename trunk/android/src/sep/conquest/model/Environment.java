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
     * The static instance to implement the singleton pattern.
     */
    private static final Environment INSTANCE = new Environment();
    
    /**
     * The private constructor to realize the singleton pattern.
     */
    private Environment(){ }
    
    /**
     * The getInstance method returns the singleton object of the Environment
     * class.
     * 
     * @return the singleton instance of Environment.
     */
    public static Environment getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initiate a drive-command to a specific robot by broadcast. In order to
     * do this a new request object will be created and sent.
     * 
     * @param ID The ID of the robot.
     * @param command The drive command to send.
     */
    public void driveCommand(UUID ID, Drive command) {
    	ComManager comManager = ComManager.getInstance();
        DriveRequest driveReq = new DriveRequest(ID, command);
        comManager.broadcast(this, driveReq);
    }
    
    /**
     * Sets the speed of a specific robot by broadcast. In order to do this a
     * new request object will be created and sent.
     * 
     * @param ID The ID of the robot.
     * @param speed The speed level of the robots.
     */
    public void setSpeed(UUID ID, SpeedLevel speed) { }
    
    /**
     * Enables (or disables) a specific robot to be controlled by the user.
     * 
     * @param ID The UUID of the specific robot.
     * @param cotnrolled <code>true</code> if the robot should be controlled,
     *  otherwise false.
     */
    public void setControlled(UUID ID, boolean enabled) { }
    

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
