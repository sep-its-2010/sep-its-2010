package sep.conquest.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.os.Environment;

/**
 * Contains methods to open maps from the file system and to save maps.
 * 
 * All files are saved to and loaded from external storage, directory
 * "/Android/data/sep.conquest/files" (Android convention).
 * 
 * To save data on the external storage, permission WRITE_EXTERNAL_STORAGE is
 * required.
 * 
 * Maps are saved with ending .sep, Simulator configurations are saved with
 * ending .sim.
 * 
 * @author Andreas Poxrucker
 * 
 */
public final class MapFileHandler {

  /**
   * The path where the maps will be saved.
   */
  private static final File DIR = new File(Environment
      .getExternalStorageDirectory().getAbsolutePath()
      + "/Android/data/sep.conquest/files/");

  /**
   * Describes valid file names with file ending .sep.
   */
  private static final Pattern MAP_FILE_NAME_FORMAT = Pattern
      .compile("\\p{Alnum}+\\w*\\.sep");

  /**
   * Describes valid file names with file ending .sim.
   */
  private static final Pattern SIM_FILE_NAME_FORMAT = Pattern
      .compile("\\p{Alnum}+\\w*\\.sim");

  /**
   * Opens a file containing the number of participating Pucks, a serialized
   * GridMap and the starting positions and initial orientations.
   * 
   * @param filename
   *          The name of the map file to open.
   * 
   * @return Simulator configuration containing the reconstructed GridMap and
   *         all the initial positions and orientations.
   * 
   * @throws IOException
   *           May occur while reading the file.
   * @throws FileNotFoundException
   *           No file with name filename has been found.
   */
  public static SimConfiguration openConfiguration(String filename)
      throws IOException, FileNotFoundException {

    if (filename != null) {
      // Create new file with passed file name.
      File mapFile = new File(DIR, filename);

      // Used to read the file.
      FileReader fReader = new FileReader(mapFile);

      // Used read file with readln().
      BufferedReader bReader = new BufferedReader(fReader);

      // The headline contains a single digit (0-6) about how many Pucks
      // will take part in an exploration.
      String headline = bReader.readLine();
      int number;
      
      try {
        number = Integer.parseInt(headline);
      } catch (NumberFormatException ex) {
        throw new IOException("Illegal file format");
      }

      // Number must be between 0 and 6
      if ((number < 1) || (number > 6)) {
        throw new IOException("Illegal file format");
      }

      // The GridMap that will be returned.
      GridMap map = new GridMap();

      // The number of positions/orientations read.
      int posRead = 0;

      // Saves positions and orientations.
      int[][] positions = new int[number][];
      Orientation[] orientations = new Orientation[number];

      // Iterate over the file and read it line by line.
      // Then split line, parse values and add new Node to map
      for (String line = bReader.readLine(); line != null; line = bReader
          .readLine()) {
        String[] tokens = line.split(" ");

        // Line must contain at least three tokens, otherwise the file format
        // is illegal.
        if ((tokens.length < 3) || (tokens.length > 4)) {
          throw new IOException("Illegal file format");
        } else {
          int x;
          int y;
          
          try {
            x = Integer.parseInt(tokens[0]);
            y = Integer.parseInt(tokens[1]);
          } catch (NumberFormatException ex) {
            throw new IOException("Illegal file format");
          }
          NodeType type = NodeType.valueOf(tokens[2]);
          map.addNode(x, y, type);

          // If line contains four tokens, then the last one indicates that
          // the position is a start position for a robot.
          // The value of the fourth token (0-3) indicates the orientation of
          // robot.
          if (tokens.length == 4) {
            int index;;
            
            try {
              index = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException ex) {
              throw new IOException("Illegal file format");
            }
            
            // If orientation is invalid, throw Exception.
            if ((index > 3) || (index < 0)) {
              throw new IOException("Configuration contains illegal orientation");
            }
            
            Orientation ori = Orientation.values()[index];
            int[] pos = { x, y };
            positions[posRead] = pos;
            orientations[posRead] = ori;
            posRead++;

            if (posRead > number) {
              throw new IOException("Illegal file format");
            }
          }
        }
      }
      // Close readers and return reconstructed map.
      bReader.close();
      fReader.close();
      
      // If map contains frontier nodes then map is invalid.
      if (map.getFrontierList().size() != 0) {
        throw new IOException("File contains an invalid map");
      }
      return new SimConfiguration(map, positions, orientations);
    } else {
      // Thrown, when filename equals null.
      throw new IllegalArgumentException("File name must not equal null");
    }
  }

  /**
   * Opens a file containing a serialized GridMap.
   * 
   * @param filename
   *          The name of the map file to open.
   * 
   * @return The reconstructed GridMap.
   * 
   * @throws IOException
   *           May occur while reading the file.
   * @throws FileNotFoundException
   *           No file with name filename has been found.
   */
  public static GridMap openMap(String filename) throws IOException,
      FileNotFoundException {

    if (filename != null) {
      // Create new file with passed file name.
      File mapFile = new File(DIR, filename);

      // Used to read the file.
      FileReader fReader = new FileReader(mapFile);

      // Used read file with readln().
      BufferedReader bReader = new BufferedReader(fReader);

      // The GridMap that will be returned.
      GridMap map = new GridMap();

      // Iterate over the file and read it line by line.
      // Then split line, parse values and add new Node to map
      for (String line = bReader.readLine(); line != null; line = bReader
          .readLine()) {
        String[] tokens = line.split(" ");

        // Line must contain three tokens, otherwise the file format
        // is illegal.
        if (tokens.length != 3) {
          throw new IOException("Illegal file format");
        } else {
          int x = Integer.parseInt(tokens[0]);
          int y = Integer.parseInt(tokens[1]);
          NodeType type = NodeType.valueOf(tokens[2]);
          map.addNode(x, y, type);
        }
      }
      // Close readers and return reconstructed map.
      bReader.close();
      fReader.close();
      return map;
    } else {
      // Thrown, when filename equals null.
      throw new IllegalArgumentException("File name must not equal null");
    }
  }

  /**
   * Saves a map as file in the file system.
   * 
   * @param map
   *          The map that has to be saved.
   * @return True, if map was successfully saved, false, if file name is invalid
   *         or external storage can not be written.
   * @throws IOException
   */
  public static boolean saveMap(GridMap map, String filename)
      throws IOException {
    // Check, if parameter are not equal to null.
    if ((map != null) && (filename != null)) {

      // Check, if filename is valid and if external media can be written.
      if (isValidMapFilename(filename) && isWriteable()) {

        if (!DIR.exists()) {
          DIR.mkdirs();
        }
        File mapFile = new File(DIR, filename);

        // If map file exists, remove it and create a new one.
        if (mapFile.exists()) {
          mapFile.delete();
        }
        mapFile.createNewFile();

        // Used to write into file.
        FileWriter fWriter = new FileWriter(mapFile);

        // Serialize map to string array where each line represents one node.
        String[] serializedMap = map.serializeMapInString();

        // Iterate over string array and write nodes to file.
        for (String str : serializedMap) {
          fWriter.write(str);
        }
        // Close file writer and return true.
        fWriter.close();
        return true;
      } else {
        return false;
      }
    } else {
      // Thrown, when either map or filename equals null.
      throw new IllegalArgumentException("Map and filename must not equal null");
    }
  }

  /**
   * Returns an array of map files names ending on .sep.
   * 
   * @return Array of files ending on .sep or null, if directory is not
   *         readable.
   */
  public static String[] getMapFileList() {
    // If directory is readable, iterate over contained files and add them
    // to list, if their ending is .sep. Otherwise return null.
    if (isReadable()) {
      if (DIR.exists()) {
        // Saves filenames ending on .sep temporary.
        List<String> files = new LinkedList<String>();

        // Get list of all files contained in directory.
        String[] dirFiles = DIR.list();

        // Iterate over contained files and add them to list, if their name ends
        // on .sep.
        for (String file : dirFiles) {
          if (isValidMapFilename(file)) {
            files.add(file);
          }
        }
        String[] arrFiles = new String[files.size()];

        for (int i = 0; i < arrFiles.length; i++) {
          arrFiles[i] = files.get(i);
        }
        // Convert List to String array and return it.
        return arrFiles;
      } else {
        return new String[0];
      }
    } else {
      return null;
    }
  }

  /**
   * Returns an array of map files names ending on .sim.
   * 
   * @return Array of files ending on .sim or null, if directory is not
   *         readable.
   */
  public static String[] getSimFileList() {
    // If directory is readable, iterate over contained files and add them
    // to list, if their ending is .sep. Otherwise return null.
    if (isReadable()) {
      if (DIR.exists()) {
        // Saves filenames ending on .sep temporary.
        List<String> files = new LinkedList<String>();

        // Get list of all files contained in directory.
        String[] dirFiles = DIR.list();

        // Iterate over contained files and add them to list, if their name ends
        // on .sep.
        for (String file : dirFiles) {
          if (isValidSimFilename(file)) {
            files.add(file);
          }
        }
        String[] arrFiles = new String[files.size()];

        for (int i = 0; i < arrFiles.length; i++) {
          arrFiles[i] = files.get(i);
        }
        // Convert List to String array and return it.
        return arrFiles;
      } else {
        return new String[0];
      }
    } else {
      return null;
    }
  }

  /**
   * Checks, whether name of a file matches the following rules: Name starts
   * with a digit or letter, followed by a variable number of digits, letters or
   * underscores and ends on .sep.
   * 
   * @param filename
   *          The filename to check.
   * @return True, if name of file is valid, false otherwise.
   */
  public static boolean isValidMapFilename(String filename) {
    return MAP_FILE_NAME_FORMAT.matcher(filename).matches();
  }

  /**
   * Checks, whether name of a file matches the following rules: Name starts
   * with a digit or letter, followed by a variable number of digits, letters or
   * underscores and ends on .sim.
   * 
   * @param filename
   *          The filename to check.
   * @return True, if name of file is valid, false otherwise.
   */
  public static boolean isValidSimFilename(String filename) {
    return SIM_FILE_NAME_FORMAT.matcher(filename).matches();
  }

  /**
   * Checks, whether external storage is mounted and can be read.
   * 
   * @return True, if external storage is mounted and can be read, false
   *         otherwise.
   */
  public static boolean isReadable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state)
        || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
  }

  /**
   * Checks, whether external storage is mounted and can be written.
   * 
   * @return True, if external storage is mounted and can be written, false
   *         otherwise.
   */
  public static boolean isWriteable() {
    return Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);
  }
}