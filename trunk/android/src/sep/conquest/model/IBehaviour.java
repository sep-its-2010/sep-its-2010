package sep.conquest.model;

import java.util.Map;

/**
 * Every single logic-stage of the navigation has to implement the IBehaviour
 * interface. The execute method will be used to do calcualtions on a given
 * map in order to the specific behaviour.
 * 
 * @author Andreas Wilhelm
 *
 */
public interface IBehaviour {

    /**
     * The execute method will do some logic-dependent calculations on a map
     * in order to navigation-decisions. It will return the resulting map
     * with new values. 
     *  
     * @param map the input map.
     * @return the map with new values.
     */
    Map<int[], Object> execute(Map<int[], Object> map);    
}
