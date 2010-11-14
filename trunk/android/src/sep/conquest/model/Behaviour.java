package sep.conquest.model;

import java.util.Map;

/**
 * The abstract class Behaviour should be derived by all concrete behaviours
 * due to have a centralized logic-approach. It forces a chain of logic-classes
 * and the corresponding chain-handling.
 * 
 * @author Ande
 *
 */
public abstract class Behaviour implements IBehaviour {
    
    // local definitions
    private IBehaviour nextBehaviour;
    
    /**
     * The constructor sets the reference to the next behaviour in the chain.
     * 
     * @param next
     */
    protected Behaviour(IBehaviour next) {
        nextBehaviour = next;
    }
    
    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    @Override
    public Map<int[], Object> execute(Map<int[], Object> map) {
        if (nextBehaviour != null) {
            return nextBehaviour.execute(map);
        } else {
            return map;
        }
            
    }

}
