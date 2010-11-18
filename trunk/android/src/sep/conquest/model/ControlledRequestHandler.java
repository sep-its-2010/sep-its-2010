package sep.conquest.model;

/**
 * Handles SpeedRequest messages.
 * 
 * @author Andreas Poxrucker
 *
 */
public class ControlledRequestHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public ControlledRequestHandler(LogicThread exec) {
    super();
    executor = exec;
  }
  
  /**
   * Handles ControlledRequest messages.
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
