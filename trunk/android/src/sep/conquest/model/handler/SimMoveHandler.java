package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

public class SimMoveHandler extends Handler {

  private Simulator sim;
  
  public SimMoveHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  @Override
  public boolean handleRequest(IRequest request) {

    if (request.getKind().equals(MessageType.REQUEST_MOVE)) {
      UUID sender = request.getSender();
      sim.getPosition(sender);
      
      return true;
      
    } else if (hasNext()) {
      return getNext().handleRequest(request);
    } else {
      return false;
    }
  }

}
