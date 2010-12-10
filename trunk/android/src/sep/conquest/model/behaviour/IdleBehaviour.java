package sep.conquest.model.behaviour;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

/**
 * LocalLocalizeBehaviour represents a behaviour for localizing adjacent robots.
 * It extends the Behaviour class for enabling a behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class IdleBehaviour extends Behaviour {
	
	private boolean timerActive = false;

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected IdleBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public boolean execute(Map<Integer, Integer> map, Puck robot) {
    	
    	if (!timerActive) {
	    	Timer timer = new Timer();
	        timer.schedule(new ExecuteTimer(robot), 5000);    		
    	}
    	
        return super.execute(map, robot);
    }
    
    private class ExecuteTimer extends TimerTask {
    	
    	Puck robot;
    	
    	public ExecuteTimer(Puck robot) {
    		this.robot = robot;
    	}

    	@Override
    	public void run() {
    		if (robot != null)
    			robot.changeBehaviour(State.LOCALIZE);
    	}
    	
    }
    
}
