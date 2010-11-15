package sep.conquest;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectHandler {
  
  public static final UUID STD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  public static BluetoothSocket connect(BluetoothDevice device) {
    
    try {
      BluetoothSocket socket = device.createRfcommSocketToServiceRecord(STD_UUID);
      
      try {
        socket.connect();
        return socket;
      } catch (IOException connectExcetion) {
        return null;
      }
    } catch (IOException createRfException) {
      return null;
    }
    
  }
  
}
