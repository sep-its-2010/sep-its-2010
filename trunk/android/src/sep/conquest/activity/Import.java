package sep.conquest.activity;

import sep.conquest.R;
import sep.conquest.model.MapFileHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Allows user to choose a map file form the file system and load it into Map
 * Activity or Simulation Activity.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class Import extends Activity {

  /**
   * Used to send GridMaps in Intent messages.
   */
  public static final String EXTRA_FILE_PATH = "Path";

  /**
   * Used to list available maps.
   */
  private ListView lsMaps;

  /**
   * Used to display names of found map files.
   */
  private ArrayAdapter<String> fileList;

  /**
   * Used to identify last recently selected map.
   */
  private String selectedMap;

  /**
   * Called when Activity is initially created.
   * 
   * Initializes the layout of the Activity and its control elements.
   * 
   * @param savedInstanceState
   *          Bundle of state information, saved when Activity was executed
   *          before.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    // Call constructor of super class
    super.onCreate(savedInstanceState);

    // Set layout of Activity
    setContentView(R.layout.import_main);

    // Get reference on ListView to display map files from the file system.
    lsMaps = (ListView) findViewById(R.id.lsMaps);
    lsMaps.setOnItemClickListener(new OnItemClickListener() {

      private int lastSelected = -1;

      /**
       * Handles clicks on items of ListView containing the available maps.
       * 
       * If an item is selected, its text color changes.
       * 
       * @param parent
       *          AdapterView containing the list item.
       * @param view
       *          List item that has been clicked.
       * @param position
       *          Position of the list item within the AdapterView.
       */
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
    	  
    	  TextView item = (TextView) view;
    	  selectedMap = item.getText().toString();
    	  
    	  
    	        if (selectedMap != null) {
    	          // Determine, whether Activity has been started to display a map or
    	          // if it has been started to open a configuration for the simulator.
    	          ImportMode mode = (ImportMode) getIntent().getSerializableExtra(
    	              ImportMode.class.toString());

    	          // Intent message to start other Activities.
    	          Intent start = new Intent();
    	          start.putExtra(EXTRA_FILE_PATH, selectedMap);

    	          switch (mode) {
    	          case SIMULATION_MAP:
    	            // If Import was launched to open a simulator configuration, set
    	            // result code and finish Activity.
    	            setResult(RESULT_OK, start);
    	            finish();
    	            break;
    	          case IMPORT_MAP:
    	            // If Import was launched to view a map, start Map Activity.
    	            start.setComponent(new ComponentName(getApplicationContext()
    	                .getPackageName(), Map.class.getName()));
    	            start.putExtra(MapMode.class.toString(), MapMode.IMPORT);
    	            startActivity(start);
    	            break;
    	          }
    	        } else {
    	          // If no map was selected, display note message.
    	          displayMessage(getString(R.string.MSG_NO_MAP_SELECTED), false);
    	        }
    	      
      }
    });
    // Display path of the directory.
    TextView txtDirectory = (TextView) findViewById(R.id.txtDirectory);
    txtDirectory.setText(MapFileHandler.getDirectoryPath());

    // Display the files found in the application directory.
    displayFiles();
  }

  /**
   * Sets the menu of the Activity.
   * 
   * @param menu
   *          Reference on menu that has to be set.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.import_menu, menu);
    return true;
  }

  /**
   * Handles selections of the menu of the Activity.
   * 
   * @param item
   *          The MenuItem that has been selected.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.mnuOpen) {
      if (selectedMap != null) {
        // Determine, whether Activity has been started to display a map or
        // if it has been started to open a configuration for the simulator.
        ImportMode mode = (ImportMode) getIntent().getSerializableExtra(
            ImportMode.class.toString());

        // Intent message to start other Activities.
        Intent start = new Intent();
        start.putExtra(EXTRA_FILE_PATH, selectedMap);

        switch (mode) {
        case SIMULATION_MAP:
          // If Import was launched to open a simulator configuration, set
          // result code and finish Activity.
          setResult(RESULT_OK, start);
          finish();
          break;
        case IMPORT_MAP:
          // If Import was launched to view a map, start Map Activity.
          start.setComponent(new ComponentName(getApplicationContext()
              .getPackageName(), Map.class.getName()));
          start.putExtra(MapMode.class.toString(), MapMode.IMPORT);
          startActivity(start);
          break;
        }
      } else {
        // If no map was selected, display note message.
        displayMessage(getString(R.string.MSG_NO_MAP_SELECTED), false);
      }
    }
    return true;
  }

  /**
   * Displays all files found in the application directory.
   * 
   * If Import was launched to open Simulator configuration, only .sim files are
   * displayed.
   * 
   * If Import was launched to view a map, only .sep files are displayed.
   */
  private void displayFiles() {
    // Determine Import mode.
    ImportMode mode = (ImportMode) getIntent().getSerializableExtra(
        ImportMode.class.toString());

    // Saves the file names returned by the MapFileHandler class.
    String[] files = null;

    switch (mode) {
    case IMPORT_MAP:
      // Get all the map files (.sep).
      files = MapFileHandler.getMapFileList();
      break;
    case SIMULATION_MAP:
      // Get all the simulator configurations (.sim).
      files = MapFileHandler.getSimFileList();
      break;
    }
    // If files equals null then storage is not readable.
    if (files == null) {
      displayMessage(getString(R.string.ERR_MSG_STORAGE_NOT_READABLE), true);
    } else {
      fileList = new ArrayAdapter<String>(this, R.layout.list_item, files);
      lsMaps.setAdapter(fileList);
    }
  }

  /**
   * Displays a message on top of the Activity.
   * 
   * @param message
   *          The message to display.
   * @param isError
   *          True if message should be displayed as an error message, false
   *          otherwise.
   */
  private void displayMessage(final String message, final boolean isError) {
    // Get new DialogBuilder.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setCancelable(false);

    // If message is an error message set error icon and error title.
    // Otherwise set warning icon and warning title.
    if (isError) {
      builder.setTitle(getString(R.string.ERR_TITLE_ERROR));
      builder.setIcon(R.drawable.err_error);
    } else {
      builder.setTitle(getString(R.string.ERR_TITLE_WARNING));
      builder.setIcon(R.drawable.err_warning);
    }

    // Add button to the dialog.
    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

      public void onClick(final DialogInterface dialog, final int which) {
        dialog.dismiss();
      }
    });
    // Create and show dialog.
    AlertDialog alert = builder.create();
    alert.show();
  }
}