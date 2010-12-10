package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.State;
import sep.conquest.model.requests.MessageType;

/**
 * LocalLocalizeBehaviour represents a behaviour for localizing adjacent robots.
 * It extends the Behaviour class for enabling a behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class LocalLocalizeBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected LocalLocalizeBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public boolean execute(Map<Integer, Integer> map, Puck robot) {
    	
    	if (!awaitResponse) {
    		byte[] request = new byte[32];
			request[0] = (byte) (MessageType.REQUEST_STATUS.getTypeCode() & 0xff);
			request[1] = (byte) ((MessageType.REQUEST_STATUS.getTypeCode() >> 8) & 0xff);
	
			super.writeSocket(robot, request);
    	} else {
    		awaitResponse = false;
    		robot.getRobotStatus().get(robot.getID()).setOrientation(Orientation.UP);
    		robot.changeBehaviour(State.EXPLORE);
    	} 
        return super.execute(map, robot);
    }
    
}
