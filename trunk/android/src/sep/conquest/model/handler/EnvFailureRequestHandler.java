package sep.conquest.model.handler;

import sep.conquest.model.Environment;
import sep.conquest.model.IRequest;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
import sep.conquest.model.requests.MessageType;

public class EnvFailureRequestHandler extends Handler {

  public EnvFailureRequestHandler(Handler nextHandler) {
    super(nextHandler);
  }

  /**
   * Handles FailureRequest messages.
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
    if (request.getKind() != MessageType.FAILURE)
      return super.handleRequest(request);
    else {
      // Get Environment.
      Environment env = Environment.getInstance();

      // Get status of sender and set error state.
      RobotStatus state = env.getStatus().getRobotStatus(request.getSender());
      synchronized (state) {
        state.setState(State.ERROR);
      }

      // Send update.
      env.sendUpdate();
      return true;
    }
  }
}