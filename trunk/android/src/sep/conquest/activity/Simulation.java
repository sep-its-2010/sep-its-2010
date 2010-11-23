package sep.conquest.activity;

import java.util.LinkedList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Allows user to open a map from the file system and specify the number of
 * participating virtual robots.
 * The application then runs in simulation mode.
 * 
 * @author Florian Buerchner
 * 
 */
public class Simulation extends Activity {

	/**
	 * Used to open a map from the file-system.
	 */
	Button btnOpen;

	/**
	 * Called when the Activity is initially created.
	 * 
	 * Initializes layout of Activity and its control elements.
	 * 
	 * @param savedInstanceState
	 *            Bundle of state information, saved when Activity was executed
	 *            before.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simulation_main);
		
		Spinner numberOfEpucks = new Spinner(this);
		
		btnOpen = new Button(this);
		btnOpen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//make an intent and go to import dialog.
			}
		});
		
	}

	/**
	 * Sets the menu of the Activity.
	 * 
	 * @param menu
	 *            Reference on menu that has to be set.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simulation_menu, menu);
		return true;
	}

	/**
	 * Handles selections of menu items.
	 * 
	 * @param item
	 *            The menu item that has been selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent start = new Intent();

		switch (item.getItemId()) {
		case R.id.mnuConnect:
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Connect.class.getName()));
			startActivity(start);
			break;
		case R.id.mnuStart:
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Map.class.getName()));
			startActivity(start);
			break;
		case R.id.mnuImport:
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Import.class.getName()));
			startActivity(start);
			break;
		}
		return true;
	}	
	
	/**
	 * Draws a preview of the selected map. For this the MapSurfaceView class
	 * is used and a new DrawThread is started. In the simulation mode it is 
	 * possible to select the start positions for the virtual robots.
	 * 
	 * @param map Contains an imported map.
	 */
	public void drawPreview(LinkedList map) {
		//binde ein neues MapSurfaceView ein um die Preview anzuzeigen
		//wenn es in der xml datei eine neue id ist wird auch eine neue
		//Instanz dieser datei erzeugt.
	}

}
