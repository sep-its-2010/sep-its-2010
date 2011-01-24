package sep.conquest.model.handler;

import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.Environment;
import sep.conquest.model.IRequest;
import sep.conquest.model.Orientation;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles StatusUpdateRequest messages.
 * 
 * @author Florian Lorenz
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
   *          The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
    if (request.getKind() != MessageType.STATUS_UPDATE)
      return super.handleRequest(request);
    else {
      // Get Environment.
      Environment env = Environment.getInstance();

      // Get RobotStatus out of message.
      RobotStatus status = ((StatusUpdateRequest) request).getStatus();

      // Get Environment status.
      ConquestUpdate update = env.getStatus();

      // Get old status of the sender and copy it.
      RobotStatus oldStatus = update.getRobotStatus(request.getSender());
      if (oldStatus != null) {
        oldStatus = oldStatus.clone();
      }

      // Set new status.
      update.setRobotStatus(request.getSender(), status.clone());

      // check if a node was passed (if it's not the first attempt)
      if (status.getNodeType() != null) {

        // Check, if robot has moved.
        int newX = status.getPosition()[0];
        int newY = status.getPosition()[1];
        if ((newX != oldStatus.getPosition()[0])
            || (newY != oldStatus.getPosition()[1])
            || oldStatus.getOrientation() == Orientation.UNKNOWN) {

          if (env.getMap().addNode(newX, newY, status.getNodeType())) {
            // If node is a new node (not already contained in the map), add
            // increase number of explored nodes.
            update.setExploredNodes(request.getSender(), update
                .getExploredNodes(request.getSender()) + 1);
          }
        }
      }
      env.sendUpdate();
      return true;
    }
  }
}