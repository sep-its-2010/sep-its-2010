package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.ComManager;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.CollisionRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles PuckCollison messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 * 
 */
public class PuckCollisionHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;

  /**
   * Constructor calling constructor of super class.
   */
  public PuckCollisionHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }

  /**
   * Handles PuckCollision messages.
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
    PuckRequest colRes = (PuckRequest) request;
    if (!(colRes.getKind() == MessageType.RESPONSE_COLLISION)) {
      return super.handleRequest(request);
    } else {
      Puck robot = executor.getRobot();
      synchronized (robot) {

        // Get collision array out of e-puck message.
        byte[] bufferMessage = colRes.getMessage();
        boolean[] colArray = new boolean[8];
        for (int i = 0; i < 8; i++) {
          if (bufferMessage[i] != 0)
            colArray[i] = true;
        }

        // Create new collision message.
        CollisionRequest req = new CollisionRequest(robot.getID(), new UUID[0],
            colArray);
        ComManager comMan = ComManager.getInstance();
        comMan.broadcast(req);

        // Set new orientation (turned 180 degree).
        RobotStatus status = robot.getRobotStatus().get(robot.getID());
        status.setOrientation(Orientation.turn(status.getOrientation()));

        // Announce changes via broadcast.
        StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(robot
            .getID(), new UUID[0], status);
        robot.broadcast(statusUpdateReq);
      }
      return true;
    }
  }

}
