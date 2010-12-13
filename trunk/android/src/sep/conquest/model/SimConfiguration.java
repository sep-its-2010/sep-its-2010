package sep.conquest.model;

/**
 * Utility class used to transfer data from MapFileHandler to Import Activity.
 * 
 * @author Andreas Poxrucker
 *
 */
public class SimConfiguration {

  /**
   * The map to transfer.
   */
  private GridMap map;
  
  /**
   * The positions to transfer.
   */
  private int[][] pos;
  
  /**
   * The orientations to transfer.
   */
  private Orientation[] ori;

  /**
   * Constructor.
   * 
   * @param openMap The map to transfer.
   * @param positions The positions to transfer.
   * @param orientations The orientations to transfer.
   */
  public SimConfiguration(GridMap openMap, int[][] positions,
      Orientation[] orientations) {
    map = openMap;
    pos = positions;
    ori = orientations;
  }
  
  /**
   * Returns the contained map.
   * 
   * @return  The map saved in the container.
   */
  public GridMap getMap() {
    return map;
  }
  
  /**
   * Returns the contained positions.
   * 
   * @return  The positions saved in the container.
   */
  public int[][] getPositions() {
    return pos;
  }
  
  /**
   * Returns the contained orientations.
   * 
   * @return  The orientations saved in the container.
   */
  public Orientation[] getOrientations() {
    return ori;
  }
}