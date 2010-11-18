package sep.conquest.model;

import java.util.UUID;


/**
 * The ControlledRequest represents message objects that are sent by 
 * IComClient clients. It will be sent from a participant if an error is
 * occurred during the runtime.
 * 
 * @author Andreas Wilhelm
 *
 */
public class FailureRequest extends Request {
    
    /**
     * The specific failure type which has occurred.
     */
    private FailureType type;
    
    /**
     * The constructor expects a list of receiver as well as the
     * specific failure type of the error.
     * 
     * @param receiver The IDs of the receiver.
     * @param type The error type.
     */
    public FailureRequest(UUID[] receiver, FailureType type) {
    	super(receiver);    	
        this.type = type;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.FAILURE;
    }
    
    /**
     * Returns the specific error type of the FailureRequest.
     * 
     * @return The error type.
     */
    public FailureType getFailureType() {
        return type;
    }
}