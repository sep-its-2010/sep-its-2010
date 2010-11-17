package sep.conquest.model;

/**
 * Contains methods to send control commands to an implementing class.
 * 
 * @author Andreas Poxrucker
 *
 */
public interface IRobot {

  /**
   * Sends 'left'-command to implementing class.
   */
  void left();
  
  /**
   * Sends 'right'-command to implementing class.
   */
  void right();
  
  /**
   * Sends 'up'-command to implementing class.
   */
  void forward();
  
  /**
   * Sends 'turn'-command to implementing class.
   */
  void turn();
  
  /**
   * Sends command to set speed to implementing class.
   * 
   * @param level The speed to set.
   */
  void setSpeed(SpeedLevel level);
}
