package sep.conquest.model.behaviour;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
import sep.conquest.util.Utility;

/**
 * CooperativeBehaviour represents a behaviour to identify frontier nodes within
 * a map that will be already explored by other robots. It extends the Behaviour
 * class for enabling a behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class CooperativeBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected CooperativeBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public boolean execute(Map<Integer, Integer> map, Puck robot) {
    	
    	boolean changed = super.execute(map, robot);
    	
    	Set<UUID> keys = robot.getRobotStatus().keySet();
    	
    	for (UUID key: keys) {
    		if (robot.getID() != key) {
    			RobotStatus status = robot.getRobotStatus().get(key);
    			int[] intent = status.getIntentPosition();
    			if (intent != null) {
    				if (expandIntentNode(intent, map))
    					changed = true;
    			}
    				
    		}
    	}    	
        return changed;
    }
    
    /**
     * Parse all neighbours of a given node which have a maximum of navigation
     * costs of 5. Every neighbour gets additionally points (50 for the first
     * neighbour... 10 for the fith neighbour). 
     * 
     * @param node The node.
     * @param map The navigation-cost-map.
     */
    private boolean expandIntentNode(int[] node, Map<Integer, Integer> map) {
    	boolean changed = false;
    	
    	for (int i = -8; i <= 8; i++) {
    		for (int j = -8; j <= 8; j++) {
    			int costs = Math.abs(i)+Math.abs(j);
    			if (costs < 8) {
    				int key = Utility.makeKey(i+node[0], j+node[1]);
    				if (map.containsKey(key)) {
    					map.put(key, map.get(key) + (100-costs*10)); 
    					changed = true;
    				}
    					   				
    			}
    		}
    	}
    	return changed;
    }

}
