package sep.conquest.model.handler;

import java.util.Set;
import java.util.UUID;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.MapNode;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.model.requests.StatusUpdateRequest;

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
   * Returns true, if request was handled. If class is not responsible,
   * call handleRequest of next handler. If next handler is null return
   * false.
   * 
   * @param request The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
	  	PuckRequest hitNodeReq = (PuckRequest) request;
		if (!(hitNodeReq.getKind() == MessageType.PUCK_HITNODE)) {
			return super.handleRequest(request);
		} else {
			RobotStatus statusOfRobot = executor.getRobot().getRobotStatus()
					.get(hitNodeReq.getSender());
			// Get the actual position of the robot to add the node in the map
			int[] bufferPosition = statusOfRobot.getPosition();
			// Get the orientation of the robot to add the node in the map
			Orientation puckOrientation = statusOfRobot.getOrientation();

			NodeType typeOfNewNode = null;
			/*
			 * Message looks like:
			 * 
			 * Byte[0-1]: MessageType Byte[2]: NodeType
			 */

			int newCoordinateX = bufferPosition[0];
			int newCoordinateY = bufferPosition[1];
			int turnCount = 0;

			switch (puckOrientation) {
			case UP:
				/*
				 * position x = (bufferPosition[0]) position y =
				 * (bufferPosition[1] + 1)
				 */
				newCoordinateY++;
				turnCount = 2;
				break;
			case DOWN:
				/*
				 * position x = (bufferPosition[0]) position y =
				 * (bufferPosition[1] - 1)
				 */
				newCoordinateY--;
				turnCount = 0;
				break;
			case LEFT:
				/*
				 * position x = (bufferPosition[0] + 1) position y =
				 * (bufferPosition[1])
				 */
				newCoordinateX++;
				turnCount = 3;
				break;
			case RIGHT:
				/*
				 * position x = (bufferPosition[0] - 1) position y =
				 * (bufferPosition[1])
				 */
				newCoordinateX--;
				turnCount = 1;
				break;
			default:
				/*
				 * He don't know where he is.. that's a big problem...
				 */
			}

			// This block seeks out the type of the node sent with the message
			int typeOfNode = hitNodeReq.getMessage()[2];
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
			}

			// Turns the corners and T-Crosses, so they can be added to the map
			NodeType finalNodeType = this.turnAround(turnCount, typeOfNewNode);

			// Add the new node to local map of the robot
			executor.getRobot().getMap().addNode(newCoordinateX,
					newCoordinateY, finalNodeType);

			// Gets the UUID's of the other robots and saves them into an array
			UUID[] receivers = new UUID[executor.getRobot().getRobotStatus()
					.keySet().size()];

			Set<UUID> it = executor.getRobot().getRobotStatus().keySet();
			int i = 0;
			for (UUID key : it) {
				receivers[i] = key;
				i++;
			}

			// Sets the new position in the status of the robot and the actual
			// NodeType
			bufferPosition[0] = newCoordinateX;
			bufferPosition[1] = newCoordinateY;
			statusOfRobot.setPosition(bufferPosition);
			statusOfRobot.setNodeType(finalNodeType);
			statusOfRobot.setMoving(false);
			// Should I create a new RobotStatus or can I use the Status of the
			// actual sender?!
			StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(
					executor.getRobot().getID(), receivers, statusOfRobot);
			executor.getRobot().broadcast(statusUpdateReq);
			return true;
		}
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
	private NodeType turnAround(int turnCount, NodeType typeOfNode) {
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
