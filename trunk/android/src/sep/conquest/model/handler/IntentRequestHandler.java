package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;

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
  public IntentRequestHandler(Handler prev, LogicThread exec) {
    super(prev);
    executor = exec;
  }
  
  /**
   * Handles IntentRequest messages.
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
