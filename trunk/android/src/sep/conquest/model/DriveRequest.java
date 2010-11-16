package sep.conquest.model;

import java.util.UUID;


/**
 * The DriveRequest class is a message object for IComClient clients. It
 * represents a drive command for a specific robot. Therefore the command
 * will be returned by the getCommand method.
 * 
 * @author Andreas Wilhelm
 *
 */
public class DriveRequest implements IRequest {
    
    /**
     * The specific drive command of the request message.
     */
    private Drive driveCommand;
    
    
    /**
     * The list of clients that should receive the message.
     */
    private UUID[] clients;
    
    /**
     * The constructor expects a drive-command and a corresponding robot.
     * 
     * @param ID The robot ID.
     * @param command The drive-command.
     */
    public DriveRequest(UUID ID, Drive command) {
        driveCommand = command;
        clients = new UUID[1];
        clients[0] = ID;
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getKind()
     */
    public MessageType getKind() {
        return MessageType.CONTROL_DIR;
    }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IRequest#getReceiver()
     */
    public UUID[] getReceiver() {
        return clients;
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
