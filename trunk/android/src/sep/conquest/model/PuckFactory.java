package sep.conquest.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
    
    // Saves created Pucks temporary.
    Map<UUID, Puck> createdPucks = new TreeMap<UUID, Puck>();

    // In case set is empty or null return false.
    // Otherwise start creating RealPucks.
    if (robots == null || robots.isEmpty()) {
      
      // Evtl. IllegalArgumentException besser?!
      return false;
    } else {
      
      // Iterate over set and create RealPuck for each device.
      for (BluetoothDevice robot : robots) {
        UUID newUUID = UUID.randomUUID();
        Puck newPuck = createRealPuck(newUUID, robot);

        // If creation has failed due to a device is no longer available 
        // creation is aborted and false s returned.
        // Otherwise add Puck to list.
        if (newPuck == null) {
          return false;
        } else {
          createdPucks.put(newUUID, newPuck);
        }
      }
      
      // When all Pucks are created, add them to the ComManager.
      ComManager man = ComManager.getInstance();
      Set<UUID> keys = createdPucks.keySet();
      
      for (UUID id : keys) {
        man.addClient(id, createdPucks.get(id));
      }
      return true;
    }
  }

  /**
   * Creates number times a new VirtualPuck instance and registers it to the
   * ComManager.
   * 
   * As soon as all Pucks are created and connected to the ComManager return
   * true.
   * 
   * @param number The number of VirtualPucks to create. 
   * @return True, if creation and registration was successful, false otherwise.
   */
  public static boolean createVirtualPucks(int number) {
    
    // Get instance of ComManager to add created Pucks.
    ComManager man = ComManager.getInstance();

    // If number is greater than zero start creation.
    // Otherwise return false.
    if (number > 0) {
      for (int i = 0; i < number; i++) {
        UUID newUUID = UUID.randomUUID();
        Puck newPuck = new VirtualPuck(UUID.randomUUID());
        man.addClient(newUUID, newPuck);
      }
      return true;
    } else {
      
      // Evtl. IllegalArgumentException besser?!
      return false;
    }
  }

  /**
   * Creates a new instance of RealPuck.
   * 
   * Connects to the passed BluetoothDevice and opens Socket for Communication.
   * Then creates new RealPuck with random UUID and the connected socket.
   * 
   * If opening connection fails return null.
   * 
   * @param uuid The UUID of the robot.
   * @param robot The e-puck robot represented as BluetoothDevice to connect to.
   * @return Instance of RealPuck
   */
  private static Puck createRealPuck(UUID uuid, BluetoothDevice robot) {
    try {
      
      // Open RFCommSocket with standard UUID.
      BluetoothSocket socket = robot.createRfcommSocketToServiceRecord(STD_UUID);
      
      try {
        socket.connect();
        Puck newPuck = new RealPuck(socket, UUID.randomUUID());
        return newPuck;
      } catch (IOException connectExcetion) {
        
        // If IOException occurs during opening a connection, return null.
        return null;
      }
    } catch (IOException createRfException) {
      
      // If IOException occurs during opening RfCommSocket, return null as
      // device is not available.
      return null;
    }  
  }
}
