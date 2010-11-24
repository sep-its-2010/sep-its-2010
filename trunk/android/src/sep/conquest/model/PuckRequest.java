package sep.conquest.model;

import java.util.UUID;


/**
 * The PuckRequest class represents message objects that are sent by 
 * robots to the android application. It contains some information
 * about the corresponding type.
 * 
 * @author Andreas Wilhelm
 *
 */
public class PuckRequest implements IRequest {
    
    /**
     * The byte-array of the corresponding request-message.
     */
    private byte[] message = new byte[32];
    
    private MessageType type;
    
    /**
     * The constructor expects an request message to identify the type of the
     * message as well as its corresponding data. It stores the specific
     * message-type.
     * 
     * @param message The input-message to determine the type and data.
     */
    public PuckRequest(byte[] message) {
    	message = this.message;
        type = MessageType.PUCK_OK; // TODO determine correct message-type
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return type;
    }
    
    /**
     * Returns the byte-array of the robot-message.
     * 
     * @return The InputStream.
     */
    public byte[] getMessage() {
        return message;
    }

	/* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getReceiver()
	 */
	public UUID[] getReceiver() {
		return new UUID[0];
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getSender()
	 */
	public UUID getSender() {
		return UUID.randomUUID();
	}
}
