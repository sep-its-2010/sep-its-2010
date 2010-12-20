package sep.conquest.model.handler;

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
import sep.conquest.util.ConquestLog;
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
		if (!(hitNodeReq.getKind() == MessageType.RESPONSE_HIT_NODE)) {
			return super.handleRequest(request);
		} else {
		  Puck robot = executor.getRobot();
		  synchronized(robot) {
			RobotStatus statusOfRobot = robot.getRobotStatus()
					.get(hitNodeReq.getSender());
			
	    ConquestLog.addMessage(this, "Puck --> "+ robot.getName() + ": Hit Node");

			
			// Get the actual position of the robot to add the node in the map
			int[] bufferPosition = statusOfRobot.getPosition();
			// Get the orientation of the robot to add the node in the map
			Orientation puckOrientation = statusOfRobot.getOrientation();

			NodeType typeOfNewNode = null;
			/*
			 * Message looks like:
			 * 
			 * Byte[0-1]: MessageType 
			 * Byte[2]: NodeType
			 * Byte[3]: Previous Node
			 */

			int newCoordinateX = bufferPosition[0];
			int newCoordinateY = bufferPosition[1];
			int turnCount = 0;
			
			switch (puckOrientation) {
			case UP:
				/*
				 * position x = (bufferPosition[0]) position y =
				 * (bufferPosition[1] - 1)
				 */
				newCoordinateY++;
				turnCount = 0;
				break;
			case DOWN:
				/*
				 * position x = (bufferPosition[0]) position y =
				 * (bufferPosition[1] + 1)
				 */
				newCoordinateY--;
				turnCount = 2;
				break;
			case LEFT:
				/*
				 * position x = (bufferPosition[0] - 1) position y =
				 * (bufferPosition[1])
				 */
				newCoordinateX--;
				turnCount = 3;
				break;
			case RIGHT:
				/*
				 * position x = (bufferPosition[0] + 1) position y =
				 * (bufferPosition[1])
				 */
				newCoordinateX++;
				turnCount = 1;
				break;
			default:
				/*
				 * TODO Lokalisierung bei HitNodeHandler
				 * He don't know where he is.. that's a big problem...
				 */
			}

			/*
			 *  This block seeks out whether the e-puck went to its intentPosition
			 *  or returned to the node it came from (when an e-puck discovered a
			 *  collision, it tries to get back to his last consistent node)
			 */
			boolean returnedToLastNode = false;;
			int returned = hitNodeReq.getMessage()[3];
			if(returned != 0){
				returnedToLastNode = true;
			} else {
				returnedToLastNode = false;
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
			case MapNode.BOTTOMLEFTEDGE:
				typeOfNewNode = NodeType.BOTTOMLEFTEDGE;
				break;
			case MapNode.BOTTOMRIGHTEDGE:
				typeOfNewNode = NodeType.BOTTOMRIGHTEDGE;
				break;
			case MapNode.BOTTOMT:
				typeOfNewNode = NodeType.BOTTOMT;
				break;
			}
			
			// Turns the corners and T-crosses, so they can be added to the map
			NodeType finalNodeType = Utility.turnAround(turnCount, typeOfNewNode);
						
			/*
			 * If !(returnedToLastNode) the information of the new node has to
			 * be written and added to the map
			 * If (returnedToLastNode) the puck went to his last position. In
			 * this case the information of the last node has to be written and
			 * added to the map.
			 * The difference is the x and y coordinate.
			 */			
			if(!(returnedToLastNode)){
				// Add the new node to local map of the robot
				robot.getMap().addNode(newCoordinateX,
						newCoordinateY, finalNodeType);
				// Sets the new position in the status of the robot
				bufferPosition[0] = newCoordinateX;
				bufferPosition[1] = newCoordinateY;
			} else {
				// Add the new node to local map of the robot with its old
				// coordinates because it returned to the node
				robot.getMap().addNode(bufferPosition[0],
						bufferPosition[1], finalNodeType);	
			}


			
			statusOfRobot.setPosition(bufferPosition);
			statusOfRobot.setNodeType(finalNodeType);
			statusOfRobot.setMoving(false);
			// Should I create a new RobotStatus or can I use the Status of the
			// actual sender?!
			StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(
					robot.getID(), null, statusOfRobot);
			robot.broadcast(statusUpdateReq);
		  }
			return true;
		}
			
		
	}

	
}