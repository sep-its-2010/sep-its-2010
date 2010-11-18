package sep.conquest.model;

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
    return null;
  }
  
  /**
   * Returns a chain of Handler objects that handle different kinds of
   * bluetooth messages.
   * 
   * Handled types of messages:
   * 
   * 
   * @param puck The LogicThread of the Puck that should get the chain.
   * @return Reference on first handler of chain.
   */
  public static Handler getPuckBTChain(LogicThread executor) {
    return null;
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
    return null;
  }
  
  
}
