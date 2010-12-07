package sep.conquest.model.behaviour;

import java.util.Map;
import java.util.TreeMap;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

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
	 * A map for the first behaviours per state-level.
	 */
	private static TreeMap <State, IBehaviour> entryBehaviours = 
		new TreeMap<State, IBehaviour>();    
    
    /**
     * The constructor sets the reference to the previous Behaviour in the chain
     * as well as its state-level. If this is the first entry for a specific
     * state level, this Behaviour will set as the agent for it. 
     * 
	 * @param stateLevel The corresponding state-level.
     * @param prev The reference to the previous Behaviour.
     */
    protected Behaviour(State stateLevel, IBehaviour prev) {
    	if (!entryBehaviours.containsKey(stateLevel)) {
    		entryBehaviours.put(stateLevel, this);
    	}    	
    	this.stateLevel = stateLevel;
    	if (prev != null) {
    		prev.setNextBehaviour(this);
    	}
    }
    
    /**
     * Returns the level of the Behaviour.
     * 
     * @return The  level of the Behaviour.
     */
    public State getStateLevel() {
    	return stateLevel;
    }
    
    /**
     * Returns the first Behaviour of a given state. If there's no Behaviour,
     * a null pointer will be returned.
     * 
     * @param stateLevel The state-level for the expected Behaviour.
     * @return The first Behaviour of the specified state-level.
     */
    public static IBehaviour getFirstBehaviour(State stateLevel) {
    	return entryBehaviours.get(stateLevel);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#setNextBehaviour(sep.conquest.model.IBehaviour)
     */
    public void setNextBehaviour(IBehaviour next) {
    	nextBehaviour = next;
    }    
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<Integer, Integer> execute(Map<Integer, Integer> map, Puck robot) {
        if (nextBehaviour != null) {
            return nextBehaviour.execute(map, robot);
        } else {
            return map;
        }            
    }
}
