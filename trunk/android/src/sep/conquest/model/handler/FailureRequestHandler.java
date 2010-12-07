package sep.conquest.model.handler;

import sep.conquest.model.FailureType;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.FailureRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles FailureRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
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
		String message;
		if (!(request.getKind() == MessageType.FAILURE)) {
			return super.handleRequest(request);
		} else {
			FailureRequest failReq = (FailureRequest) request;
			if (failReq.getFailureType() == FailureType.ABYSS) {
				message = "An Abyss was discovered..Uahhhh! "
						+ failReq.getSender();
				throw new IllegalStateException(message);
			} else if (failReq.getFailureType() == FailureType.INVALIDPITCH) {
				message = "The pitch is invalid! " + failReq.getSender();
				throw new IllegalStateException(message);
			} else if (failReq.getFailureType() == FailureType.BLUETOOTHFAILURE) {
				message = "I've lost my Bluetooth-Connection to my little E-Puck! "
						+ failReq.getSender();
				throw new IllegalStateException(message);
			} else if (failReq.getFailureType() == FailureType.NODEOBSTRUCTION) {
				message = "Cannot reach the node! " + failReq.getSender();
			}
			return true;
		}
  }

}
