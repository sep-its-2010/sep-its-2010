package sep.conquest.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapNode;
import sep.conquest.model.NodeType;
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
	
	private Spinner ePuckSelect;
	
	private ArrayAdapter < String > robotAdapter;
	
	private List < MapNode > mmap;
	
	private LinkedList < EpuckPosition > positions;

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

        mmap = new LinkedList < MapNode >();
        positions = new LinkedList < EpuckPosition >();
        
        ePuckSelect = (Spinner) findViewById(R.id.epuck_selector);
        final MapSurfaceView map = (MapSurfaceView) findViewById(R.id.map_view);
        
        robotAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initially set
        robotAdapter.add("none");

        ePuckSelect.setAdapter(robotAdapter);
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
        
        setSpinner();
        //test();
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
    	map.setSpinner(ePuckSelect);
    }

	public void update(Observable obs, Object data) {
		// TODO Auto-generated method stub
		//setMap
		//setEpuckpositions
		//check ArrayAdapter
		ConquestUpdate cu = (ConquestUpdate) data;
		
		mmap = cu.getMapList();
		MapSurfaceView draw = (MapSurfaceView) findViewById(R.id.map_view);
		int[] borders = cu.getBorders();
		draw.setMap(mmap, borders);
		//draw.setMap(list, array);
		//draw.setRobotPosition(list);
		
		//robotAdapter...
		
	}
	
	public void test() {
		
		GridMap map = new GridMap();
		
		NodeType type = NodeType.CROSS;
        int x = 1;
        int y = 1;
       
        type = NodeType.CROSS;
        x = 0;
        y = 0;
        map.addNode(x, y, type);
        
        type = NodeType.TOPT;
        x = 1;
        y = 0;
        map.addNode(x, y, type);
        
        type = NodeType.TOPT;
        x = 2;
        y = 0;
        map.addNode(x, y, type);
       
        type = NodeType.TOPRIGHTEDGE;
        x = 3;
        y = 0;
        map.addNode(x, y, type);
        
        type = NodeType.LEFTT;
        x = 0;
        y = 1;
        map.addNode(x, y, type);
        
        type = NodeType.CROSS;
        x = 0;
        y = 2;
        map.addNode(x, y, type);
        
        type = NodeType.BOTTOMT;
        x = 1;
        y = 2;
        map.addNode(x, y, type);
        
        type = NodeType.CROSS;
        x = -1;
        y = 2;
        map.addNode(x, y, type);
        
        type = NodeType.CROSS;
        x = -2;
        y = 2;
        map.addNode(x, y, type);
        
        type = NodeType.CROSS;
        x = 0;
        y = -1;
        map.addNode(x, y, type);
        
        mmap = map.getMapAsList();
        int[] borders = map.getMapBorders();
        
        MapSurfaceView v = (MapSurfaceView) findViewById(R.id.map_view);
        
        positions.add(new EpuckPosition(3, 2, 1229));
        robotAdapter.add("1229");
        
        v.setMap(mmap, borders);
        v.setRobotPosition(positions);
	}

}
