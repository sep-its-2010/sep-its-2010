package sep.conquest.model.behaviour;

import java.util.Map;

import sep.conquest.model.Puck;

/**
 * Every single logic-stage of the navigation has to implement the IBehaviour
 * interface. The execute method will be used to do calculations on a given
 * map in order to the specific behaviour.
 * 
 * @author Andreas Wilhelm
 *
 */
public interface IBehaviour {

    /**
     * The execute method will do some logic-dependent calculations on the map
     * of a robot in order to navigation-decisions. It will return the resulting
     * map with new navigation-costs. 
     *  
     * @param map The map with given navigation-costs.
     * @param robot The robot.
     * @return The map with new navigation-costs.
     */
    Map<int[], Integer> execute(Map<int[], Integer> map, Puck robot);    
    
    /**
     * Sets the following Behaviour.
     * 
     * @param next The next Behaviour.
     */
     void setNextBehaviour(IBehaviour next);   
}
