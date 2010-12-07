package sep.conquest.activity;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import sep.conquest.R;
import sep.conquest.model.PuckFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Searches for available e-puck robots and lists them. The user can select
 * robots and connect to them via Bluetooth. The application then runs in
 * exploration mode.
 * 
 * @author Andreas Poxrucker
 * 
 */
public final class Connect extends Activity {

  /**
   * Used to identify message to enable Bluetooth.
   */
  private final int REQUEST_ENABLE_BLUETOOTH = 0;

  /**
   * Identifies messages that indicate, that connecting has been successful.
   */
  private static final String CONNECTION_SUCCESSFUL = "ConnectionSuccessful";

  /**
   * Identifies messages that indicate, that opening a certain connection has
   * failed.
   */
  private static final String CONNECTION_ERROR = "ConnectionError";

  /**
   * Reference on BluetoothAdapter of the device.
   */
  private BluetoothAdapter bluetoothAdapter;

  /**
   * Receives and handles broadcast messages.
   */
  private BluetoothBroadcastReceiver bcReceiver;

  /**
   * Specifies the types of messages the bcReceiver should handle.
   */
  private IntentFilter messageFilter;

  /**
   * Saves Bluetooth devices found by the BluetoothAdapter. The key is the
   * Bluetooth name of the device.
   */
  private java.util.Map<String, BluetoothDevice> discoveredRobots;

  /**
   * Saves robots selected to open a Bluetooth connection. The key is the
   * Bluetooth name of the device.
   */
  private java.util.Map<String, BluetoothDevice> selectedRobots;

  /**
   * Used to display names of discovered devices in the ListView.
   */
  private ArrayAdapter<String> robotList;

  /**
   * Starts new search for available Bluetooth devices.
   */
  private Button btnSearch;

  /**
   * Displays the discovered devices.
   */
  private ListView lsRobots;

  /**
   * Indeterminate progress dialog shown during Bluetooth search and connect
   * action
   */
  private ProgressDialog pDialog;

  /**
   * Called when Activity is initially created.
   * 
   * Initializes layout of Activity, its control elements, the BroadcastReceiver
   * and the Bluetooth-Adapter of the device.
   * 
   * @param savedInstanceState
   *          Bundle of state information, saved when Activity was executed
   *          before.
   */
  public void onCreate(Bundle savedInstanceState) {
    // Call constructor of super class
    super.onCreate(savedInstanceState);

    // Set layout of Activity
    setContentView(R.layout.connect_main);

    // Set up BroadcastReceiver and message filter
    initializeBroadcastReceiver();

    // Get references on control elements and set EventListener
    initializeControlElements();

    // Set up Bluetooth
    initializeBluetooth();

    // Initialize fields saving the discovered devices and the selected
    discoveredRobots = new TreeMap<String, BluetoothDevice>();
    selectedRobots = new TreeMap<String, BluetoothDevice>();
  }

  /**
   * Registers the BroadcastReceiver when Activity comes into foreground.
   */
  public void onResume() {
    super.onResume();
    registerReceiver(bcReceiver, messageFilter);
  }

  /**
   * Unregisters the BroadcastReceiver when Activity goes to background.
   */
  public void onPause() {
    unregisterReceiver(bcReceiver);
    super.onPause();
  }

  /**
   * Handles result value of Activity that enables Bluetooth.
   * 
   * @param requestCode
   *          The code needed to identify the result message. The code is set
   *          when calling startActivityForResult().
   * @param resultCode
   *          Identifies the result the Activity has returned.
   * @param data
   *          Intent message containing extra data.
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
      if (resultCode == RESULT_CANCELED) {
        displayMessage(getString(R.string.ERR_MSG_BLUETOOTH_NOT_ENABLED), false);
      }
    }
  }
  
  /**
   * Sets the menu of the Activity.
   * 
   * @param menu
   *          Reference on menu that has to be set.
   */
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.connect_menu, menu);
    return true;
  }

  /**
   * Handles selections of the menu of the Activity.
   * 
   * @param item
   *          The MenuItem that has been selected.
   */
  public boolean onOptionsItemSelected(MenuItem item) {
    // Intent message to start other Activities.
    Intent start = new Intent();

    switch (item.getItemId()) {

    // If "Connect" has been chosen, check if no robot is selected. In this
    // case display a hint message. Otherwise start connecting to robots
    // one by one.
    // If all are successfully connected, start Map-Activity via Intent
    // message.
    case R.id.mnuStart:

      if (selectedRobots.size() > 0) {
        
        if (bluetoothAdapter.isEnabled()) {
        pDialog = ProgressDialog.show(Connect.this,
            getString(R.string.TXT_CONNECTING),
            getString(R.string.MSG_CONNECTING), true);

        // Start Thread that opens connections
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new ConnectionThread(selectedRobots.values()));
        } else {
          enableBluetooth();
        }
      } else {
        displayMessage(getString(R.string.MSG_NO_ROBOT), false);
      }
      break;

    // If "Simulation" has been chosen, start Simulation-Activity via
    // Intent.
    case R.id.mnuSimulation:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Simulation.class.getName()));
      startActivity(start);
      break;

    // If "Import" has been chosen, start Import-Activity via Intent.
    case R.id.mnuImport:
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Import.class.getName()));
      start.putExtra(ImportMode.class.toString(), ImportMode.IMPORT_MAP);
      startActivity(start);
      break;
    
      // Only debug
    case R.id.mnuDebug:
      start.putExtra(MapMode.class.toString(), MapMode.EXPLORATION);
      start.setComponent(new ComponentName(getApplicationContext()
          .getPackageName(), Map.class.getName()));
      startActivity(start);
      break;
    }
    return true;
  }

  /**
   * Initializes control elements of the activity and sets EventListeners.
   */
  private void initializeControlElements() {

    // Get reference on "Search"-Button and set OnClickListener
    btnSearch = (Button) findViewById(R.id.btnSearch);
    btnSearch.setOnClickListener(new ConnectOnClickListener());

    // Get reference on ListView and set OnItemClickListener
    lsRobots = (ListView) findViewById(R.id.lsRobots);
    lsRobots.setOnItemClickListener(new ConnectOnItemClickListener());

    // Initialize ArrayAdapter holding data for ListView
    robotList = new ArrayAdapter<String>(this, R.layout.list_item);
  }

  /**
   * Initializes the BroadcastReceiver needed to receive Broadcast messages of
   * the Android system.
   */
  private void initializeBroadcastReceiver() {

    // Instantiate BroadcastReceiver
    bcReceiver = new BluetoothBroadcastReceiver();

    // Instantiate IntentFilter and set it's message filter
    messageFilter = new IntentFilter();
    messageFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    messageFilter.addAction(BluetoothDevice.ACTION_FOUND);
    messageFilter.addAction(CONNECTION_SUCCESSFUL);
    messageFilter.addAction(CONNECTION_ERROR);
  }

  /**
   * Requests Bluetooth-Adapter of the device and checks whether device supports
   * Bluetooth.
   */
  private void initializeBluetooth() {
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // If bluetoothAdapter equals null then smartphone does not support
    // Bluetooth.
    // In this case, display message and disable "Search"-Button.
    // Otherwise check, if Bluetooth is enabled.
    if (bluetoothAdapter == null) {
      displayMessage(getString(R.string.ERR_MSG_BLUETOOTH_NOT_SUPPORTED), true);
      btnSearch.setEnabled(false);
    } else if (!bluetoothAdapter.isEnabled()) {
      enableBluetooth();
    }
  }

  /**
   * Starts an Activity to enable the Bluetooth-Adapter of the device.
   */
  private void enableBluetooth() {
    // Set up an Intent message, set it's action to request enable Bluetooth
    Intent intent = new Intent();
    intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
  }

  /**
   * Displays a message on top of the Activity.
   * 
   * @param message
   *          The message to display.
   */
  private void displayMessage(String message, boolean isError) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setCancelable(false);
    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
      
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Handles broadcast messages of BluetoothAdapter. Specialized on 'device
   * found'- and 'discovery finished'-messages. Inherits from abstract class
   * BroadCastreceiver.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private final class BluetoothBroadcastReceiver extends BroadcastReceiver {

    /**
     * Pattern that Bluetooth names of discovered devices have to match
     */
    private Pattern ePuckName = Pattern.compile("e-puck_\\d\\d\\d\\d");

    /**
     * Called when class receives broadcast message.
     * 
     * @param context
     *          Current context.
     * @param intent
     *          Intent containing message and data.
     */
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      // If device has been found check its name. If name matches pattern
      // add it to discovered devices.
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = (BluetoothDevice) intent
            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (ePuckName.matcher(device.getName()).matches()) {
          discoveredRobots.put(device.getName(), device);
          robotList.add(device.getName());
        }

        // If discovery is finished display the discovered devices.
      } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        lsRobots.setAdapter(robotList);
        pDialog.dismiss();
        // If connecting to all devices has been successful start Map Activity.
      } else if (CONNECTION_SUCCESSFUL.equals(action)) {
        pDialog.dismiss();
        Intent start = new Intent();
        start.putExtra(MapMode.class.toString(), MapMode.EXPLORATION);
        start.setComponent(new ComponentName(getApplicationContext()
            .getPackageName(), Map.class.getName()));
        startActivity(start);

        // If an error occurs during one of the connect actions, display error
        // message.
      } else if (CONNECTION_ERROR.equals(action)) {
        pDialog.dismiss();
        String deviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
        displayMessage("Connection to " + deviceName + " not successful esthablished", true);
      }
    }
  }

  /**
   * Handles clicks on items of ListView.
   * 
   * @author Andreas Poxrucker
   */
  private final class ConnectOnItemClickListener implements OnItemClickListener {

    /**
     * Handles clicks on items of ListView containing the discovered robots.
     * 
     * If an item is selected, its text color changes and the robot is added or
     * removed from the set of selected robots.
     * 
     * @param parent
     *          AdapterView containing the list item.
     * @param view
     *          List item that has been clicked.
     * @param position
     *          Position of the list item within the AdapterView.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
      TextView txtDevice = (TextView) view;
      String deviceName = txtDevice.getText().toString();
      Resources res = getResources();

      if (selectedRobots.containsKey(deviceName)) {
        selectedRobots.remove(deviceName);
        txtDevice.setTextColor(res.getColor(R.color.list_item_not_selected));
      } else {
        selectedRobots.put(deviceName, discoveredRobots.get(deviceName));
        txtDevice.setTextColor(res.getColor(R.color.list_item_selected));
      }
    }
  }

  /**
   * Handles click events on control elements of the Activity.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private final class ConnectOnClickListener implements OnClickListener {

    /**
     * Called when registered button receives OnClick-Event.
     * 
     * @param view
     *          View that has been clicked.
     */
    public void onClick(View v) {
      int viewId = v.getId();

      switch (viewId) {

      // If btnSearch has been clicked, check, if Bluetooth is enabled.
      // If not, start Bluetooth enable activity, otherwise clear all
      // fields and start searching
      case R.id.btnSearch:
        if (!bluetoothAdapter.isEnabled()) {
          enableBluetooth();
        } else {
          bluetoothAdapter.cancelDiscovery();
          discoveredRobots.clear();
          robotList.clear();
          selectedRobots.clear();
          pDialog = ProgressDialog.show(Connect.this,
              getString(R.string.TXT_SEARCHING),
              getString(R.string.MSG_SEARCHING), true);
          bluetoothAdapter.startDiscovery();
        }
      }
    }
  }

  /**
   * Opens Bluetooth connection for each device that has been passed in the
   * Constructor.
   * 
   * If all connections are established, it initiates the creation of the 
   * RealPuck instances.
   * 
   * If an error occurs during connecting, all connections that have been
   * opened so far are closed and Thread returns error message.
   * 
   * @author Andreas Poxrucker
   * 
   */
  private class ConnectionThread implements Runnable {

    /**
     * Standard UUID used to connect to standard Bluetooth modules.
     */
    private UUID STD_UUID = UUID
        .fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * Devices that should be connected.
     */
    Collection<BluetoothDevice> devices;

    /**
     * Constructor.
     * 
     * @param robots
     *          The BluetoothDevices that should be connected.
     */
    public ConnectionThread(Collection<BluetoothDevice> robots) {
      devices = robots;
    }

    /**
     * Called when Thread is executed.
     * 
     * Opens connections to BluetoothDevices passed in the Constructor and
     * finally creates RealPuck instances.
     * 
     * If opening a connection fails and an IOException occurs, all connections
     * are closed and error message is sent.
     */
    public void run() {
      List<BluetoothSocket> sockets = new LinkedList<BluetoothSocket>();
      Intent intent = new Intent();

      for (BluetoothDevice device : devices) {
        try {
          // Open RFCommSocket with standard UUID.
          BluetoothSocket socket = device
              .createRfcommSocketToServiceRecord(STD_UUID);
          socket.connect();
          sockets.add(socket);
        } catch (IOException connectException) {
          
          // If IOException occurs during opening RfCommSocket, cancel all
          // connections that have been opened so far and send error message.
          for (BluetoothSocket socket : sockets) {
            try {
              socket.close();
            } catch (IOException closeException) {
              // do to?!
            }
          }
          intent.putExtra(BluetoothDevice.EXTRA_NAME, device.getName());
          intent.setAction(Connect.CONNECTION_ERROR);
          sendBroadcast(intent);
          return;
        }
      }

      // When all sockets are connected properly create RealPuckInstances
      // and send success message
      PuckFactory.createRealPucks(sockets);
	    intent.setAction(CONNECTION_SUCCESSFUL);
      sendBroadcast(intent); 
    }
  }
}