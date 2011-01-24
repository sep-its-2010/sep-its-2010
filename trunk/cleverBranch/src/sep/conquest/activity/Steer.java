package sep.conquest.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Allows user to control an e-puck manually via On-Screen-Joystick or the
 * acceleration sensor of the smartphone.
 * 
 * @author Andreas Poxrucker
 * 
 */
public final class Steer extends Activity implements Observer {

  /**
   * Used to identify update message for the handler.
   */
  private static final int UPDATE_MESSAGE = 0;

  /**
   * No robot is selected.
   */
  private static final int NO_SELECTION = -1;

  /**
   * Shows selected control method.
   */
  private Spinner spControl;

  /**
   * Shows selected robot to control.
   */
  private Spinner spRobots;

  /**
   * Used to display robot names.
   */
  private ArrayAdapter<String> adpRobots;

  /**
   * Used to get 'up' commands.
   */
  private ImageButton btnUp;

  /**
   * Used to get 'down' commands.
   */
  private ImageButton btnDown;

  /**
   * Used to get 'left' commands.
   */
  private ImageButton btnLeft;

  /**
   * Used to get 'right' commands.
   */
  private ImageButton btnRight;

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
   * The currently selected control.
   */
  private Control selectedControl;

  /**
   * The currently selected Robot.
   */
  private int selectedRobot;

  /**
   * The ids of the robots..
   */
  private ArrayList<UUID> robots;

  /**
   * The moving state of the robots.
   */
  private java.util.Map<UUID, Boolean> moving;

  /**
   * The names of the robots.
   */
  private java.util.Map<UUID, String> names;

  /**
   * Used to update the View from update-method.
   */
  private Handler updateHandler;

  /**
   * Called when Activity is initially created.
   * 
   * Initializes the layout, the sensor control and the control elements of the
   * Activity.
   * 
   * @param savedInstanceState
   *          Bundle of state information, saved when Activity was executed
   *          before.
   */
  public void onCreate(Bundle savedInstanceState) {
    // Call constructor of super class.
    super.onCreate(savedInstanceState);

    // Set layout of Activity.
    setContentView(R.layout.steer_main);

    // Set up SensorManager and acceleration sensor.
    initializeSensorControl();

    // Get references on control elements and set EventListener.
    initializeControlElements();

    // Initially selected control is joystick.
    selectedControl = Control.JOYSTICK;

    // And no robot is selected.
    selectedRobot = NO_SELECTION;

    // Initially there are no robots registered.
    robots = new ArrayList<UUID>();

    // Consequently there is no moving state.
    moving = new TreeMap<UUID, Boolean>();

    // And no names.
    names = new TreeMap<UUID, String>();

    // Initialize message handler to deal with update messages.
    updateHandler = new Handler() {

      public void handleMessage(Message msg) {
        if (msg.what == UPDATE_MESSAGE) {
          adpRobots.clear();

          CheckBox chkActivate = (CheckBox) findViewById(R.id.chkActivate);

          if (robots.size() == 0) {
            selectedRobot = NO_SELECTION;
            chkActivate.setEnabled(false);
          } else {
            for (UUID id : robots) {
              adpRobots.add(names.get(id));
            }
            chkActivate.setEnabled(true);
          }
        }
      }
    };
  }

  /**
   * Called when Activity comes back to foreground. Registers Activity at the
   * Environment and reactivates sensor control (if last selected).
   */
  @Override
  public void onResume() {
    super.onResume();
    Controller.getInstance().getEnv().addObserver(this);

    // If sensor control is selected, register sensor listener.
    if (selectedControl == Control.ACC_SENSOR) {
      sensMan.registerListener(accListener, accSensor,
          SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  /**
   * Called when Activity goes to background. Unregisters Activity at the
   * Environment and unregisters the SensorEvent Listener.
   */
  @Override
  public void onPause() {
    Controller.getInstance().getEnv().deleteObserver(this);

    // If there is an active sensor listener (sensor control is enabled),
    // unregister it.
    if (selectedControl == Control.ACC_SENSOR) {
      sensMan.unregisterListener(accListener);
    }
    super.onPause();
  }

  @Override
  public void onDestroy() {
    if (selectedRobot != NO_SELECTION) {
      Controller.getInstance().setControlled(robots.get(selectedRobot), false);
    }
    
    super.onDestroy();
  }

  /**
   * Called by an observed object when its state has changed to notify the
   * Activity.
   * 
   * @param observable
   *          The observed object which is notifying.
   * @param data
   *          Attached data containing information about changes.
   */
  public synchronized void update(Observable observable, Object data) {
    // Cast data object.
    ConquestUpdate update = (ConquestUpdate) data;

    // Get states of robots from update container.
    // Container always contains all robots, even those that have entered
    // error state.
    java.util.Map<UUID, RobotStatus> states = update.getRobotStatus();

    synchronized (states) {
      // Iterate over all RobotStates of the update container and check them.
      for (UUID id : states.keySet()) {
        RobotStatus status = states.get(id);

        if (!robots.contains(id)) {
          if (status.getState() == State.EXPLORE
              || status.getState() == State.FINISH) {
            // If id is not known and corresponding robot is not in error state,
            // add new id and moving state.
            robots.add(id);
            moving.put(id, Boolean.valueOf(status.isMoving()));
            names.put(id, update.getRobotName(id));
          }
        } else {
          // If id is known but robot has entered error state, remove id.
          if (status.getState() == State.ERROR) {
            robots.remove(id);
            moving.remove(id);
            names.remove(id);
            selectedRobot = NO_SELECTION;
          } else {
            // If id is known and robot has not entered error state, update
            // moving state.
            moving.put(id, Boolean.valueOf(status.isMoving()));
          }
        }
      }
      updateHandler.obtainMessage(UPDATE_MESSAGE).sendToTarget();
    }
  }

  /**
   * Initializes sensor control by getting the required reference on the
   * SensorManager and the acceleration sensor.
   * 
   * If device does not have an acceleration sensor, sensor control is disabled.
   */
  private void initializeSensorControl() {
    sensMan = (SensorManager) getSystemService(SENSOR_SERVICE);

    // Get list of available acceleration sensors of device
    List<Sensor> sensList = sensMan.getSensorList(Sensor.TYPE_ACCELEROMETER);

    // Check, if device has at least one acc sensor. If not, sensor control
    // is not possible.
    if (sensList.size() > 0) {
      // Choose first acceleration sensor if device has several.
      accSensor = sensList.get(0);
      accListener = new SensorControl();
    } else {
      displayMessage(getString(R.string.ERR_MSG_NO_ACC_SENSOR), true);
      sensMan = null;
    }
  }

  /**
   * Initializes control elements of the Activity and sets EventListener.
   */
  private void initializeControlElements() {
    // Set listener for CheckBox that enables/disables manual control.
    final CheckBox chkActivate = (CheckBox) findViewById(R.id.chkActivate);
    chkActivate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // If a robot is currently selected, set its controlled state and enable
        // currently selected control.
        if (selectedRobot != NO_SELECTION) {
          Controller.getInstance().setControlled(robots.get(selectedRobot),
              isChecked);

          // Enable/Disable currently selected control.
          enableSelectedControl(isChecked);

          // Enable/Disable control elements.
          enableControlElements(isChecked);
        }
      }
    });
    // Create listener to react to user clicks on control buttons.
    OnClickListener btnListener = new JoystickControl();

    // Get references on control buttons and set OnClickListener.
    btnUp = (ImageButton) findViewById(R.id.btn_up);
    btnUp.setOnClickListener(btnListener);
    btnDown = (ImageButton) findViewById(R.id.btn_down);
    btnDown.setOnClickListener(btnListener);
    btnLeft = (ImageButton) findViewById(R.id.btn_left);
    btnLeft.setOnClickListener(btnListener);
    btnRight = (ImageButton) findViewById(R.id.btn_right);
    btnRight.setOnClickListener(btnListener);
    // Get reference on robot selection Spinner and set its listener.
    spRobots = (Spinner) findViewById(R.id.spRobots);

    // Saves the available robots and is used to display them in the robot
    // spinner.
    adpRobots = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item);
    adpRobots
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spRobots.setAdapter(adpRobots);
    spRobots.setOnItemSelectedListener(new OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        // If same item is selected again then there is nothing to do.
        if (selectedRobot != position) {
          // End manual control of previously selected robot.
          if (selectedRobot != NO_SELECTION) {
            Controller.getInstance().setControlled(robots.get(selectedRobot),
                false);
          }
          
          // Set new selected robot.
          selectedRobot = position;

          // Uncheck CheckBox every time a new robot is selected.
          chkActivate.setChecked(false);
          chkActivate.setEnabled(true);
        }

      }

      public void onNothingSelected(AdapterView<?> parent) {
        selectedRobot = NO_SELECTION;
      }
    });

    // Get reference on spinner displaying the controls.
    spControl = (Spinner) findViewById(R.id.spControl);

    // Contains the available controls.
    String[] controls = { getString(R.string.CONTROL_JOYSTICK),
        getString(R.string.CONTROL_ACC_SENSOR) };

    // Saves the available controls and is used to display them in the control
    // spinner.
    ArrayAdapter<String> adpControl = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, controls);
    adpControl
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spControl.setAdapter(adpControl);
    spControl.setOnItemSelectedListener(new OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        switch (position) {
        case 0:
          selectedControl = Control.JOYSTICK;
          break;
        case 1:
          selectedControl = Control.ACC_SENSOR;
          break;
        }
        enableSelectedControl(true);
      }

      public void onNothingSelected(AdapterView<?> arg0) {
        selectedControl = Control.JOYSTICK;
      }
    });
    // Disable all steer control elements in the beginning.
    enableControlElements(false);
  }

  /**
   * Displays a message on top of the Activity.
   * 
   * @param message
   *          The message to display.
   * @param isError
   *          True if message should be displayed as an error message, false
   *          otherwise.
   */
  private void displayMessage(final String message, final boolean isError) {
    // Get new DialogBuilder.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setCancelable(false);

    // If message is an error message set error icon and error title.
    // Otherwise set warning icon and warning title.
    if (isError) {
      builder.setTitle(getString(R.string.ERR_TITLE_ERROR));
      builder.setIcon(R.drawable.err_error);
    } else {
      builder.setTitle(getString(R.string.ERR_TITLE_WARNING));
      builder.setIcon(R.drawable.err_warning);
    }

    // Add button to the dialog.
    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

      public void onClick(final DialogInterface dialog, final int which) {
        dialog.dismiss();
      }
    });
    // Create and show dialog.
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Enables or disables the control elements used to control a robot.
   * 
   * @param enable
   *          True to enable control elements, false otherwise.
   */
  private void enableControlElements(boolean enable) {
    btnUp.setEnabled(enable);
    btnDown.setEnabled(enable);
    btnLeft.setEnabled(enable);
    btnRight.setEnabled(enable);

    // If smartphone does not have an acceleration sensor then control selection
    // spinner is always disabled.
    // Otherwise, state of control selection spinner depends on state of control
    // activate check box.
    TextView txtControl = (TextView) findViewById(R.id.txtControl);
    if (sensMan != null) {
      spControl.setEnabled(enable);
      txtControl.setEnabled(enable);
    } else {
      spControl.setEnabled(false);
      txtControl.setEnabled(false);
    }
  }

  /**
   * Enables or disables the currently selected control.
   * 
   * @param isEnabled
   *          True to enable currently selected control, false otherwise.
   */
  private void enableSelectedControl(boolean isEnabled) {
    // If control should be enabled, determine which type of control is
    // currently selected and enable/disable it.
    // Otherwise disable all control means.
    if (isEnabled) {
      switch (selectedControl) {
      case JOYSTICK:
        // Set Buttons clickable.
        btnUp.setClickable(true);
        btnDown.setClickable(true);
        btnRight.setClickable(true);
        btnLeft.setClickable(true);

        // Remove SensorEventListener.
        sensMan.unregisterListener(accListener);
        break;
      case ACC_SENSOR:
        // Set Buttons not clickable.
        btnUp.setClickable(false);
        btnDown.setClickable(false);
        btnRight.setClickable(false);
        btnLeft.setClickable(false);

        // Register SensorEventListener.
        sensMan.registerListener(accListener, accSensor,
            SensorManager.SENSOR_DELAY_NORMAL);
        break;
      }
    } else {
      // Remove OnClickListener for Buttons.
      btnUp.setClickable(false);
      btnDown.setClickable(false);
      btnRight.setClickable(false);
      btnLeft.setClickable(false);

      // Remove SensorEventListener.
      sensMan.unregisterListener(accListener);
    }
  }

  /**
   * Listens for changing data of the acceleration sensor. Computes the
   * orientation of the smartphone and extracts control commands.
   * 
   * If Listener is active, a valid robot is selected and the robot is not
   * currently moving then command is passed to the Controller.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private final class SensorControl implements SensorEventListener {

    /**
     * Threshold to filter signals that are to large.
     */
    private static final int THRESHOLD_ACC = 10;

    /**
     * Triggers control evaluation.
     */
    private static final float THRESHOLD_X = 2.5f;

    /**
     * Triggers control evaluation.
     */
    private static final float THRESHOLD_Y = 2.5f;

    /**
     * Used as balance for sensor value filter.
     */
    private static final float FILTER_PARAM = 0.6f;

    /**
     * Used to sum up x measure values for the filter.
     */
    private float filterX = 0;

    /**
     * Used to sum up y measure values for the filter.
     */
    private float filterY = 0;

    /**
     * Used to sum up z measure values for the filter.
     */
    private float filterZ = 0;

    /**
     * Method is not implemented.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
      // This method is no implemented
    }

    /**
     * Listens for changing data of the acceleration sensor. Computes the
     * orientation of the smartphone and extracts control commands. If manual
     * control via sensor control is activated, the direction commands are
     * passed to the Controller.
     * 
     * @param event
     *          The sensor event that has happened.
     */
    public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        // If a robot is selected and not moving, pass control command to
        // Controller.
        if ((selectedRobot != NO_SELECTION)
            && !moving.get(robots.get(selectedRobot))) {
          float[] values = event.values;

          // If x or y value is greater than threshold, add values to filter.
          if (Math.sqrt(values[0] * values[0] + values[1] * values[1]
              + values[2] + values[2]) < THRESHOLD_ACC) {
            addFilteredValues(values);
          }

          // If filter variables exceed threshold, get command.
          if (Math.abs(filterX) > THRESHOLD_X
              || Math.abs(filterY) > THRESHOLD_Y) {

            // Get hypotenuse of sum-vector.
            float hyp = (float) Math
                .sqrt(filterX * filterX + filterY * filterY);

            // Compute angle between x-sum and y-sum.
            float acos = (float) Math.acos((float) (filterX / hyp));

            if (acos < Math.PI / 4.0) {
              Controller.getInstance().left(robots.get(selectedRobot));
            } else if (Math.PI / 4.0 < acos && acos <= 3.0 * Math.PI / 4.0
                && filterY < 0) {
              Controller.getInstance().forward(robots.get(selectedRobot));
            } else if (Math.PI / 4.0 < acos && acos <= 3.0 * Math.PI / 4.0
                && filterY >= 0) {
              Controller.getInstance().turn(robots.get(selectedRobot));
            } else if (acos > 3.0 * Math.PI / 4.0) {
              Controller.getInstance().right(robots.get(selectedRobot));
            } else {
              Controller.getInstance().left(robots.get(selectedRobot));
            }
            // Reset filter values.
            filterX = 0;
            filterY = 0;
            filterZ = 0;

            // Set moving state of selected robot.
            moving.put(robots.get(selectedRobot), Boolean.TRUE);
          }
        }
      }
    }

    /**
     * Adds the current measured values to the filter values, weighted with the
     * filter parameter.
     * 
     * @param values
     *          The measure values to add.
     */
    private void addFilteredValues(float[] values) {
      filterX = filterX * FILTER_PARAM + values[0] * (float) (1 - FILTER_PARAM);
      filterY = filterY * FILTER_PARAM + values[1] * (float) (1 - FILTER_PARAM);
      filterZ = filterZ * FILTER_PARAM + values[2] * (float) (1 - FILTER_PARAM);
    }
  }

  /**
   * Handles click events on steer control elements and passes the corresponding
   * drive command to the Controller.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private final class JoystickControl implements OnClickListener {

    /**
     * Called when registered button receives OnClick-Event.
     * 
     * @param view
     *          View that has been clicked.
     */
    public void onClick(View v) {
      // If a robot is selected and not moving, pass control command to
      // Controller.
      if ((selectedRobot != NO_SELECTION)
          && !moving.get(robots.get(selectedRobot))) {
        int id = v.getId();
        switch (id) {
        // Button 'Up'
        case R.id.btn_up:
          Controller.getInstance().forward(robots.get(selectedRobot));
          break;

        // Button 'Down'
        case R.id.btn_down:
          Controller.getInstance().turn(robots.get(selectedRobot));
          break;

        // Button 'Left'
        case R.id.btn_left:
          Controller.getInstance().left(robots.get(selectedRobot));
          break;

        // Button 'Right'
        case R.id.btn_right:
          Controller.getInstance().right(robots.get(selectedRobot));
          break;
        }
        moving.put(robots.get(selectedRobot), Boolean.TRUE);
      }
    }
  }
}