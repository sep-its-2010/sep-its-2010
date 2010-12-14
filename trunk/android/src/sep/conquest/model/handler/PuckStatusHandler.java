package sep.conquest.model.handler;


import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.MapNode;
import sep.conquest.model.NodeType;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;

/**
 * Handles PuckStatus messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 *
 */
public class PuckStatusHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public PuckStatusHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles PuckStatus messages.
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
	  PuckRequest statusReq = (PuckRequest) request;
	  if(!(statusReq.getKind() == MessageType.RESPONSE_STATUS)){
		  return super.handleRequest(request);
	  } else {
		    /*
			 * Message looks like: 
			 * Byte[0-1]: MessageType 
			 * Byte[2-5]: System Up Time (little endian) 
			 * Byte[6-8]: Abyss (0 == false, otherwise == true, left middle right) 
			 * Byte[9-16]: Collision (0 == false, otherwise == true) 
			 * Byte[17]: NodeType (0-8 NodeType, 9 unknown)
			 * What to do: Check which NodeType, then check the priority and how
			 * many robots are online, then add the node to the structure.
			 */
		  	byte[] bufferMessage = statusReq.getMessage();
		  	byte[] collisionArray = new byte[8];
		  	byte[] abyssArray = new byte[3];
		  	boolean isCollision = false;
		  	boolean[] collisionArrayForPuck = new boolean[8];
		  	boolean[] abyssArrayForPuck = new boolean[3];
		  	boolean isAbyss = false;
		  	
		  	// For debugging
		  	// Fill the collisionArray with the information of the message
		  	// and checks whether there is a collision
		  	for(int i = 9; i<17;i++){
		  		collisionArray[i-9] = bufferMessage[i];
		  		if (bufferMessage[i] != 0){
		  			isCollision = true;
		  			collisionArrayForPuck[i-9] = true;
		  		} else {
		  			collisionArrayForPuck[i-9] = false;
		  		}
		  	}
		  	
		  	// Updates the RobotStatus of the actual robot
		  	if(isCollision){
		  		executor.getRobot().getRobotStatus().get(statusReq.getSender())
				.setSensorCollisionArray(collisionArrayForPuck);
		  	}
		  	
		    //For debugging
		  	//fill the abyssArray with the information of the message and
		  	//checks whether there is a collision
		  	for(int j = 6; j<9;j++){
		  		abyssArray[j-6] = bufferMessage[j];
		  		if (bufferMessage[j] != 0){
		  			isAbyss = true;
		  			abyssArrayForPuck[j-6] = true;
		  		} else {
		  			abyssArrayForPuck[j-6] = false;
		  		}
		  	}
		  	
			//E-puck's near an abyss
		  	if(isAbyss){
		  		
		  	}
		  	
  	
		  	// This block seeks out the type of the node sent with the message
		  	NodeType typeOfLastVisitedNode = null;
			int typeOfNode = bufferMessage[17];
			switch (typeOfNode) {
			case MapNode.CROSS:
				typeOfLastVisitedNode = NodeType.CROSS;
				break;
			case MapNode.LEFTT:
				typeOfLastVisitedNode = NodeType.LEFTT;
				break;
			case MapNode.RIGHTT:
				typeOfLastVisitedNode = NodeType.RIGHTT;
				break;
			case MapNode.BOTTOMLEFTEDGE:
				typeOfLastVisitedNode = NodeType.BOTTOMLEFTEDGE;
				break;
			case MapNode.BOTTOMRIGHTEDGE:
				typeOfLastVisitedNode = NodeType.BOTTOMRIGHTEDGE;
				break;
			case MapNode.BOTTOMT:
				typeOfLastVisitedNode = NodeType.BOTTOMT;
				break;
			case MapNode.TOPLEFTEDGE:
				typeOfLastVisitedNode = NodeType.TOPLEFTEDGE;
				break;
			case MapNode.TOPRIGHTEDGE:
				typeOfLastVisitedNode = NodeType.TOPRIGHTEDGE;
				break;
			case MapNode.TOPT:
				typeOfLastVisitedNode = NodeType.TOPT;
				break;
			}
			
		  	/*
		  	 * In the PuckFactory the Orientation in the PuckStatus is set to
		  	 * the default value DOWN.
		  	 * Don't know the position of the e-pucks 
		  	 */
			
			RobotStatus bufferStatus = executor.getRobot().getRobotStatus().get(statusReq.getSender());
		  	bufferStatus.setNodeType(typeOfLastVisitedNode);
		  	executor.getRobot().getMap().addNode(0, 0, typeOfLastVisitedNode);
		  	
		  return true;
	  }
  }
}
