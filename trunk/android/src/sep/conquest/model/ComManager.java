package sep.conquest.model;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The ComManager class is the global communication-manager to provide
 * broadcast-communication. It has a (thread-safe) list with all clients who are
 * registered. Every registered participant will get the request-messages from
 * others. Classes which have implemented the IComClient-interface can be
 * registered by the addClient-method. Requests will be sent by the broadcast-
 * method.
 * 
 * @author Andreas Wilhelm
 */
public class ComManager implements IComMan {

    /**
     * The ComManager instance to realize the singleton pattern.
     */
    private static final ComManager INSTANCE = new ComManager();
    
    
    /**
     * The list of registered IComClient participants who can communicate
     * via broadcast.
     */
    private ConcurrentMap<String, IComClient> clients = 
        new ConcurrentHashMap<String, IComClient>();
    
    /**
     * The private constructor to realize the singleton pattern.
     */
    private ComManager(){ }
    
    /**
     * The getInstance method returns the singleton object of the ComManager
     * class.
     * 
     * @return the singleton instance of ComManager.
     */
    public static ComManager getInstance() {
        return INSTANCE;
    }    
    

    /* (non-Javadoc)
     * @see sep.conquest.model.IComMan#addClient(java.lang.String, sep.conquest.model.IComClient)
     */
    public void addClient(String ID, IComClient client) {
        clients.put(ID, client);
    }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IComMan#removeClient(java.lang.String)
     */
    public void removeClient(String ID) {
        clients.remove(ID);
    }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IComMan#broadcast(sep.conquest.model.IComClient, sep.conquest.model.IRequest)
     */
    public void broadcast(IComClient sender, IRequest request) { 
        Iterator<String> it = clients.keySet().iterator();
        
        while (it.hasNext()) {
            String key = it.next();
            IComClient client = clients.get(key);
            client.deliver(sender, request);
        }
    }

	/* (non-Javadoc)
	 * @see sep.conquest.model.IComMan#addClient(java.util.UUID, sep.conquest.model.IComClient)
	 */
	public void addClient(UUID ID, IComClient client) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IComMan#removeClient(java.util.UUID)
	 */
	public void removeClient(UUID ID) {
		// TODO Auto-generated method stub
		
	}

}
