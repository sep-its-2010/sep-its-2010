package sep.conquest.util;

import sep.conquest.model.NodeType;

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
	
	
	/**
	 * Turns the corners and T-Crosses to the global direction of the map (so
	 * the NodeType is translated to the view of the puck if its direction is
	 * DOWN)
	 * 
	 * @param turnCount
	 *            The number of needed turns
	 * @param typeOfNode
	 *            The actual NodeType of the Orientation of the puck
	 * @return The turned NodeType
	 */
	public static NodeType turnAround(int turnCount, NodeType typeOfNode) {
		NodeType bufferNodeType = typeOfNode;
		for (int i = 0; i < turnCount; i++) {
			switch (bufferNodeType) {
			case CROSS:
				return bufferNodeType;
			case LEFTT:
				bufferNodeType = NodeType.BOTTOMT;
				break;
			case RIGHTT:
				bufferNodeType = NodeType.TOPT;
				break;
			case TOPT:
				bufferNodeType = NodeType.LEFTT;
				break;
			case BOTTOMT:
				bufferNodeType = NodeType.RIGHTT;
				break;
			case TOPLEFTEDGE:
				bufferNodeType = NodeType.BOTTOMLEFTEDGE;
				break;
			case TOPRIGHTEDGE:
				bufferNodeType = NodeType.TOPLEFTEDGE;
				break;
			case BOTTOMLEFTEDGE:
				bufferNodeType = NodeType.BOTTOMRIGHTEDGE;
				break;
			case BOTTOMRIGHTEDGE:
				bufferNodeType = NodeType.TOPRIGHTEDGE;
				break;
			}
		}
		return bufferNodeType;
	}
}
