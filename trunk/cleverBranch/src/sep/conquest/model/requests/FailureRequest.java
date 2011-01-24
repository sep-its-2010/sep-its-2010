package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.FailureType;

/**
 * The FailureRequest represents message objects that are sent by IComClient
 * clients. It will be sent from a participant if an error is occurred during
 * the runtime.
 * 
 * @author Andreas Wilhelm
 * 
 */
public class FailureRequest extends Request {

  /**
   * The specific failure type which has occurred.
   */
  private FailureType type;

  /**
   * The constructor expects a list of receiver as well as the specific failure
   * type of the error.
   * 
   * @param receiver
   *          The IDs of the receiver.
   * @param type
   *          The error type.
   */
  public FailureRequest(UUID sender, UUID[] receiver, FailureType type) {
    super(sender, receiver);
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IRequest#getKind()
   */
  public MessageType getKind() {
    return MessageType.FAILURE;
  }

  /**
   * Returns the specific error type of the FailureRequest.
   * 
   * @return The error type.
   */
  public FailureType getFailureType() {
    return type;
  }
}
