package sep.conquest.model;

/**
 * The enum FailureType contains all possible errors that can occur during the
 * runtime of the conquest-application.
 * 
 * @author Andreas Wilhelm
 *
 */
public enum FailureType {
	//SpielFeld ungueltig (Abgrund)
	//SpielFeld ungueltig (Knoten falsch)
	//Bluetoothausfall
	//KLeine mögliche Kollisionsverwaltung
	//Keine Möglichkeit den nächsten Knoten anzufahren
	/**
	 * Invalid playing field
	 */
	INVALIDPITCH,
	
	/**
	 * If the bluetooth breaks down
	 */
	BLUETOOTHFAILURE,
	
	/**
	 * If a node cannot be visited, because it's obstructed
	 */
	NODEOBSTRUCTION;

}
