package sep.conquest.model.test;

import java.util.UUID;

import android.test.AndroidTestCase;
import sep.conquest.model.*;
import sep.conquest.model.requests.HelloRequest;

/**
 * A test-case for the communication manager.
 * 
 * @author Andreas Wilhelm
 *
 */
public class ComManagerTest extends AndroidTestCase {
	
	private ComManager comManager;
	private TestClient client;

	public ComManagerTest() {
		super();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		client = new TestClient();
		comManager = ComManager.getInstance();
	}


	/**
	 * Test method for {@link sep.conquest.model.ComManager#addClient(UUID, sep.conquest.model.IComClient)}.
	 */
	public void testAddClientAndSend() {
		comManager.addClient(client.getID(), client);
		HelloRequest request_out = new HelloRequest(client.getID());
		comManager.broadcast(request_out);
		HelloRequest request_in =(HelloRequest) client.getLastRequest();
		
		assertEquals("Message wasn't received!", request_out, request_in);
	}
	
	/**
	 * Test method for {@link sep.conquest.model.ComManager#removeClient(UUID)}.
	 */
	public void testRemoveClient() {
		comManager.addClient(client.getID(), client);
		comManager.removeClient(client.getID());
		HelloRequest request_out = new HelloRequest(client.getID());
		comManager.broadcast(request_out);
		HelloRequest request_in =(HelloRequest) client.getLastRequest();
		
		assertNull("Client wasn't removed!", request_in);	
	}
	
	/**
	 * A test-class for IComClients.
	 * 
	 * @author Andreas Wilhelm
	 *
	 */
	public class TestClient implements IComClient {
		
		UUID myID;
		IRequest lastRequest;
		
		public TestClient() {
			myID  = UUID.randomUUID();
		}
		
		public IRequest getLastRequest() {
			IRequest temp = lastRequest;
			lastRequest = null;
			return temp;
		}

		public void deliver(IRequest request) {
			lastRequest = request;
		}

		public void destroy() {
		}

		public UUID getID() {
			return myID;
		}
		
	}
}
