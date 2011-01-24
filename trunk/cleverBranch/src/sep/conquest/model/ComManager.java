package sep.conquest.model;

import java.util.Set;
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
	private static ComManager INSTANCE = new ComManager();

	/**
	 * The list of registered IComClient participants who can communicate via
	 * broadcast.
	 */
	private ConcurrentMap<UUID, IComClient> clients = 
		new ConcurrentHashMap<UUID, IComClient>();

	/**
	 * The private constructor to realize the singleton pattern.
	 */
	private ComManager() {
	}

	/**
	 * The getInstance method returns the singleton object of the ComManager
	 * class.
	 * 
	 * @return The singleton instance of ComManager.
	 */
	public static ComManager getInstance() {
		return INSTANCE;
	}
	
	public static void reset() {
	  INSTANCE = new ComManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IComMan#broadcast(sep.conquest.model.IComClient,
	 * sep.conquest.model.IRequest)
	 */
	public synchronized void broadcast(IRequest request) {
		
		// broadcast
		if (request.getReceiver().length == 0) {
			for (UUID id: clients.keySet()) {
				IComClient client = clients.get(id);
				client.deliver(request);
			}
			
		// multicast
		} else {
			for (UUID id: request.getReceiver()) {
				if (clients.containsKey(id)) {
					IComClient client = clients.get(id);
					client.deliver(request);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IComMan#addClient(java.util.UUID,
	 * sep.conquest.model.IComClient)
	 */
	public void addClient(UUID id, IComClient client) {
		clients.put(id, client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sep.conquest.model.IComMan#removeClient(java.util.UUID)
	 */
	public void removeClient(UUID id) {
		clients.remove(id);
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IComMan#getClients()
	 */
	public IComClient[] getClients() {
		Set<UUID> keys = clients.keySet();
		IComClient[] clientArray = new IComClient[keys.size()];
		int i = 0;
		
		for (UUID key: keys) {
			clientArray[i] = clients.get(key);
			i++;
		}
		
		return clientArray;
	}

	/* (non-Javadoc)
	 * @see sep.conquest.model.IComMan#getClient(java.util.UUID)
	 */
	public IComClient getClient(UUID id) {
		return clients.get(id);
	}
}