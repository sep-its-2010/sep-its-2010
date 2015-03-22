package sep.conquest.activity;

/**
 * Indicates, how Import Activity is used in the Application.
 * 
 * @author Andreas Poxrucker
 *
 */
public enum ImportMode {

  /**
   * Import is used to open a map for Simulation Activity.
   */
  SIMULATION_MAP,
  
  /**
   * Import is used to open map and display is in Map Activity.
   */
  IMPORT_MAP
}
