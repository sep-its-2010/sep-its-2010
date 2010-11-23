package sep.conquest.model;

import java.util.UUID;

/**
 * Handles IntentRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class IntentRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public IntentRequestHandler(LogicThread exec) {
    super();
    executor = exec;
  }
  
  /**
   * Handles IntentRequest messages.
   * 
   * Returns true, if request was handled. If class is not responsible,
   * call handleRequest of next handler. If next handler is null return
   * false.
   * 
   * @param sender The sender of the request message.
   * @param request The request to handle.
   * @return True, if request was handled, false otherwise.
   */
  @Override
  public boolean handleRequest(UUID sender, IRequest request) {
    // TODO Auto-generated method stub
    return false;
  }

}
