package sep.conquest.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

/**
 * Contains methods to open maps from the file system and to save maps.
 * 
 * All files are saved to and loaded from external storage, directory
 * "/Android/data/sep.conquest/files" (Android convention).
 * 
 * Maps are saved with ending .sep.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class MapFileHandler {

  /**
   * The path where the maps will be saved.
   */
  private static final File DIR = new File(Environment
      .getExternalStorageDirectory().getAbsolutePath()
      + "/Android/data/sep.conquest/files/");

  /**
   * Describes valid file names with file ending.
   */
  private static final Pattern FILE_NAME_FORMAT = Pattern
      .compile("\\p{Alnum}+\\w*\\.sep");

  /**
   * Opens a file containing a serialized GridMap and reconstruct it.
   * 
   * @param filename
   *          The name of the map file to open.
   * 
   * @return The GridMap extracted from the file.
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
        String[] values = line.split(" ");
        int xValue = Integer.parseInt(values[0]);
        int yValue = Integer.parseInt(values[1]);
        NodeType type = NodeType.valueOf(values[2]);
        map.addNode(xValue, yValue, type);
      }

      // Close readers and return reconstructed map.
      bReader.close();
      fReader.close();
      return map;
    } else {
      // Thrown, when filename equals null.
      throw new IllegalArgumentException();
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
      if (isValidFilename(filename) && isWriteable()) {
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
      throw new IllegalArgumentException();
    }
  }

  /**
   * Returns an array of map files names ending on .sep.
   * 
   * @return Array of files ending on .sep or null, if directory is not
   *         readable.
   */
  public static String[] getFileList() {

    // If directory is readable, iterate over contained files and add them
    // to list, if their ending is .sep.
    // Otherwise return null.
    if (isReadable()) {

      // Saves filenames ending on .sep temporary.
      List<String> files = new LinkedList<String>();

      // Get list of all files contained in directory.
      String[] dirFiles = DIR.list();

      // Iterate over contained files and add them to list, if their name ends
      // on .sep.
      for (String file : dirFiles) {
        if (isValidFilename(file)) {
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
      return null;
    }
  }

  /**
   * Checks, whether name of a file matches the following rules: Name only
   * consists of digits, letters or underscores
   * 
   * @param filename
   *          The filename to check.
   * @return True, if name of file is valid, false otherwise.
   */
  public static boolean isValidFilename(String filename) {
    // Get matcher for regular expression.
    Matcher m1 = FILE_NAME_FORMAT.matcher(filename);

    // True, if m1 matches.
    return m1.matches();
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