package sep.conquest.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapFileHandler;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Allows user to open a previously saved map from the file system.
 * 
 * The application then runs in import mode.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class Import extends Activity {

  /**
   * Used to list available maps.
   */
  private ListView lsMaps;

  /**
   * Used to display names of found map files.
   */
  private ArrayAdapter<String> fileList;

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
  public void onCreate(Bundle savedInstanceState) {
    // Call constructor of super class
    super.onCreate(savedInstanceState);

    // Set layout of Activity
    setContentView(R.layout.import_main);

    // Get reference on ListView to display map files from the file system.
    lsMaps = (ListView) findViewById(R.id.lsMaps);
    lsMaps.setOnItemClickListener(new ImportOnItemClickListener());

    // Display the list of found map files from the file system in the ListView.
    displayMapFiles();
  }

  /**
   * Sets the menu of the Activity.
   * 
   * @param menu
   *          Reference on menu that has to be set.
   */
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
  public boolean onOptionsItemSelected(MenuItem item) {
    // Intent message to start other Activities.
    Intent start = new Intent();

    switch (item.getItemId()) {

    // If "Connect" has been chosen, start Connect-Activity via Intent.
    case R.id.mnuConnect:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Connect.class.getName()));
      startActivity(start);
      break;

    // If "Simulation" has been chosen, start Simulation-Activity via Intent.
    case R.id.mnuSimulation:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Simulation.class.getName()));
      startActivity(start);
      break;

    // If "Start" has been chosen, start Map-Activity via Intent.
    case R.id.mnuStart:

      if (selectedMap != null) {
        try {
          GridMap map = MapFileHandler.openMap(selectedMap);
          Controller.getInstance().getEnv().loadMap(map);
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          displayMessage("File not found");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          displayMessage("IO ex");
        }
        start.setComponent(new ComponentName(getApplicationContext()
            .getPackageName(), Map.class.getName()));
        start.putExtra(MapMode.class.toString(), MapMode.IMPORT);
        startActivity(start);
      } else {
        displayMessage("No map selected");
      }
      break;
    }
    return true;
  }

  private void displayMapFiles() {
    fileList = new ArrayAdapter<String>(this, R.layout.list_item,
        MapFileHandler.getFileList());
    lsMaps.setAdapter(fileList);
  }

  private void displayMessage(String message) {
    Toast mtoast = Toast.makeText(this, message, Toast.LENGTH_LONG);
    mtoast.setGravity(Gravity.CENTER, 0, 0);
    mtoast.show();
  }

  /**
   * Handles clicks on items of ListView.
   * 
   * @author Andreas Poxrucker
   */
  private final class ImportOnItemClickListener implements OnItemClickListener {

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
      TextView txt = (TextView) view;
      selectedMap = txt.getText().toString();
    }
  }
}
