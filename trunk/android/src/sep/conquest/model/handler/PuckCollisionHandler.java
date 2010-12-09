package sep.conquest.model.handler;


import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.CollisionRequest;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.PuckRequest;

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
   * Returns true, if request was handled. If class is not responsible,
   * call handleRequest of next handler. If next handler is null return
   * false.
   * 
   * @param request The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(IRequest request) {
	  PuckRequest colRes = (PuckRequest) request;
	  if(!(colRes.getKind()==MessageType.RESPONSE_COLLISION)){
		  return super.handleRequest(request);
	  } else {
		  
		  //The epuck stops on hardwarebased command
		  byte[] bufferMessage = colRes.getMessage();
		  boolean[] colArray = new boolean[8];
		  for(int i = 0 ; i < 8; i++){
			  //colArray;
		  }
		  //CollisionRequest colRes = new CollisionRequest(executor.getRobot(), null, executor.getRobot().getRobotStatus().get(executor.getRobot()));
		  
		  return true;
	  }    
  }

}
