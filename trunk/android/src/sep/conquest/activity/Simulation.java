package sep.conquest.activity;

import java.util.LinkedList;
import java.util.Map;

import sep.conquest.R;
import sep.conquest.model.GridMap;
import sep.conquest.model.NodeType;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
		
		Spinner numberOfRobots = (Spinner) findViewById(R.id.spNumber);
		ArrayAdapter numberAdapter = ArrayAdapter.createFromResource(this, R.array.numberRobots, android.R.layout.simple_spinner_item);
		numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numberOfRobots.setAdapter(numberAdapter);
		
		
		test();
		//getIntent().getSerializableExtra(name);
		
	}
	
	public void onResume() {
		super.onResume();
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
		case R.id.mnuStart:
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Map.class.getName()));
			startActivity(start);
			break;
		case R.id.mnuOpen:
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
		MapSurfaceView preview = (MapSurfaceView) findViewById(R.id.im_preview);
		//preview.setMap(list, array);
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
        
        type = NodeType.CROSS;
        x = 4;
        y = 0;
        map.addNode(x, y, type);
        
        type = NodeType.CROSS;
        x = 5;
        y = 0;
        map.addNode(x, y, type);
        
         type = NodeType.CROSS;
         x = 1;
         y = 1;
       
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
        
        
        
        MapSurfaceView v = (MapSurfaceView) findViewById(R.id.im_preview);
        
        v.setMap(map.getMapAsList(), map.getMapBorders());
	}

}
