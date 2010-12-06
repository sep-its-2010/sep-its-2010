package sep.conquest.model.handler;

import android.graphics.Path.Direction;
import android.graphics.drawable.GradientDrawable.Orientation;
import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.*;

/**
 * Handles PuckAbyss messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker
 *
 */
public class PuckAbyssHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public PuckAbyssHandler(Handler next, LogicThread exec) {
    super(next);
    executor = exec;
  }
  
  /**
   * Handles PuckAbyss messages.
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
	  if(!(request.getKind() == MessageType.RESPONSE_ABYSS)){
		  return super.handleRequest(request);
	  } else {
		  //The epuck stops on hardwarebased commands
		  //The message is to discover a failure state
		  //Possibly we stop the complete discovery?!
		  return true;
	  }
  }

}
