package sep.conquest.model;


import java.util.UUID;

import android.bluetooth.BluetoothSocket;

/**
 * This class inherits from the class puck and represents an e-puck
 * roboter on the smartphone. This class supports the communication via
 * BluetoothSocket to an e-puck roboter.
 * 
 * @author Florian Lorenz
 * 
 */
public class RealPuck extends Puck {

	/**
	 * Saves the BluetoothSocket which is consigned on the constructor and 
	 * connects the e-puck roboter and the RealPuck.
	 */
	private BluetoothSocket mybtSocket;
	
	/**
	 * Constructor which generates an instance of the class RealPuck.
	 * 
	 * @param btSocket This is the BluetoothSocket which is hand over by the 
	 * PuckFactory.
	 * @param ID The unique identifying number of an instance of the RealPuck. 
	 */
	public RealPuck(BluetoothSocket btSocket, UUID ID){
		super(ID);
		this.mybtSocket = btSocket;
	}
	
	/**
	 * This method sends a message over the BluetoothSocket to an e-puck 
	 * roboter. 
	 * It calls the method getOutputStream on mybtSocket.
	 * 
	 * @param message The Message that will be sent.
	 */
	public void writeSocket(byte[] message){
		
	}
	
	/**
	 * This method receives a message over the BluetoothSocket from an e-puck
	 * roboter. It calls the method getInputStream on mybtSocket.
	 * 
	 * @return Returns the message sent by the e-puck.
	 */
	public byte[] readSocket(){
		return null;
	}

	public void forward() {
		// TODO Auto-generated method stub
		
	}

	public void left() {
		// TODO Auto-generated method stub
		
	}

	public void right() {
		// TODO Auto-generated method stub
		
	}

	public void setControlled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	public void setSpeed(SpeedLevel level) {
		// TODO Auto-generated method stub
		
	}

	public void turn() {
		// TODO Auto-generated method stub
		
	}
}
