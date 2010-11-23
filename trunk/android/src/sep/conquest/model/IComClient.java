package sep.conquest.model;

import java.util.UUID;

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
     * @param sender The sender of the broadcast message.
     * @param request The message which has to be delivered.
     */
    void deliver(UUID sender, IRequest request);
    
    /**
     * Returns the id of the client.
     * 
     * @return The id
     */
    UUID getID();
}
