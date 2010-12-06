package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.FailureRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles FailureRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class FailureRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public FailureRequestHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles FailureRequest messages.
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
	  if(!(request.getKind() == MessageType.FAILURE)){
		  return super.handleRequest(request);
	  } else {
		  FailureRequest failReq = (FailureRequest) request;
		  
		  return true;
	  }
  }

}
