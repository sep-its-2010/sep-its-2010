package sep.conquest.model.handler.test;

import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.model.GridMap;
import sep.conquest.model.LogicThread;
import sep.conquest.model.NodeType;
import sep.conquest.model.Simulator;
import sep.conquest.model.VirtualPuck;
import sep.conquest.model.handler.Handler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.model.requests.VirtualPuckRequest;
import android.bluetooth.BluetoothSocket;

public class HandlerTest {

	VirtualPuck puck;
	LogicThread exe;
	Simulator sim;
	BluetoothSocket btsock;
	UUID id = UUID.randomUUID();
	HandlerFactory hf;
	VirtualPuckRequest req_reset;
	byte[] array = new byte[32];
	GridMap map = new GridMap();
	TreeMap<UUID, int[]> initPositions;
	int[] pos_r1 = {-1, -1 };
	
	public void setUp(){
		buildMap();
		
		//sim = new Simulator();
		puck = new VirtualPuck(id,sim,"PuckNumeroUno");
		exe = new LogicThread(puck);
		hf = new HandlerFactory();
	}

	
	public void buildMap() {
		
		initPositions = new TreeMap<UUID, int[]>();
		initPositions.put(id, pos_r1);
		map.addNode(-3, -3, NodeType.BOTTOMRIGHTEDGE);
		map.addNode(-2, -3, NodeType.BOTTOMT);
		map.addNode(-1, -3, NodeType.BOTTOMLEFTEDGE);
		map.addNode(1, -3, NodeType.BOTTOMRIGHTEDGE);
		map.addNode(2, -3, NodeType.BOTTOMT);
		map.addNode(3, -3, NodeType.BOTTOMLEFTEDGE);
		map.addNode(-3, -2, NodeType.RIGHTT);
		map.addNode(-2, -2, NodeType.CROSS);
		map.addNode(-1, -2, NodeType.LEFTT);
		map.addNode(1, -2,NodeType.RIGHTT);
		map.addNode(2, -2, NodeType.CROSS);
		map.addNode(3, -2,  NodeType.LEFTT);
		map.addNode(-3, -1, NodeType.RIGHTT);
		map.addNode(-2, -1,  NodeType.CROSS);
		map.addNode(-1, -1,  NodeType.CROSS);
		map.addNode(0, -1,  NodeType.BOTTOMT);
		map.addNode(1, -1,  NodeType.CROSS);
		map.addNode(2, -1,  NodeType.CROSS);
		map.addNode(3 ,-1,  NodeType.LEFTT);
		map.addNode(-3 ,0,  NodeType.TOPRIGHTEDGE);					
		map.addNode(-2, 0,  NodeType.CROSS);													
		map.addNode(-1 ,0,  NodeType.CROSS);																
		map.addNode(0, 0 , NodeType.TOPT);		
		map.addNode(1, 0, NodeType.CROSS);				
		map.addNode(2, 0 ,NodeType.CROSS);						
		map.addNode(3, 0 ,NodeType.TOPLEFTEDGE);						
		map.addNode(-2 ,1, NodeType.RIGHTT);
		map.addNode(-1, 1, NodeType.LEFTT);																											
		map.addNode(1, 1, NodeType.RIGHTT);														
		map.addNode(2, 1, NodeType.LEFTT);																
		map.addNode(-3 ,2 ,NodeType.BOTTOMRIGHTEDGE);																		
		map.addNode(-2 ,2,NodeType.CROSS);																				
		map.addNode(-1, 2 ,NodeType.CROSS);																																																											
		map.addNode(0, 2 ,NodeType.BOTTOMT);
		map.addNode(-3, 3 ,NodeType.RIGHTT);
		map.addNode(3, 2 ,NodeType.BOTTOMLEFTEDGE);
		map.addNode(2, 2 ,NodeType.CROSS);
		map.addNode(1 ,2 ,NodeType.CROSS);
		map.addNode(-2, 3 ,NodeType.CROSS);
		map.addNode(-1 ,3 ,NodeType.CROSS);
		map.addNode(0, 3, NodeType.TOPT);
		map.addNode(1 ,3 ,NodeType.CROSS);
		map.addNode(2, 3, NodeType.CROSS);
		map.addNode(3, 3,NodeType.LEFTT);
		map.addNode(-3, 4, NodeType.RIGHTT);
		map.addNode(-2, 4, NodeType.CROSS);
		map.addNode(-1, 4, NodeType.LEFTT);
		map.addNode(1, 4 ,NodeType.RIGHTT);
		map.addNode(2, 4, NodeType.CROSS);
		map.addNode(3 ,4, NodeType.LEFTT);
		map.addNode(-3, 5, NodeType.TOPRIGHTEDGE);
		map.addNode(-2, 5 ,NodeType.TOPT);
		map.addNode(-1, 5, NodeType.TOPLEFTEDGE);
		map.addNode(1, 5 ,NodeType.TOPRIGHTEDGE);
		map.addNode(2, 5, NodeType.TOPT);
		map.addNode(3, 5, NodeType.TOPLEFTEDGE);
	}
	
	public void testSimulatorMessages() {
		Handler handler = hf.getSimMsgChain(sim);
		
		//first message is reset
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x01;
		req_reset = new VirtualPuckRequest(id, array);
		handler.handleRequest(req_reset);
	}
	
	public void testRealMessgages() {
		
	}
	
	public void testSimTurnHandler() {
		
	}
	
	public void testSimStatusHandler() {
		
	}
	
	public void testSimSpeedHandler() {
		
	}
	
	public void testSimResetHandler() {
		
	}
	
	public void testSimMoveHandler() {
		
	}
	
	public void testSimLEDHandler() {
		
	}
	
	public void testPuckStatusHandler() {
		
	}
	
	public void testPuckRejectHandler() {
		
	}
}
