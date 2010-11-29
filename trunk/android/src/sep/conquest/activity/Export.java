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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
   * Used to start saving.
   */
  private Button btnSave;

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
    btnSave = (Button) findViewById(R.id.btnSave);
    btnSave.setOnClickListener(new ExportOnClickListener());

    etxtFilename = (EditText) findViewById(R.id.etxtFilename);
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
}
