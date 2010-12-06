package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

/**
 * Handles move request messages that are sent to the simulator.
 * 
 * On the basis of the current position and the current orientation of a robot,
 * the Handler determines the next node and returns its type as response.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class SimMoveHandler extends Handler {

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
  public SimMoveHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  /**
   * Handles move request messages.
   * 
   * 
   * 
   * 
   */
  @Override
  public boolean handleRequest(IRequest request) {

    // Check if Handler is responsible for handling move request messages.
    // In this case, determine next node and return its type.
    // Otherwise call next handler in chain or return false, if there is no next
    // handler.
    if (request.getKind().equals(MessageType.REQUEST_MOVE)) {
      // The sender of the message
      UUID sender = request.getSender();

      // Get current position and orientation of the sender robot.
      int[] pos = sim.getPosition(sender);
      Orientation ori = sim.getOrientation(sender);

      // Update current position of the robot.
      switch (ori) {
      case UP:
        pos[1]++;
        break;
      case DOWN:
        pos[1]--;
        break;
      case LEFT:
        pos[0]++;
        break;
      case RIGHT:
        pos[0]++;
        break;
      default:
        break;
      }
      // Get NodeType of new position.
      NodeType node = sim.getNodeType(pos);
      
      // Write message type "node hit" to first two bytes.
      byte[] response = new byte[32];
      response[0] = (byte) (Puck.RES_HITNODE & 0xFF);
      response[1] = (byte) ((Puck.RES_HITNODE >> 8) & 0xFF);
      
      // Write node type to third byte.
      response[2] = (byte) node.ordinal();
      sim.writeBuffer(sender, response);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}