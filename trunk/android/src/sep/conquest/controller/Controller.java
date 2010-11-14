package sep.conquest.controller;

import sep.conquest.model.Drive;
import sep.conquest.model.Environment;

/**
 * The Controller class represents the controller corresponding to the model-
 * view-controller pattern. It is a unified interface between each
 * <code>Activity
 * 
 * @author Andreas Wilhelm
 */
public class Controller {

    // local declarations and definitions
    private static final Controller INSTANCE = new Controller();
    private Environment environment;
    
    /**
     * The private constructor to realize the singleton pattern. It also binds
     * the reference to the environment (<code>Model</code>). 
     */
    private Controller(){ }
    
    /**
     * The getInstance method returns the singleton object of the Controller
     * class.
     * 
     * @return the singleton instance of Environment.
     */
    public static Controller getInstance() {
        return INSTANCE;
    }
    
    /**
     * This method returns the environment, so the <code>Activities</code> are
     * allowed to register at the model.
     * 
     * @param env the environment.
     */
    public Environment getEnv() {
        return environment;
    }
    
    /**
     * Initiates a left-command at the environment for a specific robot.
     * 
     * @param ID the ID of the robot.
     */
    public void left(String ID) {
        environment.driveCommand(ID, Drive.LEFT);
    }

    /**
     * Initiates a right-command at the environment for a specific robot.
     * 
     * @param ID the ID of the robot.
     */
    public void right(String ID) {
        environment.driveCommand(ID, Drive.RIGHT);
    }
    
    /**
     * Initiates a forward-command at the environment for a specific robot.
     * 
     * @param ID the ID of the robot.
     */
    public void forward(String ID) {
        environment.driveCommand(ID, Drive.FORWARD);
    }    
    
    /**
     * Initiates a turn-command at the environment for a specific robot.
     * 
     * @param ID the ID of the robot.
     */
    public void turn(String ID) {
        environment.driveCommand(ID, Drive.TURN);
    }    
    
    /**
     * Sets the speed of a specific robot at the environment.
     * 
     * @param ID the ID of the robot.
     * @param speed the speed of the robot (0-100)
     */
    public void setSpeed(String ID, int speed) {
        
    }    
    
    /**
     * Sets a specific robot to be controlled by the user.
     * 
     * @param ID the ID of the robot.
     * @param enabled true, if the robot should be controlled, otherwise false.
     */
    public void setControlled(String ID, boolean enabled) {
        
    }        
}
