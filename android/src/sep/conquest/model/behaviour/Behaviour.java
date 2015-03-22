package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

/**
 * The abstract class Behaviour should be derived by all concrete behaviours due
 * to have a centralized logic-approach. It forces a chain of logic-classes and
 * the corresponding chain-handling.
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
   * The constructor sets the reference to the next Behaviour in the chain as
   * well as its state-level. If this is the first entry for a specific state
   * level, this Behaviour will set as the agent for it.
   * 
   * @param stateLevel
   *          The corresponding state-level.
   * @param next
   *          The reference to the next Behaviour.
   */
  protected Behaviour(State stateLevel, IBehaviour next) {
    this.stateLevel = stateLevel;
    this.nextBehaviour = next;
  }

  /**
   * Returns the level of the Behaviour.
   * 
   * @return The level of the Behaviour.
   */
  public State getStateLevel() {
    return stateLevel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sep.conquest.model.IBehaviour#setNextBehaviour(sep.conquest.model.IBehaviour
   * )
   */
  public void setNextBehaviour(IBehaviour next) {
    nextBehaviour = next;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
   */
  public boolean execute(Map<Integer, Integer> map, Puck robot) {
    if (nextBehaviour != null) {
      return nextBehaviour.execute(map, robot);
    } else {
      return false;
    }
  }
}
