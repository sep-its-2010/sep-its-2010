package sep.conquest.model;

import java.util.UUID;


/**
 * The ControlledRequest represents message objects that are sent by 
 * IComClient clients. It will be sent if a user wants to control a specific
 * robot.
 * 
 * @author Andreas Wilhelm
 *
 */
public class ControlledRequest extends Request {
    
    /**
     * The flag indicates whether the robot shall be controlled.
     */
    private boolean controlled;
    
    /**
     * The constructor expects a list of receiver as well as the
     * indicator if the robot shall be controlled.
     * 
     * @param receiver The IDs of the receiver.
     * @param controlled The indicator if the robot shall be controlled.
     */
    public ControlledRequest(UUID[] receiver, boolean controlled) {
    	super(receiver);    	
        this.controlled = controlled;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.CONTROL_ENABLE;
    }
    
    /**
     * isControlled indicates whether the receiver of the message shall be
     * controlled.
     * 
     * @return The indicator if the robot is controlled.
     */
    public boolean isControlled() {
        return controlled;
    }
}
