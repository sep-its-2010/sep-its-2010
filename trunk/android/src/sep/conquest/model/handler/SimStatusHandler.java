package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.NodeType;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;
import sep.conquest.util.Utility;

/**
 * Handles status request messages that are sent to the simulator.
 * 
 * Returns the complete current state of the robot.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimStatusHandler extends Handler {

  // Reference on simulator that received the message.
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
  public SimStatusHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  /**
   * Handles status request messages.
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling status request messages.
    // In this case, write status message to output buffer of simulator.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_STATUS)) {
      // The sender of the message.
      UUID sender = request.getSender();
      
      // Write message type "status" to first two bytes.
      byte[] response = new byte[32];
      response[0] = (byte) (Puck.RES_STATUS & 0xFF);
      response[1] = (byte) ((Puck.RES_STATUS >> 8) & 0xFF);
      
      // Write system up time to next four bytes.
      int time = sim.getSystemUpTime(sender);     
      for (int i = 0; i < 4; i++) {
        response[2 + i] = (byte) (time & 0xFF);
        time = time >> 8;
      }
      
      // Write node type to 17th byte.
      int[] pos = sim.getPosition(sender);
      if ((pos[0] % 3 != 0) || (pos[1] % 3 != 0)) {
      //  throw new IllegalArgumentException("Robot is not on a valid position");
      }
      NodeType node = sim.getNodeType(pos[0] / 3, pos[1] / 3);
      node = Utility.calculateNodeTypesToRealWorld(node, sim.getOrientation(sender));
      response[17] = (byte) node.ordinal();
      sim.writeBuffer(sender, response);
      sim.clearRequest(sender);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}