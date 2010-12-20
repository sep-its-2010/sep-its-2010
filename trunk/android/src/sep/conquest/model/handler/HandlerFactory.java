package sep.conquest.model.handler;

import sep.conquest.model.Environment;
import sep.conquest.model.LogicThread;
import sep.conquest.model.Simulator;

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
    return statHndl;
  }
  
  /**
   * Returns a chain of Handler objects that handle different kinds of
   * bluetooth messages.
   * 
   * Handled types of requests:
   * - PuckOKRequest
   * - PuckRequest
   * 
   * @param puck The LogicThread of the Puck that should get the chain.
   * @return Reference on first handler of chain.
   */
  public static Handler getPuckBTChain(LogicThread executor) {	  
	  Handler okHndl = new PuckOkHandler(null, executor);
	  Handler abyssHndl = new PuckAbyssHandler(okHndl, executor);
	  Handler hitHndl = new PuckNodeHitHandler(abyssHndl, executor);
	  Handler statHndl = new PuckStatusHandler(hitHndl, executor);
	  Handler colHndl = new PuckCollisionHandler(statHndl, executor);
	  Handler rejHndl = new PuckRejectHandler(colHndl, executor);
	  return rejHndl;
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
  
  /**
   * Returns chain of Handler objects that handle different kinds of 
   * Puck messages for the simulator.
   * 
   * Handled types of messages:
   * - SimResetHandler
   * - SimLEDHandler
   * - SimStatusHandler
   * - SimSpeedHandler
   * - SimTurnHandler
   * - SimMoveHandler
   * 
   * @param sim The simulator that received the message.
   * @return Reference on first Handler in chain.
   */
  public static Handler getSimMsgChain(Simulator sim) {
    Handler resHndl = new SimResetHandler(null, sim);
    Handler ledHndl = new SimLEDHandler(resHndl, sim);
    Handler statHndl = new SimStatusHandler(ledHndl, sim);
    Handler spdHndl = new SimSpeedHandler(statHndl, sim);
    Handler turnHndl = new SimTurnHandler(spdHndl, sim);
    Handler moveHndl = new SimMoveHandler(turnHndl, sim);
    
    return moveHndl;
  }
}