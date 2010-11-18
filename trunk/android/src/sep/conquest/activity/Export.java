package sep.conquest.activity;

import sep.conquest.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
      // TODO Auto-generated method stub
      
    }
    
  }
}
