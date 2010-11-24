package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;

/**
 * Handles StatusUpdateRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class EnvStatusUpdateRequestHandler extends Handler {
  
  /**
   * Constructor calling constructor of super class.
   */
  public EnvStatusUpdateRequestHandler(Handler prev) {
    super(prev);
  }
  
  /**
   * Handles StatusUpdateRequest messages.
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


