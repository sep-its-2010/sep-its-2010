package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.IRSensor;


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
    private boolean[] sensorArray = new boolean[IRSensor.values().length];
    
    /**
     * The constructor expects a sender, a list of receiver as well as the
     * corresponding sensor which caused the collision.
     * 
     * @param receiver The IDs of the receiver.
     * @param speedLevel The speed-level of the command.
     */
    public CollisionRequest(UUID sender, UUID[] receiver, boolean[] sensor) {
    	super(sender, receiver);    	
        this.sensorArray = sensor;
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
     * @return The IR-sensorArray.
     */
    public boolean[] getSensor() {
        return sensorArray;
    }
}
