package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

/**
 * Handles speed request messages that are sent to the simulator.
 * 
 * As simulation does currently not support different speeds for each robot,
 * Handler just writes on ok message to the corresponding output buffer of 
 * the simulator.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimSpeedHandler extends Handler {

  // Reference on simulator that received the message.
  private Simulator sim;

  /**
   * Constructor.
   * 
   * Sets reference on previous Handler (if used in a chain) and on simulator.
   * 
   * @param next
   *          The previous Handler in chain.
   * @param simulator
   *          The simulator that has received the message and has to respond.
   */
  public SimSpeedHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  /**
   * Handles speed request messages. As different speed levels are not supported
   * in the simulator, Handler just writes ok message on the corresponding
   * output buffer of the simulator.
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling speed request messages.
    // In this case, write ok message to output buffer of simulator.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_SET_SPEED)) {
      // The sender of the message
      UUID sender = request.getSender();

      // Write message type "ok" to first two bytes and write whole message 
      // to the output buffer.
      byte[] response = new byte[32];
      response[0] = (byte) (Puck.RES_OK & 0xFF);
      response[1] = (byte) ((Puck.RES_OK >> 8) & 0xFF);
      sim.writeBuffer(sender, response);
      sim.clearRequest(sender);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}
