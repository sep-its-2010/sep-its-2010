package sep.conquest.model;

/**
 * The enum FailureType contains all possible errors that can occur during the
 * runtime of the conquest-application.
 * 
 * @author Andreas Wilhelm
 *
 */
public enum FailureType {
	
	/**
	 * Abyss detected.
	 */
	ABYSS,
	
	/**
	 * Invalid playing field.
	 */
	INVALIDPITCH,
	
	/**
	 * Bluetooth breaks down.
	 */
	BLUETOOTHFAILURE,
	
	/**
	 * If a node cannot be visited, because it's obstructed
	 */
	NODEOBSTRUCTION;
}
