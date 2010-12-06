package sep.conquest.model.handler;

import java.util.UUID;

import sep.conquest.model.IRequest;
import sep.conquest.model.Simulator;
import sep.conquest.model.requests.MessageType;

public class SimResetHandler extends Handler {

private Simulator sim;
  
  public SimResetHandler(Handler next, Simulator simulator) {
    super(next);
    sim = simulator;
  }

  @Override
  public boolean handleRequest(IRequest request) {

    if (request.getKind().equals(MessageType.REQUEST_RESET)) {
      UUID sender = request.getSender();
      byte[] response = new byte[32];
      
      
      sim.writeBuffer(sender, response);
      return true;
    } else if (hasNext()) {
      return getNext().handleRequest(request);
    } else {
      return false;
    }
  }


}
