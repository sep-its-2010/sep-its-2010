package sep.conquest.model.handler;


import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.MessageType;

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
  public PuckCollisionHandler(Handler prev, LogicThread exec) {
    super(prev);
    executor = exec;
  }
  
  /**
   * Handles PuckCollision messages.
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
	  if(!(request.getKind()==MessageType.RESPONSE_COLLISION)){
		  return super.handleRequest(request);
	  } else {
		  //The epuck stops on hardwarebased command
		  
		  	//Sets his new intentPosition to the last node where it came from
			executor.getRobot().getRobotStatus().get(request.getSender())
					.setIntentPosition(
							executor.getRobot().getRobotStatus().get(
									request.getSender()).getPosition());
			
			// Normally there has to send a broadcastMessage to all other
			// robots, so they know the "new" intentPosition of the epuck
			
			
			//The right command to turn the epuck?!
			executor.getRobot().turn();
			
			//The right command to make the robot drive?!
			executor.getRobot().forward();
			
			
		  return true;
	  }    
  }

}
