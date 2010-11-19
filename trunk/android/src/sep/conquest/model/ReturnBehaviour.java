package sep.conquest.model;

import java.util.Map;

/**
 * ReturnBehaviour represents a behaviour for robots that have to return to
 * their start-nodes. It extends the Behaviour class for enabling a
 * behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class ReturnBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected ReturnBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], GridMap> execute(Map<int[], GridMap> map) {
        return super.execute(map);
    }

}
