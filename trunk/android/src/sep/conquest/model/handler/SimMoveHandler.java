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
   * Sets moving state of robot to moving.
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
      sim.setMoving(sender, true);
      sim.clearRequest(sender);
      sim.moveRobot(sender);
      return true;
    } else {
      return super.handleRequest(request);
    }
  }
}