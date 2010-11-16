package sep.conquest.model;

/**
 * The enumeration for message-types of the broadcast-communication.
 * 
 * @author Andreas Wilhelm
 */
public enum MessageType {
    
    /**
     * Specifies a message in order to control the drive-direction of a robot.
     */
    CONTROL_DIR,
    
    /**
     * Specifies a message in order to control the speed of a robot.
     */
    CONTROL_SPEED;    
}
