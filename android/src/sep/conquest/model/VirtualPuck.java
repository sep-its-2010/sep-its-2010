package sep.conquest.model;

import java.util.UUID;
import sep.conquest.model.requests.VirtualPuckRequest;

/**
 * This class inherits from the class puck and represents an virtual
 * roboter on the smartphone. This class supports the communication 
 * between the simulator and the virtual roboter.
 * 
 * @author Florian Lorenz
 */
public class VirtualPuck extends Puck {
	
	/**
	 * Saves an instance of the class simulator.
	 */
	private Simulator sim;
	
	/**
	 * Constructor which generates an instance of the class VirtualPuck.
	 * @param puckSimulator This is the puckSimulator which is hand over by the 
	 * PuckFactory.
	 * @param ID The unique identifying number of an instance of the 
	 * VirtualPuck. 
	 */
	public VirtualPuck(UUID ID, Simulator simulator, String name){
		super(ID, name);
		sim = simulator;
	}
	
	/**
	 * This method sends a message to the Simulator.
	 * 
	 * @param buffer The Message that will be sent.
	 */
	public boolean writeSocket(byte[] buffer){
	    if (!expectMessage) {
			VirtualPuckRequest request = new VirtualPuckRequest(getID(),buffer);
			sim.addRequest(request);
	        expectMessage = true;
	        return true;
	      }
	    return false;
	}
	
	/**
	 * This method receives a message sent from the simulator.
	 * 
	 * @return Returns the message sent by the simulator.
	 */
	public byte[] readSocket(){
		// if a message is expected => read socket
		if (expectMessage) {		
			byte[] message = sim.readBuffer(getID());
			
			// return message if it's complete
			if (message.length > 0) {
				expectMessage = false;
				return message;
			}
		}
		return new byte[0];	
	}
	
	@Override
	public void destroy() {
		sim.stop();
		super.destroy();
	}
}
