package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.SpeedRequest;

/**
 * Handles SpeedRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class SpeedRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public SpeedRequestHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles SpeedRequest messages.
   * 
   * Returns true, if request was handled. If class is not responsible,
   * call handleRequest of next handler. If next handler is null return
   * false.
   * 
   * @param sender The sender of the request message.
   * @param request The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
	  if(!(request.getKind()==MessageType.CONTROL_SPEED)){
		  return super.handleRequest(request);
	  } else {
		  SpeedRequest speedReq = (SpeedRequest) request;
		  executor.getRobot().setSpeed(speedReq.getSpeed());
		  return true;
	  }
  }
}
