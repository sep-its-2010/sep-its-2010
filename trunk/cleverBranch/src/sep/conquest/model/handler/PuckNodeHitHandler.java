package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.MapNode;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.model.requests.StatusUpdateRequest;
import sep.conquest.util.Utility;

/**
 * Handles PuckNodeHit messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 * 
 */
public class PuckNodeHitHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;

  /**
   * Constructor calling constructor of super class.
   */
  public PuckNodeHitHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }

  /**
   * Handles PuckNodeHit messages.
   * 
   * Returns true, if request was handled. If class is not responsible, call
   * handleRequest of next handler. If next handler is null return false.
   * 
   * @param request
   *          The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
    PuckRequest hitNodeReq = (PuckRequest) request;
    if (!(hitNodeReq.getKind() == MessageType.RESPONSE_HIT_NODE)) {
      return super.handleRequest(request);
    } else {
      Puck robot = executor.getRobot();

      // Get robot status.
      RobotStatus statusOfRobot = robot.getRobotStatus().get(
          hitNodeReq.getSender());

      synchronized (statusOfRobot) {
        // Reset moving state.
        statusOfRobot.setMoving(false);
        
        // Get the last position of the robot to add the node in the map.
        int[] bufferPosition = statusOfRobot.getPosition();

        // Get the orientation of the robot to add the node in the map.
        Orientation puckOrientation = statusOfRobot.getOrientation();

        int newCoordinateX = bufferPosition[0];
        int newCoordinateY = bufferPosition[1];
        int turnCount = 0;

        switch (puckOrientation) {
        case UP:
          // Increase y-coordinate.
          newCoordinateY++;
          turnCount = 0;
          break;
        case DOWN:
          // Decrease y-coordinate.
          newCoordinateY--;
          turnCount = 2;
          break;
        case LEFT:
          // Decrease x-coordinate.
          newCoordinateX--;
          turnCount = 3;
          break;
        case RIGHT:
          // Increase x-coordinate.
          newCoordinateX++;
          turnCount = 1;
          break;
        default:
          /*
           * TODO To be implemented as soon as local/global localizing is
           * possible.
           */
        }

        // This block seeks out the type of the node sent with the message
        NodeType typeOfNewNode = null;
        int typeOfNode = hitNodeReq.getMessage()[Puck.NODE_HIT_BYTE];
        switch (typeOfNode) {
        case MapNode.CROSS:
          typeOfNewNode = NodeType.CROSS;
          break;
        case MapNode.LEFTT:
          typeOfNewNode = NodeType.LEFTT;
          break;
        case MapNode.RIGHTT:
          typeOfNewNode = NodeType.RIGHTT;
          break;
        case MapNode.TOPLEFTEDGE:
          typeOfNewNode = NodeType.TOPLEFTEDGE;
          break;
        case MapNode.TOPRIGHTEDGE:
          typeOfNewNode = NodeType.TOPRIGHTEDGE;
          break;
        case MapNode.TOPT:
          typeOfNewNode = NodeType.TOPT;
          break;
        case MapNode.BOTTOMLEFTEDGE:
          typeOfNewNode = NodeType.BOTTOMLEFTEDGE;
          break;
        case MapNode.BOTTOMRIGHTEDGE:
          typeOfNewNode = NodeType.BOTTOMRIGHTEDGE;
          break;
        case MapNode.BOTTOMT:
          typeOfNewNode = NodeType.BOTTOMT;
          break;
        default:
          // If illegal number was passed, throw Exception.
          throw new IllegalArgumentException(
              "Illegal map node sent from e-puck");
        }

        // Turns the corners and T-crosses, so they can be added to the map.
        NodeType finalNodeType = Utility.turnAround(turnCount, typeOfNewNode);

        // Add the new node to local map of the robot.
        robot.getMap().addNode(newCoordinateX, newCoordinateY, finalNodeType);

        // Assign new position to buffer.
        bufferPosition[0] = newCoordinateX;
        bufferPosition[1] = newCoordinateY;
        
        // Set new position and node type.
        statusOfRobot.setPosition(bufferPosition);
        statusOfRobot.setNodeType(finalNodeType);

        // Announce changes via broadcast.
        StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(robot
            .getID(), new UUID[0], statusOfRobot);
        robot.broadcast(statusUpdateReq);
      }
      return true;
    }
  }
}