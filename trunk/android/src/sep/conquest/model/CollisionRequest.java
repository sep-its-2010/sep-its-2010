package sep.conquest.model;

import java.util.UUID;


/**
 * The CollisionRequest represents message objects that are sent by 
 * IComClient clients on collision with others. It contains some information
 * about the type of collision.
 * 
 * @author Andreas Wilhelm
 *
 */
public class CollisionRequest extends Request {
    
    /**
     * The IR-sensor that has caused the collision.
     */
    private IRSensor sensor;
    
    /**
     * The constructor expects a sender, a list of receiver as well as the
     * corresponding sensor which caused the collision.
     * 
     * @param receiver The IDs of the receiver.
     * @param speedLevel The speed-level of the command.
     */
    public CollisionRequest(UUID sender, UUID[] receiver, IRSensor sensor) {
    	super(sender, receiver);    	
        this.sensor = sensor;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.COLLISION;
    }
    
    /**
     * getSensor returns the IR-sensor that caused the collision.
     * 
     * @return The IR-sensor.
     */
    public IRSensor getSensor() {
        return sensor;
    }
}
