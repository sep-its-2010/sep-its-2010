package sep.conquest.model.requests;

import java.util.UUID;

import sep.conquest.model.RobotStatus;


/**
 * The StatusUpdateRequest represents message objects that are sent by 
 * IComClient clients. It contains a position update information of a specific
 * robot as well as its current speed and orientation.
 * 
 * @author Andreas Wilhelm
 *
 */
public class StatusUpdateRequest extends Request {
    
    /**
     * The state of the specific robot that has sent the message.
     */
    private RobotStatus robotStatus;
    
    /**
     * The constructor expects a sender, a list of receiver as well as the
     * corresponding RobotStatus instance.
     * 
     * @param sender The sender of the request.
     * @param receiver The IDs of the receiver.
     * @param robotStatus The corresponding state of the robot.
     */
    public StatusUpdateRequest(UUID sender, UUID[] receiver, 
    		RobotStatus robotStatus) {
    	super(sender, receiver);    	
        this.robotStatus = robotStatus;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.STATUS_UPDATE;
    }
    
    /**
     * getStatus returns the status of the robot (sender).
     * 
     * @return The robot status.
     */
    public RobotStatus getStatus() {
        return robotStatus;
    }
}
