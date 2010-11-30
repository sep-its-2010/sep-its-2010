package sep.conquest.activity;

import java.io.IOException;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.Environment;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapFileHandler;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Saves a map to the file system of the smartphone.
 * 
 * The user can specify the name of the file. The folder is fixed.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class Export extends Activity {

  /**
   * The EditText element where filename can be entered.
   */
  private EditText etxtFilename;
  
  private ListView lsMaps;

  /**
   * Used to display names of found map files.
   */
  private ArrayAdapter<String> fileList;

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
    super.onCreate(savedInstanceState);
    setContentView(R.layout.export_main);

    initializeControlElements();
  }

  /**
   * Initializes control elements of the activity and sets EventListeners.
   */
  private void initializeControlElements() {
    Button btnSave = (Button) findViewById(R.id.btnSave);
    btnSave.setOnClickListener(new ExportOnClickListener());

    etxtFilename = (EditText) findViewById(R.id.etxtFilename);

    // Get reference on ListView to display map files from the file system.
    lsMaps = (ListView) findViewById(R.id.lsMaps);
    lsMaps.setOnItemClickListener(new ExportOnItemClickListener());

    // Display the list of found map files from the file system in the ListView.
    displayMapFiles();
  }
  
  private void displayMapFiles() {
    fileList = new ArrayAdapter<String>(this, R.layout.list_item,
        MapFileHandler.getFileList());
    lsMaps.setAdapter(fileList);
  }

  /**
   * Displays a message on top of the Activity.
   * 
   * @param message
   *          The message to display.
   */
  private void displayMessage(String message) {
    Toast mtoast = Toast.makeText(this, message, Toast.LENGTH_LONG);
    mtoast.setGravity(Gravity.CENTER, 0, 0);
    mtoast.show();
  }

  /**
   * Handles click events on control elements of the Activity.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private class ExportOnClickListener implements OnClickListener {

    /**
     * Called when registered button receives OnClick-Event.
     * 
     * @param view
     *          View that has been clicked.
     */
    public void onClick(View v) {
      switch (v.getId()) {

      // If 'Save' button has been clicked, check
      case R.id.btnSave:
        String filename = etxtFilename.getText().toString();

        Environment env = Controller.getInstance().getEnv();
        GridMap map = env.getMap();

        try {
          if (MapFileHandler.saveMap(map, filename)) {
            displayMessage("Map successfully saved");
            finish();
          } else {
            displayMessage("Unable to save map");
          }
        } catch (IOException e) {
          displayMessage("Error");
        }
        break;
      }
    }
  }

  /**
   * Handles clicks on items of ListView.
   * 
   * @author Andreas Poxrucker
   */
  private final class ExportOnItemClickListener implements OnItemClickListener {

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
      String filename = txt.getText().toString();
      etxtFilename.setText(filename);
    }
  }
}
