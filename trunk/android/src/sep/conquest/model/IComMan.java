package sep.conquest.model;

/**
 * The IComMan-interface enables a communication-manager to provide broadcast
 * messaging.
 * 
 * @author Andreas Wilhelm
 */
public interface IComMan {
    
    /**
     * Initiate a broadcast message to all registered participants at the
     * communication-manager.
     * 
     * @param sender the sender of the broadcast-message.
     * @param request the request-message.
     */
    void broadcast(IComClient sender, IRequest request);
    
    /**
     * addClient registers a client for participating the broadcast-
     * communication by the communication-manager.
     * 
     * @param ID the ID of the client.
     * @param client the client which has to be added.
     */    
    void addClient(String ID, IComClient client);

    /**
     * removeClient removes a participant from the communication-manager.
     * 
     * @param ID the ID of the client which has to be removed.
     */
    void removeClient(String ID);
}
