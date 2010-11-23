package sep.conquest.activity;

import sep.conquest.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;


/**
 * The Map class extends from Activity and is responsible for the representation
 * of the map and the selection of e-pucks.
 * 
 * @author Florian Buerchner
 *
 */
public class Map extends Activity{
	
	/**
	 * Is implemented by Activity and is called when the map class is accessed.
	 * It initializes the spinner control element for the selection of the 
	 * e-pucks and calculates the dimensions of the display. They must be
	 * available for the MapSurfaceView, but this class itself can't get this
	 * information, because they are only available in Activity classes.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int x = metrics.widthPixels;
        int y = metrics.heightPixels;
        */
        
        setContentView(R.layout.map_main); 
        
        Spinner ePuckSelect = (Spinner) findViewById(R.id.epuck_selector);
        
    }
    
    /**
     * Sets the menu of the Activity.
     * 
     * @param menu Reference on menu that has to be set.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MapMode mode = (MapMode) getIntent().getSerializableExtra(MapMode.class.toString());
        
        switch(mode) {
        case EXPLORATION: inflater.inflate(R.menu.map_exp_menu, menu); break;
        case SIMULATION: inflater.inflate(R.menu.map_sim_menu, menu); break;
        case IMPORT: break;
        }
        
        return true;
    }
    
    /**
     * Handles selections of menu items.
     * 
     * @param item The menu item that has been selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.mnuStatistics:
			Intent stats = new Intent();
			stats.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Statistics.class.getName()));
			startActivity(stats);
			break;

		case R.id.mnuControl:
			Intent steer = new Intent();
			steer.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Steer.class.getName()));
			startActivity(steer);
			break;
			
        case R.id.mnuExport:
			Intent export = new Intent();
			export.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Export.class.getName()));
			startActivity(export);
			break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    /**
     * This is the listener class which waits for an event in the ePuckSelect
     * Spinner. The selected e-puck is indicated on the map.
     *  
     * @author Florian Buerchner
     *
     */
    private class OnEpuckSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
    

}
