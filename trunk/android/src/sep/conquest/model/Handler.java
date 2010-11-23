package sep.conquest.model;

import java.util.UUID;

/**
 * Abstract class representing a Handler to handle messages.
 * 
 * @author Andreas Poxrucker
 */
public abstract class Handler {
  
  /**
   * Reference on next Handler in chain.
   */
  private Handler next;
  
  /**
   * Constructor.
   */
  public Handler() {
    next = null;
  }
  
  /**
   * Returns reference on next Handler in chain.
   * 
   * @return Next Handler in chain or null, if Handler has no reference on next
   *         Handler.
   */
  public Handler getNext() {
    return next;
  }
  
  /**
   * Sets reference of this Handler to nextHandler
   * 
   * @param nextHandler Reference on next Handler in chain
   */
  public void setNext(Handler nextHandler) {
    next = nextHandler;
  }
  
  /**
   * Returns whether Handler has successor.
   * 
   * @return True, if Handler has successor, false otherwise.
   */
  public boolean hasNext() {
    return next == null;
  }

  /**
   * Handles a request message if responsible and returns true. 
   * Otherwise passes request to next Handler or returns false, if
   * there is no next Handler.
   * 
   * @param sender The sender of the request message.
   * @param request The request that has to be handled.
   * 
   * @return True, if Handler has handled message, false otherwise.
   */
  public abstract boolean handleRequest(UUID sender, IRequest request);  
}
