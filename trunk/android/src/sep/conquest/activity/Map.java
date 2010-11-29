package sep.conquest.activity;

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
import android.widget.Spinner;


/**
 * The Map class extends from Activity and is responsible for the representation
 * of the map and the selection of e-pucks.
 *
 * @author Florian Buerchner
 *
 */
public class Map extends Activity {
	
	private Spinner ePuckSelect;

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

        ePuckSelect = (Spinner) findViewById(R.id.epuck_selector);
        final MapSurfaceView map = (MapSurfaceView) findViewById(R.id.map_view);

        ePuckSelect.setAdapter(map.robotAdapter);
        ePuckSelect.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(final AdapterView < ? > parent,
                    final View v, final int position, final long id) {
                String s = (String) parent.getItemAtPosition(position);
                if (s.equals("none")) {
                    map.setSelectedRobot(-1);
                } else {
                    int i = Integer.parseInt(s);
                    map.setSelectedRobot(i);
                }

            }

            public void onNothingSelected(final AdapterView < ? > parent) { }

        });
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

}
