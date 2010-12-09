package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.HelloRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles DriveRequest messages.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class HelloRequestHandler extends Handler {

	/**
	 * The LogicThread that executes the content.
	 */
	private LogicThread thread;

	/**
	 * Constructor calling constructor of super class.
	 */
	public HelloRequestHandler(Handler next, LogicThread exec) {
		super(next);
		thread = exec;
	}

	/**
	 * Handles HelloRequest messages.
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
		if (!(request.getKind() == MessageType.HELLO))
			return super.handleRequest(request);
		else {
			HelloRequest req = (HelloRequest) request;

			if (!thread.getRobot().getRobotStatus()
					.containsKey(req.getSender())) {
				
				// add status of robot
				thread.getRobot().getRobotStatus().put(req.getSender(),
						new RobotStatus());
				
				// send hello request
				thread.getRobot().sendHello();
			}

			return true;
		}
	}

}
