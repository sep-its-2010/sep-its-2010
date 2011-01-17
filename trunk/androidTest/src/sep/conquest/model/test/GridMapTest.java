package sep.conquest.model.test;

import java.util.LinkedList;

import android.test.AndroidTestCase;
import android.util.Log;
import sep.conquest.model.GridMap;
import sep.conquest.model.MapNode;
import sep.conquest.model.GraphNode;
import sep.conquest.model.NodeType;


public class GridMapTest extends AndroidTestCase{
	static final String LOG_TAG = "GridMapTest ";
	GridMap myMap;
	
	public void setUp(){
		myMap = new GridMap();
	}
	
	/**
	 * Test to check whether the Node is inserted in the map. The size of map is
	 * checked
	 */
	public void testInsertNode(){
		NodeType newNodeType = NodeType.CROSS;
		int x = 3;
		int y= 4;
		myMap.addNode(x, y, newNodeType);
		assertTrue(LOG_TAG + " NumberOfNodes", (myMap.getMapAsList().size() == 1));
	}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within RightT.
	 */
	public void testFrontierNodeRightT(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.RIGHTT;		
		x = -1;		
		y= 3;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 3);		
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
	}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within LeftT.
	 */
	public void testFrontierNodeLeftT(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.LEFTT;		
		x = -1;		
		y= 3;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 3);		
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
	}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within BottomT.
	 */
	public void testFrontierNodeBottomT(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.BOTTOMT;		
		x = 0;		
		y= 0;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 3);		
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
	}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within TopT.
	 */
	public void testFrontierNodeTopT(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.TOPT;		
		x = 1;		
		y= -3;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 3);		
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
		
	}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within cross.
	 */
	public void testFrontierNodeCross(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.CROSS;		
		x = -1;		
		y= -3;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 4);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
	}
	
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within bottomrightedge.
	 */
	public void testFrontierNodeBottomRightEdge(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.BOTTOMRIGHTEDGE;		
		x = -1;		
		y= -1;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 2);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);		
		}
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within toprightedge.
	 */
	public void testFrontierNodeTopRightEdge(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.TOPRIGHTEDGE;		
		x = -1;		
		y= -1;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 2);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "RightFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x+1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
	}
	
	
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within bottomleftedge.
	 */
	public void testFrontierNodeBottomLeftEdge(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.BOTTOMLEFTEDGE;		
		x = -1;		
		y= -1;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 2);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "BottomFrontierNode", (actNode.getYValue() == (y+1))&& actNode.getXValue() == x);
		}
	
	
	/**
	 * Tests whether the frontierNodes will be created in the right way after
	 * inserting the first node within topleftedge.
	 */
	public void testFrontierNodeTopLeftEdge(){
		NodeType newNodeType;
		int x;
		int y;
		GraphNode actNode;
		LinkedList<GraphNode> frontList;
		newNodeType = NodeType.TOPLEFTEDGE;		
		x = -1;		
		y= -1;
		myMap.addNode(x, y, newNodeType);
		frontList = myMap.getFrontierList();
		assertTrue(LOG_TAG + " NumberOfFrontierNodes", frontList.size() == 2);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "LeftFrontierNode", (actNode.getYValue() == (y))&& actNode.getXValue() == x-1);
		actNode= frontList.poll();
		assertTrue(LOG_TAG + "TopFrontierNode", (actNode.getYValue() == (y-1))&& actNode.getXValue() == x);
	}
	
	/**
	 * Inserts a node two times, with different nodetypes
	 */
	public void testUpdateNode(){
		int x = 3;
		int y = 3;
		
		NodeType actNodeType = NodeType.CROSS;
		myMap.addNode(x, y, actNodeType);		
		actNodeType = NodeType.BOTTOMLEFTEDGE;
		myMap.addNode(x+1, y, actNodeType);
		GraphNode actNode = myMap.getNode(x+1, y);			
		assertTrue(LOG_TAG + " Geupdateter NodeType", actNode.getNodeType()==NodeType.BOTTOMLEFTEDGE &&(myMap.getFrontierList().size() == 4));
	}
	
	/**
	 * Inserts a few nodes and checks the mapBorders
	 */
	public void testMapBorders(){
		int x;
		int y;
		NodeType actNodeType;
		x= 3;
		y= 5;
		actNodeType = NodeType.CROSS;
		myMap.addNode(x, y, actNodeType);
		x= 2;
		y= 5;
		actNodeType = NodeType.CROSS;
		myMap.addNode(x, y, actNodeType);
		x= 4;
		y= 5;
		actNodeType = NodeType.CROSS;
		myMap.addNode(x, y, actNodeType);
		x= 3;
		y= 4;
		actNodeType = NodeType.CROSS;		
		myMap.addNode(x, y, actNodeType);
		int[] borders = myMap.getMapBorders();
		assertTrue(LOG_TAG + " MapBorders", borders[0] == 2 && borders[1] == 4 && borders[2] == 4 && borders[3] == 5);		
	}
	
	/**
	 * Checks whether the format of the ouputstring is correct
	 */
	public void testSerializeMapInString(){
		int x = 1;
		int y = 1;
		NodeType actNodeType = NodeType.CROSS;
		myMap.addNode(x, y, actNodeType);
		GraphNode bufferNode = myMap.getNode(x, y);
		String[] str =myMap.serializeMapInString();
		assertTrue(LOG_TAG+" serializedMapintoString", str[0].equals((String.valueOf(bufferNode.getXValue()) + " "
	            + String.valueOf(bufferNode.getYValue()) + " "
	            + String.valueOf(bufferNode.getNodeType()) + "\n")));
	}
}
