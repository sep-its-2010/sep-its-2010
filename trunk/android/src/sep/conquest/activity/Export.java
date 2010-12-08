package sep.conquest.activity;

import java.io.IOException;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.Environment;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapFileHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        String filename = etxtFilename.getText().toString();

        // Get map from environment
        Environment env = Controller.getInstance().getEnv();
        GridMap map = env.getMap();

        try {
          if (MapFileHandler.saveMap(map, filename)) {
            displayMessage(getString(R.string.MSG_SUCCESSFULLY_SAVED), false);
            finish();
          } else {
            displayMessage(getString(R.string.ERR_MSG_NOT_SUCCESSFULLY_SAVED), true);
          }
        } catch (IOException e) {
          displayMessage("Error", true);
        }
      }
    });

    etxtFilename = (EditText) findViewById(R.id.etxtFilename);

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
        etxtFilename.setText(filename);
      }
    });

    // Display the list of found map files from the file system in the ListView.
    displayMapFiles();
  }

  private void displayMapFiles() {
    fileList = new ArrayAdapter<String>(this, R.layout.list_item,
        MapFileHandler.getFileList());
    lsMaps.setAdapter(fileList);
  }

  /**
   * Displays a message in a dialog box.
   * 
   * @param message
   *          The message to display.
   * @param isError
   *          Indicates, whether message should be displayed as error.
   */
  private void displayMessage(String message, boolean isError) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setCancelable(false);
    builder.setNeutralButton(getString(R.string.TXT_OK),
        new DialogInterface.OnClickListener() {

          /**
           * Handles click on "ok" button of dialog
           * Simply closes the dialog.
           */
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    AlertDialog alert = builder.create();
    alert.show();
  }
}