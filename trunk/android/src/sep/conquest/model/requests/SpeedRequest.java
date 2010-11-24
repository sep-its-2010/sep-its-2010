package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.SpeedLevel;


/**
 * The SpeedRequest represents message objects that are sent by 
 * IComClient clients. It contains a speed update command for a specific
 * robot.
 * 
 * @author Andreas Wilhelm
 *
 */
public class SpeedRequest extends Request {
    
    /**
     * The speed-level of the message.
     */
    private SpeedLevel speedLevel;
    
    /**
     * The constructor expects a list of receiver as well as the
     * corresponding speed-level.
     * 
     * @param sender The robot ID of the sender.
     * @param receiver The IDs of the receiver.
     * @param speedLevel The speed-level of the command.
     */
    public SpeedRequest(UUID sender, UUID[] receiver, SpeedLevel speedLevel) {
    	super(sender, receiver);    	
        this.speedLevel = speedLevel;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.CONTROL_SPEED;
    }
    
    /**
     * getStatus returns the speed-level.
     * 
     * @return The drive-command.
     */
    public SpeedLevel getSpeed() {
        return speedLevel;
    }
}
