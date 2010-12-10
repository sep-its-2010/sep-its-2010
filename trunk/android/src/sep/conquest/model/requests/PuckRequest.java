package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.IComClient;
import sep.conquest.model.IRequest;
import sep.conquest.model.Puck;


/**
 * The PuckRequest class represents message objects that are sent by 
 * robots to the android application. It contains some information
 * about the corresponding type.
 * 
 * @author Andreas Wilhelm (Florian Lorenz)
 *
 */
public class PuckRequest implements IRequest {
    
    /**
     * The byte-array of the corresponding request-message.
     */
    private byte[] message = new byte[32];
    
    /**
     * The type of the message.
     */
    private MessageType type;
    
    
    /**
     * The sender id of the request.
     */
    UUID sender;
    
    
    /**
     * The constructor expects an request message to identify the type of the
     * message as well as its corresponding data. It stores the specific
     * message-type.
     * 
     * @param message The input-message to determine the type and data.
     */
    public PuckRequest(byte[] message, IComClient sender) {
    	for (int i = 0; i < 32; i++) {
			this.message[i] = message[i];
		}
		short messageType;
		messageType = (short) (((message[1] & 0xFF) << 8) | message[0] & 0xFF);
		switch (messageType) {
		case Puck.RES_OK:
			type = MessageType.RESPONSE_OK;
			break;
		case Puck.RES_ABYSS:
			type = MessageType.RESPONSE_ABYSS;
			break;
		case Puck.RES_COLLISION:
			type = MessageType.RESPONSE_COLLISION;
			break;
		case Puck.RES_HITNODE:
			type = MessageType.RESPONSE_HIT_NODE;
			break;
		case Puck.RES_STATUS:
			type = MessageType.RESPONSE_STATUS;
			break;
		}	
		this.sender = sender.getID();
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
		return sender;
	}
}
