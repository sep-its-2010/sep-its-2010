package sep.conquest.model;


import java.util.UUID;


/**
 * This class inherits from the class puck and represents an virtual
 * roboter on the smartphone. This class supports the communication 
 * between the simulator and the virtual roboter.
 * 
 * @author Florian Lorenz
 * 
 */
public class VirtualPuck extends Puck {
	
	Simulator mySimulator;
	
	/**
	 * Constructor which generates an instance of the class VirtualPuck.
	 * @param puckSimulator This is the puckSimulator which is hand over by the PuckFactory.
	 * @param ID The unique identifying number of an instance of the VirtualPuck. 
	 */
	public VirtualPuck(Simulator puckSimulator, UUID ID){
		super(ID);
		mySimulator = puckSimulator;
	}
	
	/**
	 * This method sends a message to the Simulator
	 * @param buffer The Message that will be sent.
	 */
	public void writeSocket(byte[] buffer){
		
	}
	
	/**
	 * This method receives a message sent from the simulator.
	 * @return Returns the message sent by the simulator.
	 */
	public byte[] readSocket(){
		return null;
	}
}
