package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles StatusUpdateRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 *
 */
public class StatusUpdateRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public StatusUpdateRequestHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
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
		if (!(request.getKind() == MessageType.STATUS_UPDATE)) {
			return super.handleRequest(request);
		} else {
			StatusUpdateRequest statusReq = (StatusUpdateRequest) request;
			// Side effect in method RobotStatus.setRobotStatus?! Check this!
			RobotStatus oldStatus = executor.getRobot().getRobotStatus().get(
					statusReq.getSender());
			// The status which is sent with the message
			RobotStatus newBufferRobotStatus = statusReq.getStatus();
			oldStatus.setRobotStatus(newBufferRobotStatus);
			return true;
		}
	}

}


