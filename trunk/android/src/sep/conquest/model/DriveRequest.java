package sep.conquest.model;


/**
 * The DriveRequest class is a message object for IComClient clients. It
 * represents a drive command for a specific robot. Therefore the command
 * will be returned by the getCommand method.
 * 
 * @author Andreas Wilhelm
 *
 */
public class DriveRequest implements IRequest {
    
    // local definitions
    private Drive driveCommand;
    private String[] clients;
    
    /**
     * The constructor expects a drive-command and a corresponding robot.
     * 
     * @param ID the robot ID.
     * @param command the drive-command.
     */
    public DriveRequest(String ID, Drive command) {
        driveCommand = command;
        clients = new String[1];
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
    public String[] getReceiver() {
        return clients;
    }    
    
    /**
     * getCommand returns the drive-command which should be executed.
     * 
     * @return the drive-command.
     */
    public Drive getCommand() {
        return driveCommand;
    }
}
