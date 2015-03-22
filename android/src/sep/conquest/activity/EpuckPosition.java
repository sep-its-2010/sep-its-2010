package sep.conquest.activity;

import sep.conquest.model.Orientation;


/**
 * This class saves the position of every single e-puck within the map. The
 * roboter can be identified by its id.
 *
 * @author Florian Buerchner
 *
 */
public class EpuckPosition {

    /**
     * Saves the current x coordinate of the robot.
     */
    private int x;

    /**
     * Saves the current y coordinate of the robot.
     */
    private int y;

    /**
     * Saves the id of the robot to identify it.
     */
    private String id;
    
    /**
     * Saves the current orientation of the robot.
     */
    private Orientation ori;

    /**
     * If the MapSurfaceView detects a new e-puck on the map, a new
     * ePuckPosition object is created which saves the current x and y
     * coordinate and his id to indentify it.
     *
     * @param newX Current x coordinate on the map.
     * @param newY Current y coordinate on the map.
     * @param newID Identifier of the roboter.
     */
    public EpuckPosition(final int newX, final int newY, final String newID,
    					 final Orientation orientation) {
        x = newX;
        y = newY;
        id = newID;
        ori = orientation;
        
    }

    /**
     * Update the x coordinate of an e-puck.
     *
     * @param currentX New x coordinate.
     */
    public final void setX(final int currentX) {
        x = currentX;
    }

    /**
     * Update the y coordinate of an e-puck.
     *
     * @param currentY New y of an e-puck.
     */
    public final void setY(final int currentY) {
        y = currentY;
    }

    /**
     * Returns the x coordinate of an e-puck.
     *
     * @return Current x coordinate.
     */
    public final int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of an e-puck.
     *
     * @return Current y coordinate.
     */
    public final int getY() {
        return y;
    }

    /**
     * Returns the id of an e-puck.
     *
     * @return Identifier of the roboter.
     */
    public final String getID() {
        return id;
    }
    
    /**
     * Returns the orientation of the robot.
     *
     * @return Orientation.
     */
    public final Orientation getOrientation() {
    	return ori;
    }

}

