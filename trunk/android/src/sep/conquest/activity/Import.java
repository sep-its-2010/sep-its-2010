package sep.conquest.activity;

import java.util.Map;

import sep.conquest.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Allows user to open a previous saved map from the file system.
 * 
 * The application then runs in import mode.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class Import extends Activity {

  /**
   * Used to display headline and current folder.
   */
  private TextView txtHead;

  /**
   * Used to list available maps.
   */
  private ListView lsMaps;

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
    setContentView(R.layout.import_main);
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

    Intent start = new Intent();

    switch (item.getItemId()) {
    case R.id.mnuConnect:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Connect.class.getName()));
      startActivity(start);
      break;
    case R.id.mnuSimulation:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Simulation.class.getName()));
      startActivity(start);
      break;
    case R.id.mnuStart:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Map.class.getName()));
      startActivity(start);
      break;
    }
    return true;
  }

  /**
   * Initializes control elements of the activity and sets EventListeners.
   */
  private void initializeControlElements() {

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

    }
  }

  /**
   * Handles click events on control elements of the Activity.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private class ImportOnClickListener implements OnClickListener {

    /**
     * Called when registered button receives OnClick-Event.
     * 
     * @param view
     *          View that has been clicked.
     */
    public void onClick(View arg0) {
      // TODO Auto-generated method stub
    }
  }
}
