package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.MessageType;

/**
 * Handles ControlledRequest messages.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class ControlledRequestHandler extends Handler {

	/**
	 * The LogicThread that executes the content.
	 */
	private LogicThread thread;

	/**
	 * Constructor calling constructor of super class.
	 */
	public ControlledRequestHandler(Handler prev, LogicThread exec) {
		super(prev);
		thread = exec;
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
		if (!(request.getKind() == MessageType.CONTROL_ENABLE))
			return super.handleRequest(request);
		else {
			thread.getRobot().setControlled(true);			
			return true;
		}	
	}
}
