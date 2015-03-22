package sep.conquest.model.handler;

import sep.conquest.model.IRSensor;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.RealPuck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.LocalizeCollisionRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.util.Utility;

/**
 * Handles LocalizeCollisionRequest messages.
 * 
 * @author Andreas Wilhelm
 *
 */
public class LocalizeCollisionRequestHandler extends Handler {
	
	/**
	 * The logic thread.
	 */
	private LogicThread lThread;
	
	private boolean[] sensorArray = new boolean[IRSensor.values().length];

  /**
   * Constructor calling constructor of super class.
   */
  public LocalizeCollisionRequestHandler(Handler next, LogicThread exec) {
    super(next);
    lThread = exec;
    
  }
  
  /**
   * Handles LocalizeCollisionRequest messages.
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
	  if(!(request.getKind() == MessageType.LOCALIZE_COLLISION)){
		  return super.handleRequest(request);
	  } else {
		  RealPuck robot = (RealPuck) lThread.getRobot();
		  if (request.getSender() != robot.getID()) {			  
			  LocalizeCollisionRequest req = (LocalizeCollisionRequest) request;
			  RobotStatus status = robot.getRobotStatus().get(robot.getID());
			  
			  if (!status.getSensorCollisionArray().equals(sensorArray)) {
				  int[] pos = Utility.extractCoordinates(req.getPosition());
				  switch(req.getDirection()) {
				  case UP:
					 pos[1]++;
					  break;
					  
				  case DOWN:
					  pos[1]--;
					  break;
					  
				  case RIGHT:
					  pos[0]++;
					  break;
					  
				  case LEFT:
					  pos[1]--;
					  break;
				  }
				  status.setPosition(pos);
				  robot.setPositionSet(true);
			  }
			  sensorArray = status.getSensorCollisionArray().clone();
		  }
		  
		  return true;  
	  }  
  }
}