package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;
import sep.conquest.model.requests.VirtualPuckRequest;

/**
 * Handles turn request messages that are sent to the simulator.
 * 
 * On the basis of the current orientation of a robot, the Handler determines
 * the new orientation and returns ok as response.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimTurnHandler extends Handler {

  /**
   * Reference on simulator that received the message.
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
  public SimTurnHandler(Handler next, Simulator sim) {
    super(next);
    this.sim = sim;
  }

  /**
   * Handles turn request messages.
   * 
   * Computes the new orientation of a robot and sets it. Finally, Handler
   * writes ok message on the corresponding output buffer of the simulator.
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling turn request messages.
    // In this case, determine current orientation of the sender, compute it's
    // new orientation and set it.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_TURN)) {
      // The sender of the message
      UUID sender = request.getSender();

      // Message is of type VirtualPuckRequest
      VirtualPuckRequest req = (VirtualPuckRequest) request;

      // The message containing the turn value.
      byte[] msg = req.getMessage();

      // The third byte in the message contains the turn value.
      byte turn = msg[2];

      // Set orientation of the robot in the simulator.
      Orientation ori = sim.getOrientation(sender);
      sim.setOrientation(sender, Orientation.getTurnedOrientation(turn, ori));

      // Write message type "ok" to first two bytes and write whole message
      // to the output buffer.
      byte[] response = new byte[Puck.MSG_LENGTH];
      response[Puck.TYPE_FIRST_BYTE] = (byte) (Puck.RES_OK & 0xFF);
      response[Puck.TYPE_SECOND_BYTE] = (byte) ((Puck.RES_OK >> 8) & 0xFF);
      
      // Write message to output buffer and clear input buffer.
      sim.writeBuffer(sender, response);
      sim.clearRequest(sender);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}