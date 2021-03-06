package sep.conquest.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import sep.conquest.model.behaviour.LocalLocalizeBehaviour;
import sep.conquest.util.Utility;

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
   * The beginning of a robot name used to create names for VirtualPucks.
   */
  private static final String ROBOT_NAME = "e-puck_";
  
  /**
   * The Simulator used with the VirtualPucks.
   */
  private static Simulator simulator;

  /**
   * Creates a new RealPuck instance for each BluetoothDevice of the set and
   * registers it to the ComManager. If creation fails (for example when device
   * is not available) creation is aborted and false is returned.
   * 
   * As soon as all Pucks are successfully created, method returns true.
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
	LocalLocalizeBehaviour.startPositions = new TreeMap<UUID, Integer>();
	LocalLocalizeBehaviour.startOrientations = new TreeMap<UUID, Orientation>();
    Map<String, UUID> mapping = new TreeMap<String, UUID>();
	Puck first = null;
      for (BluetoothSocket robot : robots) {
        UUID newUUID = UUID.randomUUID();
        String name = robot.getRemoteDevice().getName();
        Puck newPuck = new RealPuck(robot, newUUID, name);
        if (first == null)
        	first = newPuck;
        man.addClient(newUUID, newPuck);
        mapping.put(name, newUUID);
        LocalLocalizeBehaviour.startOrientations.put(newUUID, Orientation.UP);
      }
      
      // set startpositions
      int i = 0;
      for (String name: mapping.keySet()) {
          LocalLocalizeBehaviour.startPositions.put(mapping.get(name), 
        		  Utility.makeKey(i++, 0)); 
      }
      
      // initiate handshaking of the robots
      first.sendHello();
      return true;
    }
  }

  /**
   * Creates a new simulator instance and new VirtualPuck instances and
   * registers it to the ComManager.
   * 
   * As soon as all Pucks are created and connected to the ComManager return
   * true.
   * 
   * @param map
   *          The map to explore in the simulator
   * @param positions
   *          The initial positions of the robots
   * @param ori
   *          The initial orientations of the robots
   * @return True, if creation and registration was successful, false otherwise.
   */
  public static boolean createVirtualPucks(GridMap map, int[][] positions,
      Orientation[] ori) {
    // Get instance of ComManager to add created Pucks.
    ComManager man = ComManager.getInstance();

    if ((positions != null) && (ori != null) && (map != null)
        && (positions.length == ori.length)) {
      // Saves ids of VirtualPucks
      Set<UUID> ids = new TreeSet<UUID>();

      // Saves mapping of id to positions
      Map<UUID, int[]> posMap = new TreeMap<UUID, int[]>();

      // Saves mapping of id to orientation
      Map<UUID, Orientation> oriMap = new TreeMap<UUID, Orientation>();

      // Create new ids and mapping to position and orientation
      LocalLocalizeBehaviour.startPositions = new TreeMap<UUID, Integer>();
      LocalLocalizeBehaviour.startOrientations = new TreeMap<UUID, Orientation>(); 
      for (int i = 0; i < positions.length; i++) {
        UUID newId = UUID.randomUUID();
        ids.add(newId);

        // Check for invalid positions or orientations.
        if ((positions[i].length != 2) || (ori[i] == null)) {
          throw new IllegalArgumentException("Invalid position or orientation");
        }
        posMap.put(newId, positions[i]);
        oriMap.put(newId, ori[i]);
        LocalLocalizeBehaviour.startPositions.put(newId, Utility.makeKey(positions[i][0], positions[i][1]));
        LocalLocalizeBehaviour.startOrientations.put(newId, ori[i]);
      }
      
      simulator = new Simulator(map, posMap, oriMap);

      int i = 0;
      Puck first = null;
      for (UUID id : ids) {
        Puck newPuck = new VirtualPuck(id, simulator, ROBOT_NAME + i);
        if (first == null) {
        	first = newPuck;
        }
        man.addClient(id, newPuck);
        i++;
      }
      // initiate handshaking of the robots
      first.sendHello();
      return true;
    } else {
      throw new IllegalArgumentException(
          "Map, position and orientation must not equal null");
    }
  }
  
  /**
   * Returns the simulator used with the current set of VirtualPucks or null,
   * if no VirtualPucks have been created so far.
   * 
   * @return Simulator or null.
   */
  public static Simulator getSimulator() {
    return simulator;
  }
}