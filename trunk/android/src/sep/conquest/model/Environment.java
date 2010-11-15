package sep.conquest.model;

import java.util.Observable;


/**
 * The class Environment represents the model corresponding to the Model-View-
 * Controller-pattern. It is used as interface between robots and the
 * graphical user interface. Every <code>Activity</code> has to register in
 * order to be notified. 
 * 
 * @author Ande
 */
public class Environment extends Observable implements IComClient {
    
    // local declarations and definitions
    private static final Environment INSTANCE = new Environment();
    private ComManager comManager;
    
    /**
     * The private constructor to realize the singleton pattern. It gets a
     * a reference to the communication-manager for broadcast-communication.
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
     * do this a new request object will be created.
     * 
     * @param ID the ID of the robot.
     * @param command the drive command to send.
     */
    public void driveCommand(String ID, Drive command) {
        DriveRequest driveReq = new DriveRequest(ID, command);
        comManager.broadcast(this, driveReq);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IComClient#deliver(sep.conquest.model.IComClient)
     */
    public void deliver(IComClient sender, IRequest request) {
        // TODO request-handler

    }  
}
