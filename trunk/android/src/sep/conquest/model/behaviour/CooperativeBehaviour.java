package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

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
    public Map<int[], Integer> execute(Map<int[], Integer> map, Puck robot) {
        return super.execute(map, robot);
    }

}
