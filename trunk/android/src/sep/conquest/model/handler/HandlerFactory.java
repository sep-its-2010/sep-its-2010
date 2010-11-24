package sep.conquest.model.handler;

import sep.conquest.model.Environment;
import sep.conquest.model.LogicThread;
import sep.conquest.model.PuckOkHandler;

/**
 * Factory to create different kinds of Handler chains for Pucks and
 * Environment.
 * 
 * @author Andreas Poxrucker
 *
 */
public class HandlerFactory {

  /**
   * Returns a chain of Handler objects that handle different kinds of
   * broadcast messages.
   * 
   * Handled types of requests:
   * - DriveRequest
   * - StatusUpdateRequest
   * - SpeedRequest
   * - CollisionRequest
   * - ControlledRequest
   * - FailureRequest
   * - IntentRequest
   * - HelloRequest
   * 
   * @param puck The LogicThread of the Puck that should get the chain.
   * @return Reference on first handler of chain.
   */
  public static Handler getPuckBCChain(LogicThread executor) {
	  Handler failHndl = new FailureRequestHandler(null, executor);
	  Handler colHndl = new CollisionRequestHandler(failHndl, executor);
	  Handler cntrlHndl = new ControlledRequestHandler(colHndl, executor);
	  Handler drvHndl = new DriveRequestHandler(cntrlHndl, executor);
	  Handler helloHndl = new HelloRequestHandler(drvHndl, executor);
	  Handler intHndl = new IntentRequestHandler(helloHndl, executor);
	  Handler spdHndl = new SpeedRequestHandler(intHndl, executor);
	  Handler statHndl = new StatusUpdateRequestHandler(spdHndl, executor);	
	  
    return failHndl;
  }
  
  /**
   * Returns a chain of Handler objects that handle different kinds of
   * bluetooth messages.
   * 
   * Handled types of requests:
   * - PuckOKRequest
   * - PuckRequest
   * 
   * 
   * @param puck The LogicThread of the Puck that should get the chain.
   * @return Reference on first handler of chain.
   */
  public static Handler getPuckBTChain(LogicThread executor) {	  
	  Handler okHndl = new PuckOkHandler(null, executor);
	  Handler reqHndl = new PuckRequestHandler(okHndl, executor);
	  
	  return okHndl;
  }
  
  /**
   * Returns a chain of Handler objects that handle different kinds of
   * bluetooth messages.
   * 
   * Handled types of messages:
   * - StatusUpdateRequest
   * - FailureRequest
   * 
   * @param env Instance of Environment that should get the chain.
   * @return Reference on first handler of chain.
   */
  public static Handler getEnvironmentBCChain(Environment env) {
	Handler statHndl = new EnvStatusUpdateRequestHandler(null);
    return statHndl;
  }
  
  
}
