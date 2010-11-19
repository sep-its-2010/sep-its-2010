package sep.conquest.model;

import java.util.Map;

/**
 * InnerBehaviour represents a behaviour to identify inner areas of frontier
 * nodes within a map. It extends the Behaviour class for enabling a behaviour-
 * chain.
 * 
 * @author Andreas Wilhelm
 */
public final class InnerBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected InnerBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], GridMap> execute(Map<int[], GridMap> map) {
        return super.execute(map);
    }

}
