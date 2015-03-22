package sep.conquest.model.test;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

import android.test.AndroidTestCase;
import sep.conquest.model.*;
import sep.conquest.model.behaviour.BehaviourFactory;
import sep.conquest.model.behaviour.IBehaviour;
import sep.conquest.util.Utility;

/**
 * A test-case for behaviour-level.
 * 
 * @author Andreas Wilhelm
 *
 */
public class BehaviourTest extends AndroidTestCase {
	
	GridMap map;
	Puck robot;
	
	public BehaviourTest() {
		super();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		map = new GridMap();
		fillMap(map, true);
		
		// create robot
		int[] pos_r1 = {0, 0};
		UUID id_r1 = UUID.randomUUID();
		TreeMap<UUID, int[]> initPositions = new TreeMap<UUID, int[]>();
		initPositions.put(id_r1, pos_r1);
		TreeMap<UUID, Orientation> initOrientations = new TreeMap<UUID, Orientation>();
		initOrientations.put(id_r1, Orientation.UP);
		RobotStatus status = new RobotStatus();
		status.setPosition(pos_r1);
		status.setOrientation(Orientation.UP);
		robot = new VirtualPuck(id_r1, new Simulator(map, initPositions, initOrientations), "Robot1");
		fillMap(robot.getMap(), false);
		robot.getRobotStatus().put(id_r1, status);
	}

	/**
	 * Test method for {@link sep.conquest.model.ComManager#addClient(UUID, sep.conquest.model.IComClient)}.
	 */
	public void testExploreBehaviour() {
		IBehaviour chain = BehaviourFactory.createBehaviourChain(State.EXPLORE);
		LinkedList<GraphNode> nodes = robot.getMap().getFrontierList();
		TreeMap<Integer, Integer> frontierNodes = new TreeMap<Integer, Integer>();
		for (GraphNode node: nodes)
			frontierNodes.put(Utility.makeKey(node.getXValue(), node.getYValue()), 0);
		
		chain.execute(frontierNodes, robot);
		
		int min = 100;
		Integer bestNode = 0;
		for (GraphNode node: nodes) {
			Integer key = Utility.makeKey(node.getXValue(), node.getYValue());
			Integer val = frontierNodes.get(key);
			if (val < min) {
				bestNode = key;
				min = val;
			}
				
		}
		Integer intent = Utility.makeKey(1, 3);
		
		assertTrue("Wrong node has been chosen.", intent.compareTo(bestNode) == 0);
	}
	
	private void fillMap(GridMap map, boolean complete) {
		// create map
		map.addNode(0, 0, NodeType.BOTTOMRIGHTEDGE);
		map.addNode(1, 0, NodeType.BOTTOMT);
		map.addNode(2, 0, NodeType.BOTTOMT);
		map.addNode(3, 0, NodeType.BOTTOMT);
		map.addNode(4, 0, NodeType.BOTTOMT);
		map.addNode(5, 0, NodeType.BOTTOMLEFTEDGE);
		map.addNode(0, 1, NodeType.RIGHTT);
		map.addNode(3, 1, NodeType.CROSS);
		map.addNode(5, 1, NodeType.LEFTT);
		map.addNode(0, 2, NodeType.RIGHTT);
		map.addNode(3, 2, NodeType.CROSS);
		map.addNode(5, 2, NodeType.LEFTT);
		map.addNode(0, 3, NodeType.RIGHTT);
		map.addNode(3, 3, NodeType.CROSS);
		map.addNode(5, 3, NodeType.LEFTT);
		map.addNode(0, 4, NodeType.TOPRIGHTEDGE);
		map.addNode(1, 4, NodeType.TOPT);
		map.addNode(2, 4, NodeType.TOPT);
		map.addNode(3, 4, NodeType.TOPT);
		map.addNode(4, 4, NodeType.TOPT);
		map.addNode(5, 4, NodeType.TOPLEFTEDGE);
		map.addNode(1, 1, NodeType.CROSS);
		map.addNode(1, 2, NodeType.CROSS);
		map.addNode(2, 1, NodeType.CROSS);
		map.addNode(2, 2, NodeType.CROSS);
		map.addNode(2, 3, NodeType.CROSS);
		map.addNode(4, 1, NodeType.CROSS);
		map.addNode(4, 2, NodeType.CROSS);
		if (complete) {
			map.addNode(1, 3, NodeType.CROSS);
			map.addNode(4, 3, NodeType.CROSS);
		}
		
	}	

}
