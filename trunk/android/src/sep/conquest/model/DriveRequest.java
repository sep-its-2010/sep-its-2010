package sep.conquest.model;

import java.util.UUID;


/**
 * The DriveRequest class is for message objects that are sent by IComClient
 * clients. It represents a drive command for a specific robot. Therefore the
 * command will be returned by the getCommand method.
 * 
 * @author Andreas Wilhelm
 *
 */
public class DriveRequest extends Request {
    
    /**
     * The specific drive command of the request message.
     */
    private Drive driveCommand;
    
    /**
     * The constructor expects a drive-command and a corresponding robot.
     * 
     * @param receiver The robot IDs that shall receive the command.
     * @param command The drive-command.
     */
    public DriveRequest(UUID sender, UUID[] receiver, Drive command) {
        super(receiver);
        driveCommand = command;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.CONTROL_DIR;
    }
    
    /**
     * getCommand returns the drive-command which should be executed.
     * 
     * @return The drive-command.
     */
    public Drive getCommand() {
        return driveCommand;
    }
}
