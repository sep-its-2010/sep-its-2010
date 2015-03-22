package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.Orientation;


/**
 * The LocalizeCollisionRequest represents message objects that are sent by 
 * IComClient clients on collision with others during the localization process.
 * 
 * @author Andreas Wilhelm
 *
 */
public class LocalizeCollisionRequest extends Request {
	
	/**
	 * The current position of the localizing robot.
	 */
	private Integer position;
	
	/**
	 * The direction of the localizing robot which has caused the collision.
	 */
	private Orientation direction;
    
    /**
     * The constructor expects a sender and a list of receiver.
     * 
     * @param sender The sender of the message.
     * @param receiver The IDs of the receiver.
     * @param position The position of the localizing robot.
     * @param direction The direction of the localizing robot during collision.
     */
    public LocalizeCollisionRequest(UUID sender, UUID[] receiver, 
    		Integer position,Orientation direction) {
    	super(sender, receiver);
    	this.position = position;
    	this.direction = direction;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.LOCALIZE_COLLISION;
    }
    
    /**
     * Returns the current position of the localizing robot.
     * 
     * @return The position in a transformed way.
     */
    public Integer getPosition() {
    	return position;
    }
    
    /**
     * Returns the direction of the localizing robot which has caused the
     * collision.
     * 
     * @return The direction.
     */
    public Orientation getDirection() {
    	return direction;
    }

}
