package sep.conquest.activity;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.RobotStatus;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Allows user to control an e-puck manually via On-Screen-Joystick or the
 * acceleration sensor of the smartphone.
 * 
 * @author Andreas Poxrucker
 * 
 */
public class Steer extends Activity implements Observer {

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
   * The currently selected control.
   */
  private Control selectedControl;

  /**
   * The currently selected Robot.
   */
  private UUID selectedRobot;

  /**
   * Mapping of names from spinner to ids of robots.
   */
  private UUID[] robots;

  /**
   * Indicates that drive command has been send to selected robot.
   */
  private boolean moving;

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

    // Initially selected control is joystick
    selectedControl = Control.JOYSTICK;
  }

  public void onResume() {
    super.onResume();
    controller.getEnv().addObserver(this);

    // If sensor control is selected, register sensor listener.
    if (selectedControl == Control.ACC_SENSOR) {
      sensMan.registerListener(accListener, accSensor,
          SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  public void onPause() {
    controller.getEnv().deleteObserver(this);

    // If there is an active sensor listener (sensor control is enabled),
    // unregister it.
    if (selectedControl == Control.ACC_SENSOR) {
      sensMan.unregisterListener(accListener);
    }
    super.onPause();
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
  public void update(Observable observable, Object data) {
    // Cast data object.
    ConquestUpdate update = (ConquestUpdate) data;

    // Get RobotStates and the keyset.
    java.util.Map<UUID, RobotStatus> states = update.getRobotStatus();
    Set<UUID> ids = states.keySet();

    robots = new UUID[ids.size()];

    ArrayAdapter<String> adpRobots = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item);
    adpRobots
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    int i = 0;
    for (UUID id : ids) {
      String name = states.get(id).getState().toString();
      adpRobots.add(name);
      robots[i] = id;
      i++;
    }
    adpRobots.add("none");
    spRobots.setAdapter(adpRobots);
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
    CheckBox chkActivate = (CheckBox) findViewById(R.id.chkActivate);
    chkActivate.setOnCheckedChangeListener(new SteerOnCheckedChangeListener());

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
    spRobots = (Spinner) findViewById(R.id.spRobots);
    spRobots.setOnItemSelectedListener(new OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        TextView item = (TextView) view;

        if (item.getText().equals("none")) {
          selectedRobot = null;
        } else {
          selectedRobot = robots[position];
        }

      }

      public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

      }

    });

    // Contains the available controls.
    String[] controls = { "On-Screen-Joystick", "Acceleration sensor" };

    // Saves the available controls and is used to display them in the control
    // spinner.
    ArrayAdapter<String> adpControl = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, controls);
    adpControl
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spControl = (Spinner) findViewById(R.id.spControl);
    spControl.setAdapter(adpControl);
    spControl.setOnItemSelectedListener(new OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        // The position of the selected item is the ordinal of the corresponding
        // Control enum.
        selectedControl = Control.values()[position];
      }

      public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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
   */
  private void displayMessage(String message) {
    Toast mtoast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
    mtoast.setGravity(Gravity.CENTER, 0, 0);
    mtoast.show();
  }

  private void enableControlElements(boolean enable) {
    btnUp.setEnabled(enable);
    btnDown.setEnabled(enable);
    btnLeft.setEnabled(enable);
    btnRight.setEnabled(enable);

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
   * Listens for changing data of the acceleration sensor. Computes the
   * orientation of the smartphone and extracts control commands. If manual
   * control via sensor control is activated, the direction commands are passed
   * to the Controller.
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
     * orientation of the smartphone and extracts control commands. If manual
     * control via sensor control is activated, the direction commands are
     * passed to the Controller.
     * 
     * @param event
     *          The sensor event that has happened.
     */
    public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        float x = event.values[0];
        float y = event.values[1];
        float hyp = (float) Math.sqrt(x * x + y * y);
        float acos = (float) Math.acos((float) x / hyp);

        if (Math.abs(x) > 3 || Math.abs(y) > 2) {
          if (acos < Math.PI / 4.0) {

          } else if (Math.PI / 4.0 < acos && acos <= 3.0 * Math.PI / 4.0
              && y < 0) {
            // Action
          } else if (Math.PI / 4.0 < acos && acos <= 3.0 * Math.PI / 4.0
              && y >= 0) {
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
     *          View whose checked state has changed.
     * @param isChecked
     *          True, if buttonView is checked, false otherwise.
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      enableControlElements(isChecked);

      if (isChecked) {

      } else {

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
     *          View that has been clicked.
     */
    public void onClick(View v) {
      // If a robot is selected and not moving, pass control command to
      // Controller.
      if ((selectedRobot != null) && !moving) {
        int id = v.getId();
        switch (id) {
        // Button 'Up'
        case R.id.btn_up:
          controller.forward(selectedRobot);
          break;

        // Button 'Down'
        case R.id.btn_down:
          controller.turn(selectedRobot);
          break;

        // Button 'Left'
        case R.id.btn_left:
          controller.left(selectedRobot);
          break;

        // Button 'Right'
        case R.id.btn_right:
          controller.right(selectedRobot);
          break;
        }
        moving = true;
      }
    }
  }
}