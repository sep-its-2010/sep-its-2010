package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles PuckOK messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class PuckOkHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;

  /**
   * Constructor calling constructor of super class.
   */
  public PuckOkHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }

  /**
   * Handles PuckOk messages.
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
    if (request.getKind().equals(MessageType.RESPONSE_OK)) {
      Puck robot = executor.getRobot();
      RobotStatus state = robot.getRobotStatus().get(robot.getID());
      
      synchronized (state) {
        // Reset moving state.
        state.setMoving(false);
        
        // Set new orientation.
        state.setOrientation(executor.getTurnOrientation());

        // Announce changes via broadcast.
        StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(robot
            .getID(), new UUID[0], state);
        robot.broadcast(statusUpdateReq);
      }
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}