package sep.conquest.model;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andreas Wilhelm
 *
 */
public class AStarPathFinderTest {

	AStarPathFinder aStar;
	GridMap map;
	Puck robot;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		aStar = new AStarPathFinder();
		
		// create robot
		UUID myID = UUID.randomUUID();
		RobotStatus status = new RobotStatus();
		int[] pos = {0, 0};
		status.setPosition(pos);
		status.setOrientation(Orientation.RIGHT);
		robot = new VirtualPuck(myID);
		robot.getRobotStatus().put(myID, status);
		RobotStatus status_r2 = new RobotStatus();
		int[] pos_r2 = {4, -3};
		status_r2.setPosition(pos_r2);
		robot.getRobotStatus().put(UUID.randomUUID(), status_r2);
		RobotStatus status_r3 = new RobotStatus();
		int[] pos_r3 = {5, -2};
		status_r3.setPosition(pos_r3);
		robot.getRobotStatus().put(UUID.randomUUID(), status_r3);	
		
		// create map
		robot.getMap().addNode(0, 0, NodeType.TOPLEFTEDGE);
		robot.getMap().addNode(1, 0, NodeType.TOPT);
		robot.getMap().addNode(2, 0, NodeType.TOPT);
		robot.getMap().addNode(3, 0, NodeType.TOPT);
		robot.getMap().addNode(4, 0, NodeType.TOPRIGHTEDGE);
		robot.getMap().addNode(0, -1, NodeType.LEFTT);
		robot.getMap().addNode(1, -1, NodeType.CROSS);
		robot.getMap().addNode(2, -1, NodeType.CROSS);
		robot.getMap().addNode(3, -1, NodeType.CROSS);		
		robot.getMap().addNode(4, -1, NodeType.RIGHTT);
		robot.getMap().addNode(0, -2, NodeType.LEFTT);
		robot.getMap().addNode(1, -2, NodeType.CROSS);
		robot.getMap().addNode(2, -2, NodeType.CROSS);
		robot.getMap().addNode(3, -2, NodeType.CROSS);		
		robot.getMap().addNode(4, -2, NodeType.CROSS);			
		robot.getMap().addNode(5, -2, NodeType.TOPT);
		robot.getMap().addNode(6, -2, NodeType.TOPRIGHTEDGE);
		robot.getMap().addNode(0, -3, NodeType.BOTTOMLEFTEDGE);
		robot.getMap().addNode(1, -3, NodeType.BOTTOMT);	
		robot.getMap().addNode(2, -3, NodeType.CROSS);
		robot.getMap().addNode(3, -3, NodeType.CROSS);
		robot.getMap().addNode(4, -3, NodeType.CROSS);		
		robot.getMap().addNode(5, -3, NodeType.CROSS);	
		robot.getMap().addNode(6, -3, NodeType.RIGHTT);
		robot.getMap().addNode(2, -4, NodeType.LEFTT);	
		robot.getMap().addNode(3, -4, NodeType.CROSS);
		robot.getMap().addNode(4, -4, NodeType.CROSS);		
		robot.getMap().addNode(5, -4, NodeType.CROSS);	
		robot.getMap().addNode(6, -4, NodeType.RIGHTT);	
		robot.getMap().addNode(2, -5, NodeType.BOTTOMLEFTEDGE);			
		robot.getMap().addNode(3, -5, NodeType.BOTTOMT);
		robot.getMap().addNode(4, -5, NodeType.BOTTOMT);			
		robot.getMap().addNode(5, -5, NodeType.BOTTOMT);	
		robot.getMap().addNode(6, -5, NodeType.BOTTOMRIGHTEDGE);	
		
		assertEquals("Wrong number of nodes.",
						34, robot.getMap().getMapAsList().size());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link sep.conquest.model.AStarPathFinder#caluclateCosts(sep.conquest.model.Puck, sep.conquest.model.MapNode, sep.conquest.model.MapNode)}.
	 */
	@Test
	public void testCaluclateCosts() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sep.conquest.model.AStarPathFinder#find(sep.conquest.model.Puck, sep.conquest.model.MapNode, sep.conquest.model.MapNode[])}.
	 */
	@Test
	public void testFindPuckMapNodeMapNodeArray() {
		
		MapNode node = robot.getMap().getNode(0, 0);		
		assertNotNull("Start node wasn't found.", node);
		
		MapNode dest1 = robot.getMap().getNode(6, -3);		
		assertNotNull("Destination-1 node wasn't found.", dest1);

		MapNode dest2 = robot.getMap().getNode(5, -5);		
		assertNotNull("Destination-2 node wasn't found.", dest2);
		
		MapNode dest3 = robot.getMap().getNode(2, -5);		
		assertNotNull("Destination-3 node wasn't found.", dest3);
		
		MapNode[] destinations = {dest1, dest2, dest3};
		
		PathNode[] path = aStar.find(robot, node, destinations);
		
		assertNotNull("wrong answer", path);
	}

	/**
	 * Test method for {@link sep.conquest.model.AStarPathFinder#find(sep.conquest.model.Puck, int[], int[][])}.
	 */
	@Test
	public void testFindPuckIntArrayIntArrayArray() {
		fail("Not yet implemented");
	}

}