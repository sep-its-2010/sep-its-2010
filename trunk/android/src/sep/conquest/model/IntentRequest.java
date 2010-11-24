package sep.conquest.model;

import java.util.UUID;


/**
 * The IntentRequest represents message objects that are sent by 
 * IComClient clients. It will be sent from a participant if it's up to
 * drive to a new node.
 * 
 * @author Andreas Wilhelm
 *
 */
public class IntentRequest extends Request {
    
    /**
     * The node that the robot has intended driving to.
     */
    private int[] intentNode;
    
    /**
     * The constructor expects a list of receiver as well as the
     * specific node which the robot has intended driving to.
     * 
     * @param receiver The IDs of the receiver.
     * @param node The intended destination.
     */
    public IntentRequest(UUID sender, UUID[] receiver, int[] node) {
    	super(sender, receiver);    	
        this.intentNode = node;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.INTENT;
    }
    
    /**
     * Returns the node that was intended as destination for the robot.
     * 
     * @return The node.
     */
    public int[] getIntendedNode() {
        return intentNode;
    }
}
