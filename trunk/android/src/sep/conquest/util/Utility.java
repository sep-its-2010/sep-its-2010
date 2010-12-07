package sep.conquest.util;

/**
 * The utility class for the e-puck conquest application.
 * 
 * @author Andreas Wilhelm
 *
 */
public final class Utility {

	/**
	 * Creates the key for the mapping in the TreeMap.
	 * 
	 * @param x The x-coordinate of the node
	 * @param y The y-coordinate of the node
	 * @return The key for the mapping
	 */
	public static int makeKey(int x, int y) {
		return ((x ^ y) << 16) | (x & 0xFFFF);
	}
	
	/**
	 * Extracts the coordinate from a key.
	 * 
	 * @param key The key.
	 * @return The coordinates as an int-array.
	 */
	public static int[] extractCoordinates(int key) {
		int cord[] = new int[2];
		cord[0] = (key & 0xFFFF);
		cord[1] = (key >> 16) ^ cord[0];
		return cord;
	}
	
}
