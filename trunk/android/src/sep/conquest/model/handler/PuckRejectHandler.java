package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Puck;
import sep.conquest.model.requests.MessageType;
import sep.conquest.util.ConquestLog;

/**
 * Handles REJECT messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker
 *
 */
public class PuckRejectHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public PuckRejectHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles REJECT messages.
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
    if (request.getKind().equals(MessageType.RESPONSE_REJECT)) {
      Puck robot = executor.getRobot();
      synchronized(robot) {
      ConquestLog.addMessage(this, "Puck --> "+ robot.getName() + ": Rejected");
      }
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}
