package sep.conquest.model;

import java.util.Collection;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;

/**
 * Contains methods to create a set of RealPucks or VirtualPucks. Created Pucks
 * are registered at the ComManager.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class PuckFactory {

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
  public static boolean createRealPucks(Collection<BluetoothSocket> robots) {
    // Get instance of ComManager to add created Pucks.
    ComManager man = ComManager.getInstance();

    // In case set is empty or null return false.
    // Otherwise start creating RealPucks.
    if (robots == null || robots.isEmpty()) {
      throw new IllegalArgumentException("Invalid set of robots passed");
    } else {
      // Iterate over set and create RealPuck for each device.
      for (BluetoothSocket robot : robots) {
        UUID newUUID = UUID.randomUUID();
        Puck newPuck = new RealPuck(robot, newUUID);
        man.addClient(newUUID, newPuck);
      }
      // initiate handshaking of the robots
      Puck first = (Puck) man.getClients()[0];
      first.sendHello();
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
   * @param number
   *          The number of VirtualPucks to create.
   * @return True, if creation and registration was successful, false otherwise.
   */
  public static boolean createVirtualPucks(Simulator sim, int number) {
    // Get instance of ComManager to add created Pucks.
    ComManager man = ComManager.getInstance();

    // If number is greater than zero start creation.
    // Otherwise return false.
    if (number > 0 && sim != null) {
      
      for (int i = 0; i < number; i++) {
        // UUID of new VirtualPuck
        UUID newUUID = UUID.randomUUID();
        Puck newPuck = new VirtualPuck(newUUID, sim);
        man.addClient(newUUID, newPuck);
      }
      // initiate handshaking of the robots
      Puck first = (Puck) man.getClients()[0];
      first.sendHello();
      return true;
    } else {
      throw new IllegalArgumentException(
          "Number must be grater than zero and Simulator must not equal null");
    }
  }
}
