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
    cord[0] = (key & 0xFFFF);
    cord[1] = (key >> 16) ^ cord[0];
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
      if (ori == Orientation.RIGHT) {
        return NodeType.TOPRIGHTEDGE;
      } else if (ori == Orientation.UP) {
        return NodeType.TOPLEFTEDGE;
      } else {
        throw new IllegalArgumentException("TopLeftEdge can be reached from"
            + "Right and Up not from: " + ori);
      }
    case TOPRIGHTEDGE:
      if (ori == Orientation.UP) {
        return NodeType.TOPRIGHTEDGE;
      } else if (ori == Orientation.LEFT) {
        return NodeType.TOPLEFTEDGE;
      } else {
        throw new IllegalArgumentException("TopRightEdge can be reached from"
            + "Left and Up not from: " + ori);
      }
    case BOTTOMLEFTEDGE:
      if (ori == Orientation.RIGHT) {
        return NodeType.TOPLEFTEDGE;
      } else if (ori == Orientation.DOWN) {
        return NodeType.TOPRIGHTEDGE;
      } else {
        throw new IllegalArgumentException("BottomLeftEdge can be reached from"
            + "Right and Down not from: " + ori);
      }
    case BOTTOMRIGHTEDGE:
      if (ori == Orientation.LEFT) {
        return NodeType.TOPRIGHTEDGE;
      } else if (ori == Orientation.DOWN) {
        return NodeType.TOPLEFTEDGE;
      } else {
        throw new IllegalArgumentException(
            "BottomRightEdge can be reached from" + "Left and Down not from: "
                + ori);
      }
    case BOTTOMT:
      if (ori == Orientation.LEFT) {
        return NodeType.RIGHTT;
      } else if (ori == Orientation.DOWN) {
        return NodeType.TOPT;
      } else if (ori == Orientation.RIGHT) {
        return NodeType.LEFTT;
      } else {
        throw new IllegalArgumentException("BOTTOMT can be reached from"
            + "Left and Down and Right not from: " + ori);
      }
    case CROSS:
      return NodeType.CROSS;
    case LEFTT:
      if (ori == Orientation.RIGHT) {
        return NodeType.TOPT;
      } else if (ori == Orientation.DOWN) {
        return NodeType.RIGHTT;
      } else if (ori == Orientation.UP) {
        return NodeType.LEFTT;
      } else {
        throw new IllegalArgumentException("LEFTT can be reached from"
            + "Up and Down and Right not from: " + ori);
      }
    case RIGHTT:
      if (ori == Orientation.LEFT) {
        return NodeType.TOPT;
      } else if (ori == Orientation.DOWN) {
        return NodeType.LEFTT;
      } else if (ori == Orientation.UP) {
        return NodeType.RIGHTT;
      } else {
        throw new IllegalArgumentException("RIGHTT can be reached from"
            + "Left and Down and Up not from: " + ori);
      }
    case TOPT:
      if (ori == Orientation.LEFT) {
        return NodeType.LEFTT;
      } else if (ori == Orientation.UP) {
        return NodeType.TOPT;
      } else if (ori == Orientation.RIGHT) {
        return NodeType.RIGHTT;
      } else {
        throw new IllegalArgumentException("TOPT can be reached from"
            + "Left and Up and Right not from: " + ori);
      }
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
