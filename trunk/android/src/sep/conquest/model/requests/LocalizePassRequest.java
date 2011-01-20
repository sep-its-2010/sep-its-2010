package sep.conquest.model.requests;

import java.util.UUID;


/**
 * The LocalizePassRequest represents message objects that are sent by 
 * IComClient clients on localizing state in order to pass this state to the
 * next client.
 * 
 * @author Andreas Wilhelm
 *
 */
public class LocalizePassRequest extends Request {
	
    /**
     * The constructor expects a sender and a list of receiver.
     * 
     * @param sender The sender of the message.
     * @param receiver The IDs of the receiver.
     */
    public LocalizePassRequest(UUID sender, UUID[] receiver) {
    	super(sender, receiver);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.PASS_LOCALIZATION;
    }

}
