package sep.conquest.model.requests;

/**
 * The PuckMessageType represents message types which are used by real e-puck
 * robots as well as the simulator. It contains a 2-byte code to identify
 * the specific message type.
 * 
 * @author Andreas Wilhelm
 *
 */
public enum PuckMessageType {

	/**
	 * Reset e-puck logic.
	 */
	RESET((short)0x01FF),
	
	/**
	 * Get e-puck status.
	 */
	STATUS((short)0x02FF),
	
	/**
	 * Turn e-puck by the specified degrees.
	 */
	TURN((short)0x03FF),
	
	/**
	 * Move e-puck in view direction until a node is found.
	 */
	MOVE((short)0x04FF),
	
	/**
	 * Change the e-puck speed.
	 */
	SET_SPEED((short)0x05FF),
	
	/**
	 * Set the specified LEDs.
	 */
	SET_LED((short)0x06FF),
	
	/**
	 * Request acknowledged.
	 */
	RESPONSE_OK((short)0x81FF),
	
	/**
	 * Current e-puck status.
	 */
	RESPONSE_STATUS((short)0x82FF),

	/**
	 * e-puck has hit a node.
	 */
	RESPONSE_HIT_NODE((short)0x83FF),
	
	/**
	 * e-puck has detected a collision while moving.
	 */
	RESPONSE_COLLISION((short)0x84FF),
	
	/**
	 * e-puck has detected an abyss while moving.
	 */
	RESPONSE_ABYSS((short)0x85FF);

	/**
	 * The typecode.
	 */
	private final short typecode;
	
	/**
	 * The private constructor.
	 * 
	 * @param typecode The typecode.
	 */
	private PuckMessageType(short typecode) {
		this.typecode = typecode;		
	}
	
	/**
	 * Returns the typecode of the message.
	 * 
	 * @return The typecode.
	 */
	public short getTypeCode() {
		return typecode;
	}
	
	
}
