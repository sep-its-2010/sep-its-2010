package sep.conquest.model.handler;

import sep.conquest.model.IRequest;
import sep.conquest.model.requests.MessageType;

/**
 * Handles PuckOK messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker
 *
 */
public class PuckOkHandler extends Handler {

  /**
   * Constructor calling constructor of super class.
   */
  public PuckOkHandler(Handler next) {
    super(next);
  }
  
  /**
   * Handles PuckOk messages.
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
    if (request.getKind().equals(MessageType.RESPONSE_OK)) {
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}