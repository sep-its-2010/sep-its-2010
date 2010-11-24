package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;

/**
 * Handles CollisionRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class CollisionRequestHandler extends Handler {
	
	/**
	 * The logic thread.
	 */
	private LogicThread lThread;

  /**
   * Constructor calling constructor of super class.
   */
  public CollisionRequestHandler(Handler prev, LogicThread exec) {
    super(prev);
    lThread = exec;
    
  }
  
  /**
   * Handles CollisionRequest messages.
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
    return false;
  }
}
