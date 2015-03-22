package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

/**
 * Represents the behaviour executed, when exploration has been successfull and
 * robot has returned to its inital position.
 * 
 * Sets all LED the first time the behaviour is executed.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class FinishBehaviour extends Behaviour {
  /**
   * 
   */
  private boolean firstRun = true;

  /**
   * Constructor.
   * 
   * @param stateLevel The state of the robot.
   * @param next Reference on next behaviour.
   */
  protected FinishBehaviour(State stateLevel, IBehaviour next) {
    super(stateLevel, next);
  }

  /**
   * Executes state behaviour.
   * Sets LEDs when executed for the first time.
   */
  @Override
  public boolean execute(Map<Integer, Integer> map, Puck robot) {

    if (firstRun) {
      robot.setLED((short) 255);
      firstRun = false;
    }
    return false;
  }
}