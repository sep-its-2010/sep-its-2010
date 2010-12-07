package sep.conquest.activity;

import java.util.UUID;

/**
 * This class saves the position of every single e-puck within the map. The
 * roboter can be identified by its id.
 *
 * @author Florian Buerchner
 *
 */
public class EpuckPosition {

    /**
     * Saves the current x coordinate of the roboter.
     */
    private int x;

    /**
     * Saves the current y coordinate of the roboter.
     */
    private int y;

    /**
     * Saves the id of the roboter to identify it.
     */
    private UUID id;

    /**
     * If the MapSurfaceView detects a new e-puck on the map, a new
     * ePuckPosition object is created which saves the current x and y
     * coordinate and his id to indentify it.
     *
     * @param newX Current x coordinate on the map.
     * @param newY Current y coordinate on the map.
     * @param newID Identifier of the roboter.
     */
    public EpuckPosition(final int newX, final int newY, final UUID newID) {
        x = newX;
        y = newY;
        id = newID;
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
    public final UUID getID() {
        return id;
    }

}

