package sep.conquest.model;

import java.util.Map;

/**
 * GlobalLocalizeBehaviour represents a behaviour for localizing robots on
 * arbitrary map-nodes. It extends the Behaviour class for enabling a
 * behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class GlobalLocalizeBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected GlobalLocalizeBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public Map<int[], GridMap> execute(Map<int[], GridMap> map) {
        return super.execute(map);
    }

}
