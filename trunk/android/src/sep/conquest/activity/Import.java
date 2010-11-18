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
	 * Called when Activity is initially created.
	 * 
	 * Initializes the layout of the Activity and its control elements.
	 * 
	 * @param savedInstanceState
	 *            Bundle of state information, saved when Activity was executed
	 *            before.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Sets the menu of the Activity.
	 * 
	 * @param menu
	 *            Reference on menu that has to be set.
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
	 *            The MenuItem that has been selected.
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
}
