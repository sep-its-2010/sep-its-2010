package sep.conquest.util;

import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;

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
   * @param x
   *          The x-coordinate of the node
   * @param y
   *          The y-coordinate of the node
   * @return The key for the mapping
   */
  public static int makeKey(int x, int y) {
    return ((x ^ y) << 16) | (x & 0xFFFF);
  }

  /**
   * Extracts the coordinate from a key.
   * 
   * @param key
   *          The key.
   * @return The coordinates as an int-array.
   */
  public static int[] extractCoordinates(int key) {
	  int cord[] = new int[2];
	    int reg = (key & 0xFFFF);
	    //Signed Bit of X-Coordinate
	    int negativeX = (key & 0x8000)>>15;
		  //x-coordinate is negative:
		  if(negativeX == 1){
			  cord[0] = reg - 0x10000;
				  cord[1] = (key >> 16) ^ cord[0];
		  } else {
			  cord[0] = (key & 0xFFFF);
			    cord[1] = (key >> 16) ^ cord[0];
		  }
	    return cord;
  }

  /**
   * Turns the corners and T-Crosses to the global direction of the map (so the
   * NodeType is translated to the view of the puck if its direction is DOWN)
   * 
   * @param turnCount
   *          The number of needed turns
   * @param typeOfNode
   *          The actual NodeType of the Orientation of the puck
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

  public static NodeType calculateNodeTypesToPuckOrientation(Orientation ori,
      NodeType typeOfNode) {
    switch (typeOfNode) {
    case TOPLEFTEDGE:
    	switch (ori){
    	case RIGHT:
    		return NodeType.TOPRIGHTEDGE;
    	case LEFT:
    		return NodeType.BOTTOMLEFTEDGE;
    	case UP:
    		return NodeType.TOPLEFTEDGE;
    	case DOWN:
    		return NodeType.BOTTOMRIGHTEDGE;
    	}
    case TOPRIGHTEDGE:
    	switch (ori){
    	case RIGHT:
    		return NodeType.BOTTOMRIGHTEDGE;
    	case LEFT:
    		return NodeType.TOPLEFTEDGE;
    	case UP:
    		return NodeType.TOPRIGHTEDGE;
    	case DOWN:
    		return NodeType.BOTTOMLEFTEDGE;
    	}
    case BOTTOMLEFTEDGE:
    	switch (ori){
    	case RIGHT:
    		return NodeType.TOPLEFTEDGE;
    	case LEFT:
    		return NodeType.BOTTOMRIGHTEDGE;
    	case UP:
    		return NodeType.BOTTOMLEFTEDGE;
    	case DOWN:
    		return NodeType.TOPRIGHTEDGE;
    	}
    case BOTTOMRIGHTEDGE:
    	switch (ori){
    	case RIGHT:
    		return NodeType.BOTTOMLEFTEDGE;
    	case LEFT:
    		return NodeType.TOPRIGHTEDGE;
    	case UP:
    		return NodeType.BOTTOMRIGHTEDGE;
    	case DOWN:
    		return NodeType.TOPLEFTEDGE;
    	}
    case BOTTOMT:
    	switch (ori){
    	case RIGHT:
    		return NodeType.LEFTT;
    	case LEFT:
    		return NodeType.RIGHTT;
    	case UP:
    		return NodeType.BOTTOMT;
    	case DOWN:
    		return NodeType.TOPT;
    	}
    case LEFTT:
    	switch (ori){
    	case RIGHT:
    		return NodeType.TOPT;
    	case LEFT:
    		return NodeType.BOTTOMT;
    	case UP:
    		return NodeType.LEFTT;
    	case DOWN:
    		return NodeType.RIGHTT;
    	}
    case RIGHTT:
    	switch (ori){
    	case RIGHT:
    		return NodeType.BOTTOMT;
    	case LEFT:
    		return NodeType.TOPT;
    	case UP:
    		return NodeType.RIGHTT;
    	case DOWN:
    		return NodeType.LEFTT;
    	}
    case TOPT:
    	switch (ori){
    	case RIGHT:
    		return NodeType.RIGHTT;
    	case LEFT:
    		return NodeType.LEFTT;
    	case UP:
    		return NodeType.TOPT;
    	case DOWN:
    		return NodeType.BOTTOMT;
    	}
    case CROSS:
        return NodeType.CROSS;
    default:
      throw new IllegalArgumentException("Failure: " + typeOfNode + " , " + ori);
    }
  }

  public static NodeType calculateNodeTypesToRealWorld(
      NodeType typeOfNodeEpuck, Orientation globalOrientationOfPuck) {
    int turnCount = Orientation.addGlobalDirection(globalOrientationOfPuck,
        Orientation.UP);
    return turnAround(turnCount, typeOfNodeEpuck);
  }

}
