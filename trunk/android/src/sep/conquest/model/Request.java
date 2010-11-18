package sep.conquest.model;

import java.util.UUID;

/**
 * The class has to be used by request classes, which are needed for the
 * broadcast-communication.
 * 
 * @author Andreas Wilhelm
 *
 */
public abstract class Request implements IRequest {
	
    /**
     * The list of clients that should receive the message.
     */
    protected UUID[] receiver;	
    
    /**
     * The constructor expects a list of receivers. It represents a request
     * which shall be read by the receivers.
     * 
     * @param receiver
     */
    protected Request(UUID[] receiver) {
    	this.receiver = receiver;
    }
    
    /**
     * The constructor expects no receiver. The requests shall be read by
     * all participants of the communication-network.
     */
    protected Request() { }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getReceiver()
     */
    public UUID[] getReceiver() {
        return receiver;
    }    
}
