package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

/**
 * Handles set led request messages that are sent to the simulator.
 * 
 * Because there is no led visualization currently supported by the simulator,
 * Handler just writes ok message to corresponding output buffer of the
 * simulator.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimLEDHandler extends Handler {

  // Reference on simulator that received the message.
  private final Simulator sim;

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
  public SimLEDHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  /**
   * Handles led request messages. As led visualization is currently not
   * supported by the simulator, Handler just writes ok message on the
   * corresponding output buffer of the simulator.
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling led request messages.
    // In this case, write ok message to output buffer of simulator.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_SET_LED)) {
      UUID sender = request.getSender();
      byte[] response = new byte[32];
      
      // Write message type "ok" to first two bytes and write whole message 
      // to the output buffer.
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
