package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
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
   * Returns true, if request was handled. If class is not responsible,
   * call handleRequest of next handler. If next handler is null return
   * false.
   * 
   * @param request The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
	  if(!(request.getKind() == MessageType.CONTROL_DIR)){
		  return super.handleRequest(request);
	  } else {
		  DriveRequest driveReq = (DriveRequest) request;
		  // calls the method in the Puck class to handle the driveCommand
			executor.getRobot().driveCommand(driveReq.getCommand());

			// Theoretical:
			executor.getRobot().getRobotStatus().get(executor.getRobot())
					.setMoving(true);
			StatusUpdateRequest statusUpdateReq = new StatusUpdateRequest(
					executor.getRobot().getID(), null, executor.getRobot()
							.getRobotStatus().get(executor.getRobot()));
			executor.getRobot().broadcast(statusUpdateReq);
			return true;
		}
	}

}
