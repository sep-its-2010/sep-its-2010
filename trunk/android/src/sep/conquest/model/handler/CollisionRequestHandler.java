package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.CollisionRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles CollisionRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 *
 */
public class CollisionRequestHandler extends Handler {
	
	/**
	 * The logic thread.
	 */
	private LogicThread lThread;

  /**
   * Constructor calling constructor of super class.
   */
  public CollisionRequestHandler(Handler next, LogicThread exec) {
    super(next);
    lThread = exec;
    
  }
  
  /**
   * Handles CollisionRequest messages.
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
	  if(!(request.getKind() == MessageType.COLLISION)){
		  return super.handleRequest(request);
	  } else {
		  CollisionRequest colReq = (CollisionRequest) request;
		  
		  // Refresh the collision sensor array.
		  lThread.getRobot().getRobotStatus().get(colReq.getSender())
					.setSensorCollisionArray(colReq.getSensor());
		  return true;  
	  }  
  }
}