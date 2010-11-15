package sep.conquest.model;

import java.util.Map;

/**
 * BehaviourDistance represents a behaviour to identify the next frontier-nodes
 * of a given map. It extends the Behaviour class for enabling a behaviour-
 * chain.
 * 
 * @author Andreas Wilhelm
 */
public final class BehaviourDistance extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param next
     */
    protected BehaviourDistance(IBehaviour next) {
        super(next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], Object> execute(Map<int[], Object> map) {
        return super.execute(map);
    }

}
