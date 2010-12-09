package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.IntentRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles IntentRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 * 
 */
public class IntentRequestHandler extends Handler {

	/**
	 * The LogicThread that executes the content.
	 */
	private LogicThread executor;

	/**
	 * Constructor calling constructor of super class.
	 */
	public IntentRequestHandler(Handler next, LogicThread exec) {
		super(next);
		executor = exec;
	}

	/**
	 * Handles IntentRequest messages.
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
		if (!(request.getKind() == MessageType.INTENT)) {
			return super.handleRequest(request);
		} else {
			IntentRequest intReq = (IntentRequest) request;
			// Set the New IntentPosition of the puck
			executor.getRobot().getRobotStatus().get(intReq.getSender())
					.setIntentPosition(intReq.getIntendedNode());
			
			// Generates a StatusUpdateRequest Message and sends it via broadcast
			StatusUpdateRequest statusReq = new StatusUpdateRequest(executor
					.getRobot().getID(), null, executor.getRobot()
					.getRobotStatus().get(executor.getRobot()));
			executor.getRobot().broadcast(statusReq);
			return true;
		}
	}

}
