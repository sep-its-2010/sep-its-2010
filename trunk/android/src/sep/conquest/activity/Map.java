package sep.conquest.activity;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * The Map class extends from Activity and is responsible for the representation
 * of the map and the selection of e-pucks.
 *
 * @author Florian Buerchner
 *
 */
public class Map extends Activity implements Observer {
	
	private Spinner mRobotSelect;
	
	private ArrayAdapter < String > mRobotAdapter;
	
	private LinkedList < EpuckPosition > mPositions;

    /**
     * Is implemented by Activity and is called when the map class is accessed.
     * It initializes the spinner control element for the selection of the
     * e-pucks and calculates the dimensions of the display. They must be
     * available for the MapSurfaceView, but this class itself can't get this
     * information, because they are only available in Activity classes.
     *
     * @param savedInstanceState Bundle of state information, saved when
     *                           Activity was executed before.
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_main);

        mPositions = new LinkedList < EpuckPosition >();
        
        mRobotSelect = (Spinner) findViewById(R.id.epuck_selector);
        final MapSurfaceView map = (MapSurfaceView) findViewById(R.id.map_view);
        
        mRobotAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mRobotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initially set
        mRobotAdapter.add("none");

        mRobotSelect.setAdapter(mRobotAdapter);
        mRobotSelect.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(final AdapterView < ? > parent,
                    final View v, final int position, final long id) {
                String s = (String) parent.getItemAtPosition(position);
                if (s.equals("none")) {
                    map.setSelectedRobot(null);
                } else {
                    UUID uuid = UUID.fromString(s);
                    map.setSelectedRobot(uuid);
                }

            }

            public void onNothingSelected(final AdapterView < ? > parent) { }

        });
        
        setSpinner();
    }
    
    public void onResume() {
    	super.onResume();
    	Controller.getInstance().getEnv().addObserver(this);
    }
    
    public void onPause() {
    	super.onPause();
    	Controller.getInstance().getEnv().deleteObserver(this);
    }

    /**
     * Sets the menu of the Activity.
     *
     * @param menu Reference on menu that has to be set.
     * @return Returns true when the correct menu is displayed.
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MapMode mode = (MapMode) getIntent().
                       getSerializableExtra(MapMode.class.toString());

        switch(mode) {
        case EXPLORATION: inflater.inflate(R.menu.map_exp_menu, menu); break;
        case SIMULATION: inflater.inflate(R.menu.map_sim_menu, menu); break;
        case IMPORT: break;
        default: break;
        }

        return true;
    }

    /**
     * Handles selections of menu items.
     *
     * @param item The menu item that has been selected.
     * @return Returns true when the correct intent is started.
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
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
    
    public void setSpinner() {
    	MapSurfaceView map = (MapSurfaceView) findViewById(R.id.map_view);
    	map.setSpinner(mRobotSelect);
    }

	public void update(Observable obs, Object data) {
		ConquestUpdate cu = (ConquestUpdate) data;
		MapSurfaceView draw = (MapSurfaceView) findViewById(R.id.map_view);
		
		Set<UUID> id = cu.getRobotStatus().keySet();
		mPositions.clear();
		mRobotAdapter.clear();
		mRobotAdapter.add("none");
		for (UUID key : id) {
			int[] position = cu.getRobotStatus().get(key).getPosition();
			mPositions.add(new EpuckPosition(position[0], position[1], key));
			mRobotAdapter.add(key.toString());
		}
		
		draw.setMap(cu.getMapList(), cu.getBorders());
		draw.setRobotPosition(mPositions);

	}

}
