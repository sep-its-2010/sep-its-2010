package sep.conquest.model.handler;

import sep.conquest.model.Environment;
import sep.conquest.model.IRequest;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles StatusUpdateRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class EnvStatusUpdateRequestHandler extends Handler {
  
  /**
   * Constructor calling constructor of super class.
   */
  public EnvStatusUpdateRequestHandler(Handler prev) {
    super(prev);
  }
  
  /**
   * Handles StatusUpdateRequest messages.
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
    if (request.getKind() != MessageType.STATUS_UPDATE)
    	return super.handleRequest(request);
    else {
    	Environment env = Environment.getInstance();
    	RobotStatus status = ((StatusUpdateRequest) request).getStatus();
    	env.getStatus().setRobotStatus(request.getSender(), status);
    	return true;
    }
  }

}


