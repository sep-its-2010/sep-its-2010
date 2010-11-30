package sep.conquest.model;


import java.util.UUID;

import sep.conquest.model.requests.PuckRequest;


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
	public VirtualPuck(UUID ID){
		super(ID);
		sim = Simulator.getInstance();
	}
	
	/**
	 * This method sends a message to the Simulator.
	 * 
	 * @param buffer The Message that will be sent.
	 */
	public void writeSocket(byte[] buffer){
		PuckRequest request = new PuckRequest(buffer);

	}
	
	/**
	 * This method receives a message sent from the simulator.
	 * 
	 * @return Returns the message sent by the simulator.
	 */
	public byte[] readSocket(){
		return null;
	}
}
