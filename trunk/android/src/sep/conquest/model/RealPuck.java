package sep.conquest.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import sep.conquest.util.ConquestLog;

import android.bluetooth.BluetoothSocket;

/**
 * This class inherits from the class puck and represents an e-puck roboter on
 * the smartphone. This class supports the communication via BluetoothSocket to
 * an e-puck roboter.
 * 
 * @author Florian Lorenz
 * 
 */
public class RealPuck extends Puck {
	
	// < Reset e-puck logic.
	private static final short REQUEST_RESET = 0x01FF; 
	
	// < Get e-puck status.
	private static final short REQUEST_STATUS = 0x02FF; 
	
	// < Turn e-puck by the specified degrees.
	private static final short REQUEST_TURN = 0x03FF;
	
	// < Move e-puck in view direction until a node is found.
	private static final short REQUEST_MOVE = 0x04FF; 
	
	// < Change the e-puck speed.
	private static final short REQUEST_SET_SPEED = 0x05FF; 
	
	// < Set the specified LEDs.
	private static final short REQUEST_SET_LED = 0x06FF;

	/**
	 * Saves the BluetoothSocket which is consigned on the constructor and
	 * connects the e-puck roboter and the RealPuck.
	 */
	private BluetoothSocket mybtSocket;

	/**
	 * Constructor which generates an instance of the class RealPuck.
	 * 
	 * @param btSocket
	 *            This is the BluetoothSocket which is hand over by the
	 *            PuckFactory.
	 * @param ID
	 *            The unique identifying number of an instance of the RealPuck.
	 */
	public RealPuck(BluetoothSocket btSocket, UUID ID) {
		super(ID);
		this.mybtSocket = btSocket;
	}

	/**
	 * This method sends a message over the BluetoothSocket to an e-puck
	 * roboter. It calls the method getOutputStream on mybtSocket.
	 * 
	 * @param message
	 *            The Message that will be sent.
	 */
	public void writeSocket(byte[] message) {
		OutputStream out;

		try {
			out = mybtSocket.getOutputStream();
			out.write(message);
			out.flush();
		} catch (IOException e) {
			ConquestLog.addMessage(this, "Can't write message to socket!");
		}
	}

	/**
	 * This method receives a message over the BluetoothSocket from an e-puck
	 * roboter. It calls the method getInputStream on mybtSocket.
	 * 
	 * @return Returns the message sent by the e-puck.
	 */
	public byte[] readSocket() {
		InputStream in;
		byte[] message = new byte[32];

		try {
			in = mybtSocket.getInputStream();
			in.read(message);
		} catch (IOException e) {
			ConquestLog.addMessage(this, "Can't read message from socket!");
		}

		return message;
	}

	public void forward() {
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_MOVE & 0xff);
		request[1] = (byte)((REQUEST_MOVE >> 8) & 0xff);
		
		writeSocket(request);
	}

	public void left() {
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_TURN & 0xff);
		request[1] = (byte)((REQUEST_TURN >> 8) & 0xff);
		request[2] = (byte) 1; // turn right
		
		writeSocket(request);
	}

	public void right() {
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_TURN & 0xff);
		request[1] = (byte)((REQUEST_TURN >> 8) & 0xff);
		request[2] = (byte) -1; // turn left
		
		writeSocket(request);
	}

	public void setSpeed(SpeedLevel level) {
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_SET_SPEED & 0xff);
		request[1] = (byte)((REQUEST_SET_SPEED >> 8) & 0xff);
		request[2] = (byte) level.getSpeed();
		
		writeSocket(request);
	}

	public void turn() {
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_TURN & 0xff);
		request[1] = (byte)((REQUEST_TURN >> 8) & 0xff);
		request[2] = (byte) 2;
		
		writeSocket(request);
	}
	
	@Override
	public void setControlled(boolean enabled) {
		super.setControlled(enabled);
		byte[] request = new byte[32];
		request[0] = (byte)(REQUEST_SET_LED & 0xff);
		request[1] = (byte)((REQUEST_SET_LED >> 8) & 0xff);
		request[2] = (byte) 10;
		
		writeSocket(request);
	}	
}
