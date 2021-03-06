package sep.conquest.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapFileHandler;
import sep.conquest.model.MapNode;
import sep.conquest.model.Orientation;
import sep.conquest.model.PuckFactory;
import sep.conquest.model.SimConfiguration;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
import android.widget.TextView;

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
	
	/**
	 * Avoids an incorrect selection in the spinner menu.
	 */
	private boolean mFirstRun = false;
	
	/**
	 * This spinner contains the number of robots.
	 */
	private Spinner numberOfRobots;
	
	/**
	 * Saves the values for the spinner.
	 */
	private ArrayAdapter<String> numberAdapter;
	
	/**
	 * Displays the filename of the chosen map.
	 */
	private TextView filename;

	/**
	 * Called when the Activity is initially created.
	 * 
	 * Initializes layout of Activity and its control elements.
	 * 
	 * @param savedInstanceState
	 *            Bundle of state information, saved when Activity was executed
	 *            before.
	 */
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simulation_main);
		
		filename = (TextView) findViewById(R.id.filename);
		
		positions = new LinkedList<EpuckPosition>();
		map = new GridMap();
		
		numberOfRobots = (Spinner) findViewById(R.id.spNumber);
		numberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numberOfRobots.setAdapter(numberAdapter);
		numberOfRobots.setEnabled(false);
		numberOfRobots.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(final AdapterView<?> parent, final View v,
					final int position, final long id) {
				
				mNumber = position;
				if (mFirstRun) {
					setRobotPositions();
					drawPreview(map.getMapAsList(), map.getMapBorders());
				}
							
			}

			public void onNothingSelected(final AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public final void onResume() {
		super.onResume();
		drawPreview(map.getMapAsList(), map.getMapBorders());
		
		Controller.getInstance().destroy();
	}
	
	public final void onPause() {
		super.onPause();
	}

	/**
	 * Sets the menu of the Activity.
	 * 
	 * @param menu
	 *            Reference on menu that has to be set.
	 */
	public final boolean onCreateOptionsMenu(final Menu menu) {
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
	public final boolean onOptionsItemSelected(final MenuItem item) {

		Intent start = new Intent();

		switch (item.getItemId()) {
		case R.id.mnuStart:
			if (positions.size() < 1) {
				displayMessage(getString(R.string.ERR_NO_MAP_SELECTED), false);
				return false;
			}
			int[][] pos = new int[mNumber+1][2];
			Orientation[] orientation = new Orientation[mNumber + 1];
			for (int i = 0; i < mNumber+1; i++) {
				pos[i][0] = robotPositions[i][0];
				pos[i][1] = robotPositions[i][1];
				orientation[i] = ori[i];
			}
			
			start.setComponent(new ComponentName(getApplicationContext()
					.getPackageName(), sep.conquest.activity.Map.class.getName()));
			PuckFactory.createVirtualPucks(map, pos, orientation);
			start.putExtra(MapMode.class.toString(), MapMode.SIMULATION);
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
	private void drawPreview(final List<MapNode> map, final int[] borders) {
		MapSurfaceView preview = (MapSurfaceView) findViewById(R.id.im_preview);
		
		preview.setMode(MapMode.PREVIEW);
		preview.setMap(map, borders);
		preview.setRobotPosition(positions);
	}
	
	/**
	 * Gets the file path and file name as result of the import intent.
	 */
	protected final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_SIM_CONFIG) {
			if (resultCode == RESULT_OK) {
				
				String mapName = data.getStringExtra(Import.EXTRA_FILE_PATH);
				try {
					SimConfiguration container = MapFileHandler.openConfiguration(mapName);
					map = container.getMap();
					robotPositions = container.getPositions();
					ori = container.getOrientations();
				} catch (FileNotFoundException e) {
					displayMessage(getString(R.string.ERR_MSG_FILE_NOT_FOUND), true);
					return;
				} catch (IOException e) {
					displayMessage(getString(R.string.ERR_MSG_INVALID_FILE), true);
					return;
				}
				numberOfRobots.setEnabled(true);
				mNumber = 0;
				numberAdapter.clear();
				for (int i = 0; i < robotPositions.length; i++) {
					numberAdapter.add("" + (i+1));
				}
				setRobotPositions();
				numberOfRobots.setSelection(0);
				drawPreview(map.getMapAsList(), map.getMapBorders());
				mFirstRun = true;
				
				filename.setText("" + mapName);
			}
		}
	}
	
	/**
	 * Updates the robot positions in the map if a new number of robots is 
	 * chosen.
	 */
	private void setRobotPositions() {
		positions.clear();
		for (int j = 0; j < mNumber+1; j++) {
			int x = robotPositions[j][0];
			int y = robotPositions[j][1];
			Orientation o = ori[j]; 
			positions.add(new EpuckPosition(x, y, "", o));
		}
	}
	
	  /**
	   * Displays a message on top of the Activity.
	   * 
	   * @param message
	   *          The message to display.
	   */
	  private void displayMessage(final String message, final boolean isError) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(message);
	    builder.setCancelable(false);
	    
	    if (isError) {
	    	builder.setTitle(getString(R.string.ERR_TITLE_ERROR));
	    	builder.setIcon(R.drawable.err_error);
	    } else {
	    	builder.setTitle(getString(R.string.ERR_TITLE_WARNING));
	    	builder.setIcon(R.drawable.err_warning);
	    }
	    
	    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	      
	      public void onClick(final DialogInterface dialog, final int which) {
	        dialog.dismiss();
	        
	      }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	  }
	
}
