package sep.conquest.model.requests;

/**
 * The enumeration for message-types of the broadcast- and bluetooth-
 * communication.
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
    PUCK_ABYSS,
    
	/**
	 * Reset e-puck logic.
	 */
	REQUEST_RESET(0x01FF),
	
	/**
	 * Get e-puck status.
	 */
	REQUEST_STATUS(0x02FF),
	
	/**
	 * Turn e-puck by the specified degrees.
	 */
	REQUEST_TURN(0x03FF),
	
	/**
	 * Move e-puck in view direction until a node is found.
	 */
	REQUEST_MOVE(0x04FF),
	
	/**
	 * Change the e-puck speed.
	 */
	REQUEST_SET_SPEED(0x05FF),
	
	/**
	 * Set the specified LEDs.
	 */
	REQUEST_SET_LED(0x06FF),
	
	/**
	 * Request acknowledged.
	 */
	RESPONSE_OK(0x81FF),
	
	/**
	 * Current e-puck status.
	 */
	RESPONSE_STATUS(0x82FF),

	/**
	 * e-puck has hit a node.
	 */
	RESPONSE_HIT_NODE(0x83FF),
	
	/**
	 * e-puck has detected a collision while moving.
	 */
	RESPONSE_COLLISION(0x84FF),
	
	/**
	 * e-puck has detected an abyss while moving.
	 */
	RESPONSE_ABYSS(0x85FF);

	/**
	 * The typecode.
	 */
	private final int typecode;
	
	/**
	 * The empty constructor for messages without type-codes.
	 */
	private MessageType() {
		this.typecode = 0;
	}
	
	/**
	 * The private constructor.
	 * 
	 * @param typecode The typecode.
	 */
	private MessageType(int typecode) {
		this.typecode = typecode;		
	}
	
	/**
	 * Returns the typecode of the message.
	 * 
	 * @return The typecode.
	 */
	public int getTypeCode() {
		return typecode;
	}    
    
}
