package sep.conquest.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sep.conquest.R;
import sep.conquest.model.GridMap;
import sep.conquest.model.ImportContainer;
import sep.conquest.model.MapFileHandler;
import sep.conquest.model.MapNode;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.PuckFactory;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
	
	private static final int REQUEST_SIM_CONFIG = 10;
	
	GridMap map = new GridMap();
	
	int[][] robotPositions;
	
	Orientation[] ori;
	
	private LinkedList <EpuckPosition> positions;
	
	private int mNumber;

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
		
		positions = new LinkedList<EpuckPosition>();
		
		Spinner numberOfRobots = (Spinner) findViewById(R.id.spNumber);
		ArrayAdapter<?> numberAdapter = ArrayAdapter.createFromResource(this, R.array.numberRobots, android.R.layout.simple_spinner_item);
		numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numberOfRobots.setAdapter(numberAdapter);
		numberOfRobots.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				
				mNumber = position;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		//test();
		
	}
	
	public void onResume() {
		super.onResume();
	}
	
	public void onPause() {
		super.onPause();
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
					.getPackageName(), sep.conquest.activity.Map.class.getName()));
			PuckFactory.createVirtualPucks(map, robotPositions, ori);
			startActivity(start);
			break;
		case R.id.mnuOpen:
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), Import.class.getName()));
			start.putExtra(ImportMode.class.toString(), ImportMode.SIMULATION_MAP);
			startActivityForResult(start, REQUEST_SIM_CONFIG);
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
	public void drawPreview(List<MapNode> map, int[] borders) {
		MapSurfaceView preview = (MapSurfaceView) findViewById(R.id.im_preview);
		preview.setMap(map, borders);
		preview.setRobotPosition(positions);
		preview.isDrawing(true);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIM_CONFIG) {
			if (resultCode == RESULT_OK) {
				
				String mapName = data.getStringExtra(Import.EXTRA_FILE_PATH);
				int i = 0;
				try {
					ImportContainer container = MapFileHandler.openMap(mapName);
					map = container.getMap();
					robotPositions = container.getPositions();
					ori = container.getOrientations();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (int j = 0; j < robotPositions.length; j++) {
					int x = robotPositions[j][0];
					int y = robotPositions[j][1];
					positions.add(new EpuckPosition(x, y, null));
				}
				drawPreview(map.getMapAsList(), map.getMapBorders());
			}
		}
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
        v.isDrawing(true);
	}

}
