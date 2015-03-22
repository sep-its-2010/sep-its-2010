package sep.conquest.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.UUID;

import sep.conquest.model.requests.FailureRequest;
import sep.conquest.util.ConquestLog;
import android.bluetooth.BluetoothSocket;

/**
 * This class inherits from the class puck and represents an e-puck roboter on
 * the smartphone. This class supports the communication via BluetoothSocket to
 * an e-puck roboter.
 * 
 * @author Florian Lorenz, Andreas Poxrucker
 * 
 */
public class RealPuck extends Puck {

  /**
   * The Bluetooth socket used to communicate with the real robot.
   */
  private BluetoothSocket mybtSocket;

  /**
   * The number of bytes of the current message that have already been read.
   */
  private int btMessageLen = 0;

  /**
   * The buffer for the current Bluetooth message.
   */
  private byte[] btMessage = new byte[MSG_LENGTH];
  
  /**
   * The position of the robot was set externally.
   */
  private boolean positionSet = false;
  
  public LinkedList<UUID> nextLocalizers = new LinkedList<UUID>();
  public UUID localizeSuccessor = null;
	/**
	 * @return the positionSet
	 */
	public boolean isPositionSet() {
		return positionSet;
	}

	/**
	 * @param positionSet
	 *            the positionSet to set
	 */
	public void setPositionSet(boolean positionSet) {
		this.positionSet = positionSet;
	}

/**
   * Constructor which generates an instance of the class RealPuck.
   * 
   * @param btSocket
   *          This is the BluetoothSocket which is hand over by the PuckFactory.
   * @param ID
   *          The unique identifying number of an instance of the RealPuck.
   */
  public RealPuck(BluetoothSocket btSocket, UUID ID, String name) {
    super(ID, name);
    this.mybtSocket = btSocket;
  }

  /**
   * This method sends a message over the BluetoothSocket to an e-puck robot. It
   * calls the method getOutputStream on mybtSocket.
   * 
   * @param message
   *          The Message that will be sent.
   */
  @Override
  public synchronized boolean writeSocket(byte[] message) {
    if (!expectMessage) {
      try {
        synchronized (mybtSocket) {
          OutputStream out = mybtSocket.getOutputStream();
          out.write(message);
        }
        expectMessage = true;
      } catch (IOException e) {
        FailureRequest req = new FailureRequest(getID(), new UUID[0],
            FailureType.BLUETOOTHFAILURE);
        ComManager comMan = ComManager.getInstance();
        comMan.broadcast(req);
        ConquestLog.addMessage(this, "Can't write message to socket!");
      }
      return true;
    }
    return false;
  }

  /**
   * This method receives a message over the BluetoothSocket from an e-puck
   * robot. It calls the method getInputStream on mybtSocket.
   * 
   * @return Returns the message sent by the e-puck.
   */
  public synchronized byte[] readSocket() {
    // if a message is expected => read socket
    if (expectMessage) {
      // Saves the number of bytes read in one read procedure.
      int numberOfBytesRead = 0;

      try {
        synchronized (mybtSocket) {
          numberOfBytesRead = mybtSocket.getInputStream().read(btMessage,
              btMessageLen, MSG_LENGTH - btMessageLen);
        }
      } catch (IOException e) {
        // If an exception occurs while reading from the input stream, create
        // a FailureRequest and send it to all other participants.
        FailureRequest req = new FailureRequest(getID(), new UUID[0],
            FailureType.BLUETOOTHFAILURE);
        ComManager comMan = ComManager.getInstance();
        comMan.broadcast(req);
        ConquestLog.addMessage(this, "Can't read message from socket!");
      }
      
      // If more then one byte has been read, increase number of read bytes.
      if (numberOfBytesRead > 0) {
        btMessageLen += numberOfBytesRead;
      }

      // Return message if it's complete and reset buffer.
      if (MSG_LENGTH == btMessageLen) {
        btMessageLen = 0;
        expectMessage = false;
        return btMessage;
      }
    }
    // If message length is not 32 bytes then return an empty array until
    // complete message is received.
    return new byte[0];
  }

  /**
   * Used to destroy a RealPuck instance. Closes the Bluetooth socket and stops
   * the LogicThread from executing.
   */
  @Override
  public void destroy() {
    super.destroy();
    try {
      mybtSocket.close();
    } catch (IOException e) {
      throw new IllegalStateException("Error! Couldn't destroy BT-Socket");
    }
  }
}