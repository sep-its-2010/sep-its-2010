package sep.conquest.activity;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Statistics extends Activity implements Observer{
	
	private TextView numberOf;
	private TextView overrun;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.statistics_main);

		numberOf = (TextView) findViewById(R.id.nodeNumberValues);
		overrun = (TextView) findViewById(R.id.overrunValues);
		
	}
	
    public void onResume() {
    	super.onResume();
    	Controller.getInstance().getEnv().addObserver(this);
    }
    
    public void onPause() {
    	super.onPause();
    	Controller.getInstance().getEnv().deleteObserver(this);
    }

	public void update(Observable obs, Object data) {
		ConquestUpdate cu = (ConquestUpdate) data;
		
		Set<UUID> id = cu.getRobotStatus().keySet();
		for (UUID key : id) {
			
		}		
	}
	
	
	
	
	
	
	
	
	

}
