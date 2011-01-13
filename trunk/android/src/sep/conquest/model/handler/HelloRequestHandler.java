package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.HelloRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles HelloRequest messages.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class HelloRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;

  /**
   * Constructor calling constructor of super class.
   */
  public HelloRequestHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }

  /**
   * Handles HelloRequest messages.
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
    if (request.getKind() == MessageType.HELLO) {
      HelloRequest req = (HelloRequest) request;
      Puck robot = executor.getRobot();

      // If sender is unknown, create new state.
      if (!robot.getRobotStatus().containsKey(req.getSender())) {

        // Add status of new robot.
        robot.getRobotStatus().put(req.getSender(),
            new RobotStatus());

        // Send hello request.
        robot.sendHello();
      }
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}