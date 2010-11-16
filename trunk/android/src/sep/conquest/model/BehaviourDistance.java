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
     * @param next A Reference to the next behaviour.
     */
    protected BehaviourDistance(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], GridMap> execute(Map<int[], GridMap> map) {
        return super.execute(map);
    }

}
