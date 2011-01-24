package sep.conquest.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import sep.conquest.R;
import sep.conquest.controller.Controller;
import sep.conquest.model.ConquestUpdate;
import sep.conquest.model.MapNode;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Statistics extends Activity implements Observer {

	private static final int UPDATE_MESSAGE = 0;

	private TextView explored;
	private TextView frontier;
	private TextView multiple;

	private Handler updateHandler;

	private String[] name;
	private int[] exploredNumber;

	private int total;
	
	private int multipleCrossed;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.statistics_main);

		explored = (TextView) findViewById(R.id.nodeNumberValues);
		frontier = (TextView) findViewById(R.id.frontierNumberValues);
		multiple = (TextView) findViewById(R.id.multipleNumberValues);

		updateHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == UPDATE_MESSAGE) {
					explored.setText("");
					frontier.setText("");
					multiple.setText("");
					for (int i = 0; i < name.length; i++) {

						explored.append(name[i] + ": " + exploredNumber[i]
								+ " " + getString(R.string.TXT_NODES) + "\n");
						
						frontier.append(name[i] + ": \n");

					}
					explored.append("\nTotal: " + total + " " + getString(R.string.TXT_NODES ) + "\n");
					multiple.append("\nTotal: " + multipleCrossed);
				}
			}
		};
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
		synchronized (data) {
			ConquestUpdate cu = (ConquestUpdate) data;
			Set<UUID> id = cu.getRobotStatus().keySet();
			name = new String[id.size()];
			exploredNumber = new int[id.size()];
			int i = 0;
			total = 0;
			
			for (UUID key : id) {
				exploredNumber[i] = cu.getExploredNodes(key);
				name[i] = cu.getRobotName(key);
				total += exploredNumber[i];
				i++;
			}

			multipleCrossed = 0;

			List<MapNode> temp = cu.getMapList();
			Iterator<MapNode> it = temp.iterator();
			
			while (it.hasNext()) {
				MapNode node = (MapNode) it.next();
				int sum = node.getVisitCounter() - 1;
				multipleCrossed += sum;
			}
		
			updateHandler.obtainMessage(UPDATE_MESSAGE).sendToTarget();
		}

	}
}
