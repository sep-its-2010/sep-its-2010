package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.IRequest;

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
     * The sender of the request.
     */
    protected UUID sender;
    
    /**
     * The constructor expects a list of receivers. It represents a request
     * which shall be read by the receivers.
     * 
     * @param receiver
     */
    protected Request(UUID sender, UUID[] receiver) {
    	this.sender = sender;
    	this.receiver = receiver;
    }
    
    /**
     * The constructor expects no receiver. The requests shall be read by
     * all participants of the communication-network.
     */
    protected Request(UUID sender) {
    	this.sender = sender;
    	this.receiver = new UUID[0];
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getReceiver()
     */
    public UUID[] getReceiver() {
        return receiver;
    }  
    
    /* (non-Javadoc)
	 * @see sep.conquest.model.IRequest#getSender()
	 */
	public UUID getSender() {
		return sender;
	}    
}