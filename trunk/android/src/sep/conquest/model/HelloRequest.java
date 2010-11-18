package sep.conquest.model;

/**
 * The ControlledRequest represents message objects that are sent by 
 * IComClient clients. It will be sent from a participant after a defined
 * period of time during the init-process to all participants of the
 * communication-network.
 * 
 * @author Andreas Wilhelm
 *
 */
public class HelloRequest extends Request {
    
    /**
     * The constructor for a introduction-request.
     */
    public HelloRequest() {
    	super();    	
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.HELLO;
    }
}
