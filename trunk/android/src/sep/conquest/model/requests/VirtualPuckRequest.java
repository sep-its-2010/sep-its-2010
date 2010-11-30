package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.IRequest;


/**
 * The VirtualPuckRequest class represents message objects that are sent by 
 * virtual pucks to a simulator. It contains some information about the
 * corresponding type.
 * 
 * @author Andreas Wilhelm
 *
 */
public class VirtualPuckRequest implements IRequest {
    
    /**
     * The byte-array of the corresponding request-message.
     */
    private byte[] message = new byte[32];
    
    /**
     * The type of the message.
     */
    private MessageType type;
    
    /**
     * The sender of the message.
     */
    private UUID sender;
    
    /**
     * The constructor expects an request message to identify the type of the
     * message as well as its corresponding data. It stores the specific
     * message-type.
     * 
     * @param message The input-message to determine the type and data.
     */
    public VirtualPuckRequest(UUID sender, byte[] message) {
    	this.message = message;
    	this.sender = sender;
    	
    	byte[] code = {message[0], message[1]};
    	
    	
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
