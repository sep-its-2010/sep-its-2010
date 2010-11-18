package sep.conquest.model;

import java.io.IOException;
import java.io.InputStream;
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
    private byte[] stream = new byte[30];
    
    private MessageType type;
    
    /**
     * The constructor expects an InputStream to identify the type of the
     * message as well as its corresponding data. It stores the specific
     * message-type.
     * 
     * @param stream The InputStream to determine the type and data.
     */
    public PuckRequest(InputStream stream) {   	
    	// TODO InputStream handling
        try {
			stream.read(this.stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    public byte[] getStream() {
        return stream;
    }

	/* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getReceiver()
	 */
	public UUID[] getReceiver() {
		return new UUID[0];
	}
}
