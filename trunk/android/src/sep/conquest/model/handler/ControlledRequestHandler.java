package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Puck;
import sep.conquest.model.requests.ControlledRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles ControlledRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 * 
 */
public class ControlledRequestHandler extends Handler {

	/**
	 * The LogicThread that executes the content.
	 */
	private LogicThread executor;

	/**
	 * Constructor calling constructor of super class.
	 */
	public ControlledRequestHandler(Handler next, LogicThread exec) {
		super(next);
		executor = exec;
	}

	/**
	 * Handles ControlledRequest messages.
	 * 
	 * Returns true, if request was handled. If class is not responsible, call
	 * handleRequest of next handler. If next handler is null return false.
	 * 
	 * @param request
	 *            The request to handle.
	 * @return True, if request was handled, false otherwise.
	 */
	@Override
	public boolean handleRequest(IRequest request) {
		if (request.getKind() == MessageType.CONTROL_ENABLE) {
		  ControlledRequest req = (ControlledRequest) request;
		  
		  // Get controlled state out of the message and set according robot state.
		  Puck robot = executor.getRobot();
		  
		  if (robot.getID() == req.getReceiver()[0]) {
	      robot.setControlled(req.isControlled());
		  }
      return true;
		} else {
	    return super.handleRequest(request);
		}	
	}
}