package sep.conquest.model.handler;

import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.Environment;
import sep.conquest.model.IRequest;
import sep.conquest.model.Orientation;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
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
	public EnvStatusUpdateRequestHandler(Handler next) {
		super(next);
	}

	/**
	 * Handles StatusUpdateRequest messages.
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
		if (request.getKind() != MessageType.STATUS_UPDATE)
			return super.handleRequest(request);
		else {
			Environment env = Environment.getInstance();
			RobotStatus status = ((StatusUpdateRequest) request).getStatus();
			ConquestUpdate update = env.getStatus();
			RobotStatus oldStatus = update.getRobotStatus(request.getSender());
			if (oldStatus != null)
				oldStatus = oldStatus.clone();
			update.setRobotStatus(request.getSender(), status.clone());	
			
			// check if a node was passed (if it's not the first attempt)
			if (status.getNodeType() != null) {

				// check if robot has moved				
				if (!(status.getPosition()[0] == oldStatus.getPosition()[0] &&
					status.getPosition()[1] == oldStatus.getPosition()[1]) ||
					oldStatus.getOrientation() == Orientation.UNKNOWN) {				
					if (env.getMap().addNode(status.getPosition()[0],
							status.getPosition()[1], status.getNodeType())) {
						
						update.setExploredNodes(request.getSender(), 
								update.getExploredNodes(request.getSender())+1);
					}
				}
			}
			env.notifyObservers(update);
			return true;
		}
	}

}
