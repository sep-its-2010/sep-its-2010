package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

/**
 * Handles reset request messages that are sent to the simulator.
 * 
 * Calls reset method of corresponding robot.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimResetHandler extends Handler {

  /**
   *  Reference on simulator that received the message.
   */
  private Simulator sim;

  /**
   * Constructor.
   * 
   * Sets reference on next Handler (if used in a chain) and on simulator.
   * 
   * @param next
   *          The next Handler in chain.
   * @param simulator
   *          The simulator that has received the message and has to respond.
   */
  public SimResetHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  /**
   * Handles reset request messages.
   * 
   * Determines the robot to reset and resets its current position and
   * orientation to their initial values.
   * 
   * Finally, Handler writes ok message on the corresponding output buffer of
   * the simulator.
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling reset request messages.
    // In this case, determine next node and return its type.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_RESET)) {
      // The sender of the message.
      UUID sender = request.getSender();

      // Reset state of robot to its initial values.
      sim.resetRobotState(sender);

      // Write message type "ok" to first two bytes and write whole message
      // to the output buffer.
      byte[] response = new byte[Puck.MSG_LENGTH];
      response[Puck.TYPE_FIRST_BYTE] = (byte) (Puck.RES_OK & 0xFF);
      response[Puck.TYPE_SECOND_BYTE] = (byte) ((Puck.RES_OK >> 8) & 0xFF);
      sim.writeBuffer(sender, response);
      
      // Finally remove request from input buffer.
      sim.clearRequest(sender);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}