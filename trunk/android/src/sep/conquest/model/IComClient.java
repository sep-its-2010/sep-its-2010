package sep.conquest.model;

/**
 * The interface IComClient enables clients to receive messages from others
 * by the communication-manager.
 * 
 * @author Andreas Wilhelm
 */
public interface IComClient {
    
    /**
     * The method delivers a message from a specific sender.
     * 
     * @param sender sender the sender of the broadcast message.
     * @param request request the message which has to be delivered.
     */
    void deliver(IComClient sender, IRequest request);
}
