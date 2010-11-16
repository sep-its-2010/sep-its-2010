package sep.conquest.activity;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Allows user to control an e-puck manually via On-Screen-Joystick or
 * the acceleration sensor of the smartphone.
 * 
 * @author Andreas Poxrucker
 *
 */
public class Steer extends Activity implements Observer {

	/**
	 * Marks whether manual control is activated or not.
	 */
	private CheckBox chkActivate;

	/**
	 * Shows selected control method.
	 */
	private Spinner spControl;

	/**
	 * Shows selected robot to control.
	 */
	private Spinner spRobots;

	/**
	 * Used to get 'up' commands.
	 */
	private Button btnUp;
	
	/**
	 * Used to get 'down' commands.
	 */
	private Button btnDown;
	
	/**
	 * Used to get 'left' commands.
	 */
	private Button btnLeft;
	
	/**
	 * Used to get 'right' commands.
	 */
	private Button btnRight;

	/**
	 * Makes sensors and their data available.
	 */
	private SensorManager sensMan;

	/**
	 * The acceleration sensor used for controlling.
	 */
	private Sensor accSensor;

	/**
	 * SensorEventListener to react to changes of acceleration.
	 */
	private SensorEventListener accListener;
	
	/**
	 * Interface between model (Environment) and the Activity.
	 */
	private Controller controller;

	/**
	 * Called when Activity is initially created.
	 * 
	 * Initializes the layout, the sensor control and the control elements of
	 * the Activity.
	 * 
	 * @param savedInstanceState
	 *            Bundle of state information, saved when Activity was executed
	 *            before.
	 */
	public void onCreate(Bundle savedInstanceState) {

		// Call constructor of super class
		super.onCreate(savedInstanceState);

		// Set layout of Activity
		setContentView(R.layout.steer_main);

		// Set up SensorManager and acceleration sensor
		initializeSensorControl();

		// Get references on control elements and set EventListener
		initializeControlElements();
		
		// Get reference on Controller
		controller = Controller.getInstance();
	}

	/**
	 * Sets the menu of the Activity.
	 * 
	 * @param menu
	 *            Reference on menu that has to be set.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.steer_menu, menu);
		return true;
	}

	/**
	 * Handles selections of the menu of the Activity.
	 * 
	 * @param item
	 *            The MenuItem that has been selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	/**
	 * Called by an observed object when its state has changed to notify the
	 * Activity.
	 * 
	 * @param observable
	 *            The observed object which is notifying.
	 * @param data
	 *            Attached data containing information about changes.
	 */
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}

	/**
	 * Initializes sensor control by getting the required reference on the
	 * SensorManager and the acceleration sensor.
	 * 
	 * If device does not have an acceleration sensor, sensor control is
	 * disabled.
	 */
	private void initializeSensorControl() {
		sensMan = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get list of available acceleration sensors of device
		List<Sensor> sensList = sensMan
				.getSensorList(Sensor.TYPE_ACCELEROMETER);

		// Check, if device has at least one acc sensor. If not, sensor control
		// is
		// not possible.
		if (sensList.size() > 0) {
			accSensor = sensList.get(0);
			accListener = new SteerSensorEventListener();
		} else {
			displayMessage("Your device does not have an acceleration sensor.");
			sensMan = null;
		}
	}

	/**
	 * Initializes control elements of the Activity and sets EventListener.
	 */
	private void initializeControlElements() {

		// Get reference on CheckBox and set its OnCheckedChangeListener
		chkActivate = (CheckBox) findViewById(R.id.chk_activate);
		chkActivate
				.setOnCheckedChangeListener(new SteerOnCheckedChangeListener());

		// Get references on Buttons and set their OnClickListener
		OnClickListener listener = new SteerOnClickListener();
		btnUp = (Button) findViewById(R.id.btn_up);
		btnUp.setOnClickListener(listener);
		btnDown = (Button) findViewById(R.id.btn_down);
		btnDown.setOnClickListener(listener);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnLeft.setOnClickListener(listener);
		btnRight = (Button) findViewById(R.id.btn_right);
		btnRight.setOnClickListener(listener);

		// Get reference on robot selection Spinner and set its
		spRobots = (Spinner) findViewById(R.id.sp_robots);

		// Get reference on control selection Spinner and set its
		spControl = (Spinner) findViewById(R.id.sp_control);
	}

	/**
	 * Displays a message on top of the Activity.
	 * 
	 * @param message
	 *            The message to display.
	 */
	private void displayMessage(String message) {
		Toast mtoast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		mtoast.setGravity(Gravity.CENTER, 0, 0);
		mtoast.show();
	}

	/**
	 * Listens for changing data of the acceleration sensor. Computes the
	 * orientation of the smartphone and extracts control commands. If manual
	 * control via sensor control is activated, the direction commands are
	 * passed to the Controller.
	 * 
	 * @author Andreas Poxrucker
	 * 
	 */
	private final class SteerSensorEventListener implements SensorEventListener {

		/**
		 * Method is not implemented.
		 */
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// This method is no implemented
		}

		/**
		 * Listens for changing data of the acceleration sensor. Computes the
		 * orientation of the smartphone and extracts control commands. If
		 * manual control via sensor control is activated, the direction
		 * commands are passed to the Controller.
		 * 
		 * @param event
		 *            The sensor event that has happened.
		 */
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				float x = event.values[0];
				float y = event.values[1];
				float hyp = (float) Math.sqrt(x * x + y * y);
				float acos = (float) Math.acos((float) x / hyp);

				if (Math.abs(x) > 3 || Math.abs(y) > 2) {
					if (acos < Math.PI / 4.0) {
						// Action
					} else if (Math.PI / 4.0 < acos
							&& acos <= 3.0 * Math.PI / 4.0 && y < 0) {
						// Action
					} else if (Math.PI / 4.0 < acos
							&& acos <= 3.0 * Math.PI / 4.0 && y >= 0) {
						// Action
					} else if (acos > 3.0 * Math.PI / 4.0) {
						// Action
					}
				}
			}
		}
	}

	/**
	 * Handles CheckChangedEvents of control elements of the Activity.
	 * 
	 * @author Andreas Poxrucker
	 *
	 */
	private final class SteerOnCheckedChangeListener implements
			OnCheckedChangeListener {

		/**
		 * Called when registered checkbox receives OnCheckedChanged-Event.
		 * 
		 * @param buttonview
		 *            View whose checked state has changed.
		 * @param isChecked
		 *            True, if buttonView is checked, false otherwise.
		 */
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			// If control is activated, set Buttons enabled
			btnUp.setEnabled(isChecked);
			btnDown.setEnabled(isChecked);
			btnLeft.setEnabled(isChecked);
			btnRight.setEnabled(isChecked);

			// If control is activated and on-screen joystick control is
			// selected
			// make Buttons clickable.
			btnUp.setClickable(isChecked);
			btnDown.setClickable(isChecked);
			btnLeft.setClickable(isChecked);
			btnRight.setClickable(isChecked);

			if (isChecked) {
				sensMan.registerListener(accListener, accSensor,
						SensorManager.SENSOR_DELAY_UI);
			} else {
				sensMan.unregisterListener(accListener);
			}
		}
	}

	/**
	 * Handles click events on control elements of the Activity.
	 * 
	 * @author Andreas Poxrucker
	 * 
	 */
	private final class SteerOnClickListener implements OnClickListener {

		/**
		 * Called when registered button receives OnClick-Event.
		 * 
		 * @param view
		 *            View that has been clicked.
		 */
		public void onClick(View v) {
			int id = v.getId();

			switch (id) {
			case R.id.btn_up:
				break;
			case R.id.btn_down:		
				break;
			case R.id.btn_left:
				break;
			case R.id.btn_right:
				break;
			}
		}
	}
}
