package sep.conquest.model.handler;

import java.util.UUID;

import android.webkit.WebView.HitTestResult;

import sep.conquest.model.IRequest;
import sep.conquest.model.LogicThread;
import sep.conquest.model.requests.MessageType;

/**
 * Handles PuckNodeHit messages coming from the Bluetooth Adapter.
 * 
 * @author Andreas Poxrucker (Florian Lorenz)
 *
 */
public class PuckNodeHitHandler extends Handler {

  /**
   * The LogicThread that executes the content.
   */
  private LogicThread executor;
  
  /**
   * Constructor calling constructor of super class.
   */
  public PuckNodeHitHandler(Handler prev, LogicThread exec) {
    super(prev);
    executor = exec;
  }
  
  /**
   * Handles PuckNodeHit messages.
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
	  if(!(request.getKind() == MessageType.PUCK_HITNODE)){
		  return super.handleRequest(request);
	  } else {
		  //What to do:
		  //RobotStatus position[] sets to old intentPosition[]
			executor.getRobot().getRobotStatus().get(request.getSender())
					.setPosition(
							executor.getRobot().getRobotStatus().get(
									request.getSender()).getIntentPosition());
		  //Aus RobotStatus intentposition[] auslesen und in Karte einfuegen
			int[] bufferPosition = executor.getRobot().getRobotStatus().get(
					request.getSender()).getIntentPosition();
			//in map einfuegen?!
		  //Der Knotentyp steht in der Message!!
		  return true;
	  }
  }
}
