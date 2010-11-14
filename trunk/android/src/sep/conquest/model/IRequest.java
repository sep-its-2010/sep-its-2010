package sep.conquest.model;

/**
 * The IRequest-interface determines, that every request-object must implement
 * the getKind-method to identify the request-type.
 * 
 * @author Andreas Wilhelm
 */
public interface IRequest {
    
    /**
     * getKind returns the type of the request-message.
     * 
     * @return
     */
    MessageType getKind();
    
    /**
     * getReceiver returns the list of client (IDs) which have to receive the
     * message.
     * 
     * @return the list of destinations.
     */
    String[] getReceiver();
}
