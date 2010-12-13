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
import sep.conquest.model.Orientation;
import sep.conquest.model.PuckFactory;
import sep.conquest.model.SimConfiguration;
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
 * Allows user to open a map from the file system and specify the number of
 * participating virtual robots.
 * The application then runs in simulation mode.
 * 
 * @author Florian Buerchner
 * 
 */
public class Simulation extends Activity {
	
	/**
	 * Request Code to identify intent data.
	 */
	private static final int REQUEST_SIM_CONFIG = 10;
	
	/**
	 * GridMap is created after a file is imported.
	 */
	GridMap map;
	
	/**
	 * Saves the robot positions. They are needed for the simulator.
	 */
	int[][] robotPositions;
	
	/**
	 * Saves the orientation of the robots. Also needed for simulator.
	 */
	Orientation[] ori;
	
	/**
	 * Saves the e-puck positions to draw the preview.
	 */
	private LinkedList <EpuckPosition> positions;
	
	/**
	 * Saves the chosen position in the spinner.
	 */
	private int mNumber;
	
	private boolean mFirstRun = false;

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
		map = new GridMap();
		
		Spinner numberOfRobots = (Spinner) findViewById(R.id.spNumber);
		ArrayAdapter<?> numberAdapter = ArrayAdapter.createFromResource(this, R.array.numberRobots, android.R.layout.simple_spinner_item);
		numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numberOfRobots.setAdapter(numberAdapter);
		numberOfRobots.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				
				mNumber = position;
				if (mFirstRun) {
					setRobotPositions();
					drawPreview(map.getMapAsList(), map.getMapBorders());
				}
							
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
	}
	
	/**
	 * Gets the file path and file name as result of the import intent.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIM_CONFIG) {
			if (resultCode == RESULT_OK) {
				
				String mapName = data.getStringExtra(Import.EXTRA_FILE_PATH);
				try {
					SimConfiguration container = MapFileHandler.openConfiguration(mapName);
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
				
				setRobotPositions();
				drawPreview(map.getMapAsList(), map.getMapBorders());
				mFirstRun = true;
			}
		}
	}
	
	private void setRobotPositions() {
		positions.clear();
		for (int j = 0; j < mNumber+1; j++) {
			int x = robotPositions[j][0];
			int y = robotPositions[j][1];
			positions.add(new EpuckPosition(x, y, "0"));
		}
	}
	
}
