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
    CONTROL_SPEED,
    
    /**
     * Specifies a message to provide a status update of robots.
     */
    STATUS_UPDATE,
    
    /**
     * Specifies a message in case of a collision of robots.
     */
    COLLISION,
    
    /**
     * Specifies a message in case of enabling or disabling the manual control
     * of a participant.
     */
    CONTROL_ENABLE,
    
    /**
     * Specifies a message in case of a failure.
     */
    FAILURE,
    
    /**
     * Specifies a message which represents a drive-intent of a robot.
     */
    INTENT,
    
    /**
     * Specifies a message to introduce a participant to others.
     */
    HELLO,
    
    /**
     * Specifies a collision-message of the robot.
     */
    PUCK_COLLISION,
    
    /**
     * Specifies an information-message of the robot for visited nodes.
     */
    PUCK_HITNODE,
    
    /**
     * Specifies an status-message of a robot.
     */
    PUCK_STATUS,
    
    /**
     * Specifies an acknowledge-message of a robot.
     */
    PUCK_OK,
    
    /**
     * Specifies an abyss-message of a robot.
     */
    PUCK_ABYSS;
}
