package sep.conquest.model;

import java.util.UUID;

/**
 * Handles CollisionRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class CollisionRequestHandler extends Handler {

  /**
   * Constructor calling constructor of super class.
   */
  public CollisionRequestHandler(LogicThread exec) {
    super();
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
    // TODO Auto-generated method stub
    return false;
  }
}
