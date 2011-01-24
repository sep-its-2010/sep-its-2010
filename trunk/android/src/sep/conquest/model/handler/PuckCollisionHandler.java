package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.ComManager;
import sep.conquest.model.IRSensor;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.CollisionRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.model.requests.StatusUpdateRequest;
import sep.conquest.util.Utility;

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
        boolean[] colArray = new boolean[IRSensor.values().length];
        for (int i = 0; i < colArray.length; i++) {
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

        // set the node where the last collision happened
        if (robot.getCollisionNode() == null) {

          // collision behind robot (due to turn)
          if (colArray[0] || colArray[7]) {
            robot.setCollisionNode(getAdjacentNode(status, Orientation.DOWN));
          }
        }

        // Announce changes via broadcast.
        StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(robot
            .getID(), new UUID[0], status);
        robot.broadcast(statusUpdateReq);
      }
      return true;
    }
  }

  /**
   * Returns the neighbour-node (integer-key) in direction nodePos according to
   * the robot-orientation.
   * 
   * @param status
   *          the status of the robot
   * @param nodePos
   *          the orientation of the adjacent neighbour-node
   * @return the position of the neighbour-node.
   */
  private Integer getAdjacentNode(RobotStatus status, Orientation nodePos) {
    int[] pos = status.getPosition();

    switch (status.getOrientation()) {
    case UP:
      switch (nodePos) {
      case UP:
        return Utility.makeKey(pos[0], pos[1] + 1);
      case DOWN:
        return Utility.makeKey(pos[0], pos[1] - 1);
      case RIGHT:
        return Utility.makeKey(pos[0] + 1, pos[1]);
      case LEFT:
        return Utility.makeKey(pos[0] - 1, pos[1]);
      }
      break;
    case DOWN:
      switch (nodePos) {
      case UP:
        return Utility.makeKey(pos[0], pos[1] - 1);
      case DOWN:
        return Utility.makeKey(pos[0], pos[1] + 1);
      case RIGHT:
        return Utility.makeKey(pos[0] - 1, pos[1]);
      case LEFT:
        return Utility.makeKey(pos[0] + 1, pos[1]);
      }
      break;
    case RIGHT:
      switch (nodePos) {
      case UP:
        return Utility.makeKey(pos[0] + 1, pos[1]);
      case DOWN:
        return Utility.makeKey(pos[0] - 1, pos[1]);
      case RIGHT:
        return Utility.makeKey(pos[0], pos[1] - 1);
      case LEFT:
        return Utility.makeKey(pos[0], pos[1] + 1);
      }
      break;
    case LEFT:
      switch (nodePos) {
      case UP:
        return Utility.makeKey(pos[0] - 1, pos[1]);
      case DOWN:
        return Utility.makeKey(pos[0] + 1, pos[1]);
      case RIGHT:
        return Utility.makeKey(pos[0], pos[1] + 1);
      case LEFT:
        return Utility.makeKey(pos[0], pos[1] - 1);
      }
      break;
    }
    return 0;
  }

}
