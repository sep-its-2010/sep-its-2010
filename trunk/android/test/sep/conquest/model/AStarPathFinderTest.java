package sep.conquest.model;

import static org.junit.Assert.*;

import java.util.TreeMap;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andreas Wilhelm
 *
 */
public class AStarPathFinderTest{

	AStarPathFinder aStar;
	GridMap map;
	Puck robot;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		aStar = new AStarPathFinder();
		map  = new GridMap();

		// create map
		fillMap(map);	
		
		// create robot
		int[] pos_r1 = {0, 0};
		int[] pos_r2 = {4, 3};
		int[] pos_r3 = {5, 2};
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
		robot = new VirtualPuck(id_r1, new Simulator(map, initPositions, initOrientations), "Robot1");
		fillMap(robot.getMap());
		robot.getRobotStatus().put(id_r1, status);
		RobotStatus status_r2 = new RobotStatus();		
		status_r2.setPosition(pos_r2);
		robot.getRobotStatus().put(UUID.randomUUID(), status_r2);
		RobotStatus status_r3 = new RobotStatus();		
		status_r3.setPosition(pos_r3);
		robot.getRobotStatus().put(UUID.randomUUID(), status_r3);		
		
		assertEquals("Wrong number of nodes.",
						34, robot.getMap().getMapAsList().size());
	}

	private void fillMap(GridMap map) {
		// create map
		map.addNode(0, 0, NodeType.TOPLEFTEDGE);
		map.addNode(1, 0, NodeType.TOPT);
		map.addNode(2, 0, NodeType.TOPT);
		map.addNode(3, 0, NodeType.TOPT);
		map.addNode(4, 0, NodeType.TOPRIGHTEDGE);
		map.addNode(0, 1, NodeType.LEFTT);
		map.addNode(1, 1, NodeType.CROSS);
		map.addNode(2, 1, NodeType.CROSS);
		map.addNode(3, 1, NodeType.CROSS);		
		map.addNode(4, 1, NodeType.RIGHTT);
		map.addNode(0, 2, NodeType.LEFTT);
		map.addNode(1, 2, NodeType.CROSS);
		map.addNode(2, 2, NodeType.CROSS);
		map.addNode(3, 2, NodeType.CROSS);		
		map.addNode(4, 2, NodeType.CROSS);			
		map.addNode(5, 2, NodeType.TOPT);
		map.addNode(6, 2, NodeType.TOPRIGHTEDGE);
		map.addNode(0, 3, NodeType.BOTTOMLEFTEDGE);
		map.addNode(1, 3, NodeType.BOTTOMT);	
		map.addNode(2, 3, NodeType.CROSS);
		map.addNode(3, 3, NodeType.CROSS);
		map.addNode(4, 3, NodeType.CROSS);		
		map.addNode(5, 3, NodeType.CROSS);	
		map.addNode(6, 3, NodeType.RIGHTT);
		map.addNode(2, 4, NodeType.LEFTT);	
		map.addNode(3, 4, NodeType.CROSS);
		map.addNode(4, 4, NodeType.CROSS);		
		map.addNode(5, 4, NodeType.CROSS);	
		map.addNode(6, 4, NodeType.RIGHTT);	
		map.addNode(2, 5, NodeType.BOTTOMLEFTEDGE);			
		map.addNode(3, 5, NodeType.BOTTOMT);
		map.addNode(4, 5, NodeType.BOTTOMT);			
		map.addNode(5, 5, NodeType.BOTTOMT);	
		map.addNode(6, 5, NodeType.BOTTOMRIGHTEDGE);	
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {		
	}

	/**
	 * Test method for {@link sep.conquest.model.AStarPathFinder#find(sep.conquest.model.Puck, sep.conquest.model.MapNode, sep.conquest.model.MapNode[])}.
	 */
	@Test
	public void testFindPuckMapNodeMapNodeArray() {
		
		MapNode node = robot.getMap().getNode(0, 0);		
		assertNotNull("Start node wasn't found.", node);
		
		MapNode dest1 = robot.getMap().getNode(6, 3);		
		assertNotNull("Destination-1 node wasn't found.", dest1);

		MapNode dest2 = robot.getMap().getNode(5, 5);		
		assertNotNull("Destination-2 node wasn't found.", dest2);
		
		MapNode dest3 = robot.getMap().getNode(2, 5);		
		assertNotNull("Destination-3 node wasn't found.", dest3);
		
		MapNode[] destinations = {dest1, dest2, dest3};
		
		PathNode[] path = aStar.find(robot, node, destinations);
		for (PathNode p: path) {
			PathNode current = p;
			do {
				System.out.print("(" + current.getNode().getXValue() + ", " + current.getNode().getYValue() + ")  ");
				current = current.getNext();
				
			} while (current.getNext() != null);
			System.out.println("... " + current.getCosts() + " Punkte ...");
		}
		
		assertNotNull("wrong answer", path);
	}
}
