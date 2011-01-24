package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.RealPuck;
import sep.conquest.model.requests.LocalizeCollisionRequest;
import sep.conquest.model.requests.MessageType;



/**
 * Handles LocalizeCollisionRequest messages.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class LocalizeCollisionRequestHandler extends Handler {

	/**
	 * The logic thread.
	 */
	private LogicThread lThread;



	/**
	 * Constructor calling constructor of super class.
	 */
	public LocalizeCollisionRequestHandler(Handler next, LogicThread exec) {
		super(next);
		lThread = exec;

	}

	/**
	 * Handles LocalizeCollisionRequest messages.
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
		if (!(request.getKind() == MessageType.LOCALIZE_COLLISION)) {
			return super.handleRequest(request);
		} else {
			RealPuck robot = (RealPuck) lThread.getRobot();
			if (request.getSender() != robot.getID()) {
				LocalizeCollisionRequest req = (LocalizeCollisionRequest) request;
				robot.requestStatus();
				robot.collisionDir = req.getDirection();
				robot.collisionPos = req.getPosition();
			}
			return true;
		}
	}
}