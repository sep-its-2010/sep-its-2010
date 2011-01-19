package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.requests.DriveRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.StatusUpdateRequest;

/**
 * Handles DriveRequest messages.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 * 
 */
public class DriveRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;

  /**
   * Constructor calling constructor of super class.
   */
  public DriveRequestHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }

  /**
   * Handles DriveRequest messages.
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
    if (!(request.getKind() == MessageType.CONTROL_DIR)) {
      return super.handleRequest(request);
    } else {
      DriveRequest driveReq = (DriveRequest) request;
      Puck robot = executor.getRobot();

      if (robot.getID() == driveReq.getReceiver()[0]) {

        // The drive command.
        int command = driveReq.getCommand();

        // Pass drive command to robot.
        robot.driveCommand(command);

        // The current state of the robot.
        RobotStatus state = robot.getRobotStatus().get(robot.getID());

        if (command == Orientation.UP.getOrientation()) {
          // If command is forward, set intended position.
          int x = state.getPosition()[0];
          int y = state.getPosition()[1];

          // Compute intent position.
          switch (state.getOrientation()) {
          case UP:
            y++;
            break;
          case LEFT:
            x--;
            break;
          case RIGHT:
            x++;
            break;
          case DOWN:
            y--;
            break;
          default:
            break;
          }
          int[] intentPos = { x, y };
          state.setIntentPosition(intentPos);
        } else {
          // Otherwise compute new orientation.
          Orientation ori = state.getOrientation();
          ori = Orientation.getTurnedOrientation(command, ori);
          executor.setTurnOrientation(ori);
        }
        // Announce changes via broadcast.
        robot.getRobotStatus().get(robot.getID()).setMoving(true);
        StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(robot
            .getID(), new UUID[0], robot.getRobotStatus().get(robot.getID()));
        robot.broadcast(statusUpdateReq);
      }
      return true;
    }
  }
}