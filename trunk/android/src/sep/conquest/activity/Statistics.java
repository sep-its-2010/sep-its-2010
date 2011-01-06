package sep.conquest.activity;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.TextView;

public class Statistics extends Activity implements Observer{
	
	private TextView explored;
	private TextView frontier;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.statistics_main);

		explored = (TextView) findViewById(R.id.nodeNumberValues);
		frontier = (TextView) findViewById(R.id.frontierNumberValues);
		
		
		
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
		
		int total = 0;
		explored.setText("");
		Set<UUID> id = cu.getRobotStatus().keySet();
		for (UUID key : id) {
			int number = cu.getExploredNodes(key);
			String name = cu.getRobotName(key);
			explored.append(name + ": " + number + " nodes \n");
			total += number;
		}
		explored.append("\nTotal: " + total + " nodes");
		
	}

}
