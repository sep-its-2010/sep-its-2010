package sep.conquest.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Contains methods to create a set of RealPucks or VirtualPucks. Created Pucks
 * are registered at the ComManager.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class PuckFactory {
  
  private static UUID STD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  /**
   * Creates a new RealPuck instance for each BluetoothDevice of the set and
   * registers it to the ComManager. If creation fails (for example when device
   * is not available) creation is aborted and false is returned.
   * 
   * As soon as all Pucks are successfully created method return true.
   * 
   * If empty set or null is passed method returns false.
   * 
   * @param robots
   *          Set of BluetoothDevices identified as e-puck robots.
   * @return True, if creation and registration was successful, false otherwise.
   */
  public static boolean createRealPucks(Set<BluetoothDevice> robots) {
    List<Puck> createdPucks = new LinkedList<Puck>();

    if (robots == null || robots.isEmpty()) {
      for (BluetoothDevice robot : robots) {
        Puck puck = createPuck(robot);

        if (puck == null) {
          return false;
        } else {
          createdPucks.add(puck);
        }
      }
    } else {
      return false;
    }
    return true;
  }

  public static boolean createVirtualPucks(int number) {

    for (int i = 0; i < number; i++) {
      createPuck();
    }
    return true;
  }

  private static Puck createPuck(BluetoothDevice robot) {
    try {
      BluetoothSocket socket = robot.createRfcommSocketToServiceRecord(STD_UUID);
      
      try {
        socket.connect();
        return null;
      } catch (IOException connectExcetion) {
        return null;
      }
    } catch (IOException createRfException) {
      return null;
    }  
  }

  private static Puck createPuck() {
    return null;
  }
}
