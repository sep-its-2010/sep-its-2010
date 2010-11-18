package sep.conquest.model;

import java.util.Map;

/**
 * The abstract class Behaviour should be derived by all concrete behaviours
 * due to have a centralized logic-approach. It forces a chain of logic-classes
 * and the corresponding chain-handling.
 * 
 * @author Andreas Wilhelm
 *
 */
public abstract class Behaviour implements IBehaviour {
    	
    /**
     * A reference to the successor-behaviour.
     */
    private IBehaviour nextBehaviour;
    
    /**
     * The logical level of the behaviour.
     */
    private State stateLevel;
    
    /**
     * The constructor sets the reference to the next behaviour in the chain.
     * 
     * @param next The reference to the next Behaviour.
     */
    protected Behaviour(State stateLevel, IBehaviour next) {
    	this.stateLevel = stateLevel;
        nextBehaviour = next;
    }
    
    /**
     * Returns the level of the behaviour.
     * 
     * @return The  level of the behaviour.
     */
    public State getStateLevel() {
    	return stateLevel;
    }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], GridMap> execute(Map<int[], GridMap> map) {
        if (nextBehaviour != null) {
            return nextBehaviour.execute(map);
        } else {
            return map;
        }
            
    }

}
