package sep.conquest.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import sep.conquest.model.requests.FailureRequest;
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
	public RealPuck(BluetoothSocket btSocket, UUID ID, String name) {
		super(ID, name);
		this.mybtSocket = btSocket;
	}

	/**
	 * This method sends a message over the BluetoothSocket to an e-puck
	 * robot. It calls the method getOutputStream on mybtSocket.
	 * 
	 * @param message
	 *            The Message that will be sent.
	 */
	@Override
	public boolean writeSocket(byte[] message) {
		if (super.writeSocket(message)) {
			OutputStream out;

			try {
				out = mybtSocket.getOutputStream();
				out.write(message);
				out.flush();
			} catch (IOException e) {
				FailureRequest req = new FailureRequest(getID(), null, FailureType.BLUETOOTHFAILURE);
				ComManager comMan = ComManager.getInstance();
				comMan.broadcast(req);
				ConquestLog.addMessage(this, "Can't write message to socket!");
			}
			return true;
		} else
			return false;

	}

	/**
	 * This method receives a message over the BluetoothSocket from an e-puck
	 * robot. It calls the method getInputStream on mybtSocket.
	 * 
	 * @return Returns the message sent by the e-puck.
	 */
	public byte[] readSocket() {
		// if a message is expected => read socket
		if (isMessageExpected()) {		
			InputStream in;
			byte[] message = new byte[32];
	
			try {
				in = mybtSocket.getInputStream();
				in.read(message);
			} catch (IOException e) {
				ConquestLog.addMessage(this, "Can't read message from socket!");
			}

			// check message length
			if ((btMessageLen + message.length) > MSGLENGTH)
				throw new IllegalArgumentException("Message from socket is "
						+ "longer than " + MSGLENGTH + " bytes!");
			
			// fill up message
			for (int i = 0; i < message.length; i++) {
				btMessage[btMessageLen] = message[i];
				btMessageLen++;
			}
			
			// return message if it's complete
			if (MSGLENGTH == btMessageLen) {
				super.expectMessage = false;
				return btMessage;
			}
		}
		return new byte[0];	

	}
}
