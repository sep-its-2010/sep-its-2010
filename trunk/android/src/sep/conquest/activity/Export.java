package sep.conquest.activity;

import java.io.IOException;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapFileHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
   * Displays previous saved maps from the file system.
   */
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

    // Get references on control elements and set Listener.
    initializeControlElements();
  }

  /**
   * Initializes control elements of the activity and sets EventListeners.
   */
  private void initializeControlElements() {
    Button btnSave = (Button) findViewById(R.id.btnSave);
    btnSave.setOnClickListener(new OnClickListener() {

      /**
       * Saves the map to the file system.
       * 
       * @param view
       *          View that has been clicked.
       */
      public void onClick(View v) {
        // Get filename of the text box
        EditText etxtFilename = (EditText) findViewById(R.id.etxtFilename);
        String filename = etxtFilename.getText().toString();

        // Get map from environment
        GridMap map = Controller.getInstance().getEnv().getMap();

        try {
          if (MapFileHandler.saveMap(map, filename)) {
            finish();
          } else {
            displayMessage(getString(R.string.ERR_MSG_NOT_SUCCESSFULLY_SAVED), true);
          }
        } catch (IOException e) {
          displayMessage(getString(R.string.ERR_MSG_NOT_SUCCESSFULLY_SAVED), true);
        }
      }
    });

    // Get reference on ListView to display map files from the file system.
    lsMaps = (ListView) findViewById(R.id.lsMaps);
    lsMaps.setOnItemClickListener(new OnItemClickListener() {
      
      /**
       * Copys the text (file name) of an item into the EditText.
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
        EditText etxtFilename = (EditText) findViewById(R.id.etxtFilename);
        etxtFilename.setText(filename);
      }
    });

    // Display the list of found map files from the file system in the ListView.
    displayMapFiles();
  }

  private void displayMapFiles() {
    fileList = new ArrayAdapter<String>(this, R.layout.list_item,
        MapFileHandler.getMapFileList());
    lsMaps.setAdapter(fileList);
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