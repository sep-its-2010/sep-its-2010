package sep.conquest.model.handler;

import sep.conquest.model.FailureType;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.FailureRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles PuckAbyss messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker
 *
 */
public class PuckAbyssHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public PuckAbyssHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles PuckAbyss messages.
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
		if (!(request.getKind() == MessageType.RESPONSE_ABYSS)) {
			return super.handleRequest(request);
		} else {
			// Create new FailureRequest and send it to the other pucks.
			FailureType failType = FailureType.ABYSS;
			FailureRequest failReq = new FailureRequest(request.getSender(),
					request.getReceiver(), failType);
			executor.getRobot().broadcast(failReq);
			return true;
		}
	}
}