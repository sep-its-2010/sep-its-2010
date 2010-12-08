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
    public Map<Integer, Integer> execute(Map<Integer, Integer> map, Puck robot) {
    	
    	Set<UUID> keys = robot.getRobotStatus().keySet();
    	
    	for (UUID key: keys) {
    		if (robot.getID() != key) {
    			RobotStatus status = robot.getRobotStatus().get(key);
    			int[] intent = status.getIntentPosition();
    			if (intent != null)
    				expandIntentNode(intent, map);
    		}
    	}    	
        return super.execute(map, robot);
    }
    
    /**
     * Parse all neighbours of a given node which have a maximum of navigation
     * costs of 5. Every neighbour gets additionally points (50 for the first
     * neighbour... 10 for the fith neighbour). 
     * 
     * @param node The node.
     * @param map The navigation-cost-map.
     */
    private void expandIntentNode(int[] node, Map<Integer, Integer> map) {
    	for (int i = -5; i <= 5; i++) {
    		for (int j = -5; j <= 5; j++) {
    			int costs = Math.abs(i)+Math.abs(j);
    			if (costs < 5) {
    				int key = Utility.makeKey(i+node[0], j+node[1]);
    				if (map.containsKey(key)) 
    					map.put(key, map.get(key) + (50-costs*10));    				
    			}
    		}
    	}
    }

}
