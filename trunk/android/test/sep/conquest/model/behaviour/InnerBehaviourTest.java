/**
 * 
 */
package sep.conquest.model.behaviour;


import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Test;

import sep.conquest.model.GraphNode;
import sep.conquest.model.GridMap;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.Simulator;
import sep.conquest.model.State;
import sep.conquest.model.VirtualPuck;
import sep.conquest.model.behaviour.InnerBehaviour;
import sep.conquest.util.Utility;

/**
 * @author Andreas Wilhelm
 *
 */
public class InnerBehaviourTest {

	/**
	 * Test method for {@link sep.conquest.model.behaviour.InnerBehaviour#execute(java.util.Map, sep.conquest.model.Puck)}.
	 */
	@Test
	public void testExecute() {
		
		Puck robot;
		Map<Integer, Integer> mapCosts;
		GridMap map;
		
		// create map
		map = new GridMap();
		fillMap(map);

		mapCosts = new TreeMap<Integer, Integer>();
		for (GraphNode node : map.getFrontierList())
			mapCosts.put(Utility.makeKey(node.getXValue(), node.getYValue()),
					new Integer(0));

		// create robot
		int[] pos_r1 = { 0, 0 };
		int[] pos_r2 = { 4, 3 };
		int[] pos_r3 = { 5, 2 };
		UUID id_r1 = UUID.randomUUID();
		UUID id_r2 = UUID.randomUUID();
		UUID id_r3 = UUID.randomUUID();
		TreeMap<UUID, int[]> initPositions = new TreeMap<UUID, int[]>();
		initPositions.put(id_r1, pos_r1);
		initPositions.put(id_r2, pos_r2);
		initPositions.put(id_r3, pos_r3);
		TreeMap<UUID, Orientation> initOrientations = new TreeMap<UUID, Orientation>();
		initOrientations.put(id_r1, Orientation.UP);
		initOrientations.put(id_r2, Orientation.UP);
		initOrientations.put(id_r3, Orientation.UP);
		RobotStatus status = new RobotStatus();
		status.setPosition(pos_r1);
		status.setOrientation(Orientation.RIGHT);
		robot = new VirtualPuck(id_r1, new Simulator(map, initPositions,
				initOrientations), "Robot1");
		fillMap(robot.getMap());			
		
		IBehaviour behaviour = new InnerBehaviour(State.EXPLORE, null);

		mapCosts = behaviour.execute(mapCosts, robot);

		for (int i = map.getMapBorders()[0]; i < map.getMapBorders()[2]; i++) {
			for (int j = map.getMapBorders()[1]; j < map.getMapBorders()[3]; j++)
				if (mapCosts.containsKey(Utility.makeKey(i, j)))
					System.out.print("(" + i + "," + j + ") - "
							+ mapCosts.get(Utility.makeKey(i, j)) + "  ");
			System.out.println();
		}
		
		assertNotNull("MapCosts were not calculated properly.",
				mapCosts);
		
	}

	private static void fillMap(GridMap map) {
		// create map
		map.addNode(0, 0, NodeType.TOPLEFTEDGE);
		map.addNode(1, 0, NodeType.TOPT);
		map.addNode(2, 0, NodeType.TOPT);
		map.addNode(3, 0, NodeType.TOPT);
		map.addNode(4, 0, NodeType.TOPT);
		map.addNode(5, 0, NodeType.TOPRIGHTEDGE);
		map.addNode(0, 1, NodeType.LEFTT);
		map.addNode(3, 1, NodeType.CROSS);
		map.addNode(5, 1, NodeType.RIGHTT);
		map.addNode(0, 2, NodeType.LEFTT);
		map.addNode(3, 2, NodeType.CROSS);
		map.addNode(5, 2, NodeType.RIGHTT);
		map.addNode(0, 3, NodeType.LEFTT);
		map.addNode(3, 3, NodeType.CROSS);
		map.addNode(5, 3, NodeType.RIGHTT);
		map.addNode(0, 4, NodeType.BOTTOMLEFTEDGE);
		map.addNode(1, 4, NodeType.BOTTOMT);
		map.addNode(2, 4, NodeType.BOTTOMT);
		map.addNode(3, 4, NodeType.BOTTOMT);
		map.addNode(4, 4, NodeType.BOTTOMT);
		map.addNode(5, 4, NodeType.BOTTOMRIGHTEDGE);
	}	
	
}
