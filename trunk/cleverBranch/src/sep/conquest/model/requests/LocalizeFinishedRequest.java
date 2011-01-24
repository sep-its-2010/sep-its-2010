package sep.conquest.model.requests;

import java.util.UUID;


/**
 * The LocalizeFinishedRequest represents message objects that are sent by 
 * IComClient clients on finishing the localizing. It will be sent to the
 * successor client.
 * 
 * @author Andreas Wilhelm
 *
 */
public class LocalizeFinishedRequest extends Request {
	
    /**
     * The constructor expects a sender and a list of receiver.
     * 
     * @param sender The sender of the message.
     * @param receiver The IDs of the receiver.
     */
    public LocalizeFinishedRequest(UUID sender, UUID[] receiver) {
    	super(sender, receiver);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.FINISHED_LOCALIZATION;
    }

}
