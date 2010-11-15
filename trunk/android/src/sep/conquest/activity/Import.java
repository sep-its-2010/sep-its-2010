package sep.conquest.activity;

import java.util.Map;

import sep.conquest.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Import extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.menu_connect, menu);
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem item) {

    Intent start = new Intent();
    
    switch (item.getItemId()) {
    case R.id.mnuConnect:
      start.setComponent(new ComponentName(getApplicationContext().getPackageName(), Connect.class.getName()));
      startActivity(start);
      break;
    case R.id.mnuSimulation:
      start.setComponent(new ComponentName(getApplicationContext().getPackageName(), Simulation.class.getName()));
      startActivity(start);
      break;
    case R.id.mnuStart:
      start.setComponent(new ComponentName(getApplicationContext().getPackageName(), Map.class.getName()));
      startActivity(start);
      break;
    }
    return true;
  }
}
