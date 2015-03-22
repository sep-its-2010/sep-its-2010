package sep.conquest.model.behaviour;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sep.conquest.model.Puck;
import sep.conquest.model.State;

/**
 * LocalLocalizeBehaviour represents a behaviour for localizing adjacent robots.
 * It extends the Behaviour class for enabling a behaviour-chain.
 * 
 * @author Andreas Wilhelm
 */
public final class IdleBehaviour extends Behaviour {

  /**
   * Delay of start time of timer.
   */
  private static long DELAY = 2000;

  /**
   * Indicates whether timer has been started.
   */
  private boolean timerActive = false;

  /**
   * The constructor enables chain-handling by calling the constructor of the
   * super-class (Behaviour).
   * 
   * @param stateLevel
   *          The level of the state.
   * @param next
   *          A reference to the next behaviour.
   */
  protected IdleBehaviour(State stateLevel, IBehaviour next) {
    super(stateLevel, next);
  }

  /*
   * (non-Javadoc)
   * 
   * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
   */
  public boolean execute(Map<Integer, Integer> map, Puck robot) {

    if (!timerActive) {
      // If timer is not active, start it with a certain delay.
      // During this time, hello messages can be exchanged between
      // Pucks.
      timerActive = true;
      Timer timer = new Timer();
      timer.schedule(new ExecuteTimer(robot), DELAY);
    }
    // Execute next behaviour if exits.
    return super.execute(map, robot);
  }

  /**
   * Initiates a state transition from IDLE to LOCALIZING as soon as it is run.
   * 
   * @author Andreas Wilhelm
   * 
   */
  private class ExecuteTimer extends TimerTask {

    /**
     * The Puck whose state will be changed.
     */
    private Puck robot;

    /**
     * Constructor.
     * 
     * @param robot
     *          The Puck whose state will be changed.
     */
    public ExecuteTimer(Puck robot) {
      this.robot = robot;
    }

    /**
     * When executed, changes state of Puck.
     */
    @Override
    public void run() {
      if (robot != null) {
        robot.changeBehaviour(State.LOCALIZE);
      }
    }
  }
}