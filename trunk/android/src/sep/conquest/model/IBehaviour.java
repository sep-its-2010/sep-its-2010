package sep.conquest.model;

import java.util.Map;

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
     * The execute method will do some logic-dependent calculations on a map
     * in order to navigation-decisions. It will return the resulting map
     * with new values. 
     *  
     * @param map The input map.
     * @return The map with new values.
     */
    Map<int[], GridMap> execute(Map<int[], GridMap> map);    
}
