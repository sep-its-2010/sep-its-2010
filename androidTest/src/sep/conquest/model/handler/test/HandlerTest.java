package sep.conquest.model.handler.test;

import java.util.TreeMap;
import java.util.UUID;

import sep.conquest.model.Environment;
import sep.conquest.model.FailureType;
import sep.conquest.model.GridMap;
import sep.conquest.model.LogicThread;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.RealPuck;
import sep.conquest.model.Simulator;
import sep.conquest.model.VirtualPuck;
import sep.conquest.model.handler.CollisionRequestHandler;
import sep.conquest.model.handler.ControlledRequestHandler;
import sep.conquest.model.handler.DriveRequestHandler;
import sep.conquest.model.handler.EnvFailureRequestHandler;
import sep.conquest.model.handler.FailureRequestHandler;
import sep.conquest.model.handler.HandlerFactory;
import sep.conquest.model.handler.IntentRequestHandler;
import sep.conquest.model.handler.PuckAbyssHandler;
import sep.conquest.model.handler.PuckCollisionHandler;
import sep.conquest.model.handler.PuckNodeHitHandler;
import sep.conquest.model.handler.PuckOkHandler;
import sep.conquest.model.handler.PuckRejectHandler;
import sep.conquest.model.handler.PuckStatusHandler;
import sep.conquest.model.handler.SimLEDHandler;
import sep.conquest.model.handler.SimMoveHandler;
import sep.conquest.model.handler.SimResetHandler;
import sep.conquest.model.handler.SimSpeedHandler;
import sep.conquest.model.handler.SimStatusHandler;
import sep.conquest.model.handler.SimTurnHandler;
import sep.conquest.model.requests.CollisionRequest;
import sep.conquest.model.requests.ControlledRequest;
import sep.conquest.model.requests.DriveRequest;
import sep.conquest.model.requests.FailureRequest;
import sep.conquest.model.requests.IntentRequest;
import sep.conquest.model.requests.PuckRequest;
import sep.conquest.model.requests.VirtualPuckRequest;
import android.bluetooth.BluetoothSocket;
import android.test.AndroidTestCase;

public class HandlerTest extends AndroidTestCase{

	VirtualPuck puck;
	LogicThread exe;
	Simulator sim;
	BluetoothSocket btsock;
	UUID id = UUID.randomUUID();
	UUID id2 = UUID.randomUUID();
	HandlerFactory hf;
	VirtualPuckRequest req;
	PuckRequest req2;
	IntentRequest req3;
	byte[] array = new byte[32];
	GridMap map = new GridMap();
	TreeMap<UUID, int[]> initPositions;
	TreeMap<UUID, Orientation> orientation;
	int[] pos_r1 = {-1, -1 };
	RealPuck puck2;
	UUID[] receiver;
	Environment env;
	
	
	String LOG_MES = "HandlerTest";
	
	public void setUp(){
		buildMap();
		
		//env = Environment.getInstance();
		sim = new Simulator(map, initPositions, orientation);
		puck = new VirtualPuck(id,sim,"PuckNumeroUno");
		puck2 = new RealPuck(null, id, "first");
		exe = new LogicThread(puck);
		hf = new HandlerFactory();
		receiver = new UUID[1];
		receiver[0] = puck2.getID();
		
	}

	
	public void buildMap() {
		orientation = new TreeMap<UUID, Orientation>();
		orientation.put(id, Orientation.DOWN);
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
	
	public void testSimTurnHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x03;
		req = new VirtualPuckRequest(id, array);
		SimTurnHandler handler = new SimTurnHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testSimStatusHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x02;
		req = new VirtualPuckRequest(id, array);
		SimStatusHandler handler = new SimStatusHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testSimSpeedHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x05;
		req = new VirtualPuckRequest(id, array);
		SimSpeedHandler handler = new SimSpeedHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testSimResetHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x01;
		req = new VirtualPuckRequest(id, array);
		SimResetHandler handler = new SimResetHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testSimMoveHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x04;
		req = new VirtualPuckRequest(id, array);
		SimMoveHandler handler = new SimMoveHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testSimLEDHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x06;
		req = new VirtualPuckRequest(id, array);
		SimLEDHandler handler = new SimLEDHandler(null, sim);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req));
	}
	
	public void testPuckStatusHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x82;
		req2 = new PuckRequest(array, puck2);
		PuckStatusHandler handler = new PuckStatusHandler(null, exe);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testPuckRejectHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x86;
		req2 = new PuckRequest(array, puck2);
		PuckRejectHandler handler = new PuckRejectHandler(null, exe);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testPuckOkHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x81;
		req2 = new PuckRequest(array, puck2);
		PuckOkHandler handler = new PuckOkHandler(null, exe);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testPuckNodeHitHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x83;
		req2 = new PuckRequest(array, puck2);
		PuckNodeHitHandler handler = new PuckNodeHitHandler(null, exe);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testPuckCollisionHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x84;
		req2 = new PuckRequest(array, puck2);
		PuckCollisionHandler handler = new PuckCollisionHandler(null, exe);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testPuckAbyssHandler() {
		array[0] = (byte) 0xFF;
		array[1] = (byte) 0x85;
		req2 = new PuckRequest(array, puck2);
		PuckAbyssHandler handler = new PuckAbyssHandler(null, exe);
		assertTrue(handler.toString() + LOG_MES, handler.handleRequest(req2));
	}
	
	public void testFailureRequestHandler() {
		FailureRequestHandler handler = new FailureRequestHandler(null, exe);
		FailureRequest req4 = new FailureRequest(id, receiver, FailureType.ABYSS);
		assertTrue("SimReset" + LOG_MES, handler.handleRequest(req4));	
	}
	
	public void testDriveRequestHandler() {
		DriveRequestHandler handler = new DriveRequestHandler(null, exe);
		DriveRequest req4 = new DriveRequest(id, receiver, Orientation.LEFT);
		assertTrue("", handler.handleRequest(req4));
	}
	
	public void testControllesRequestHandler() {
		ControlledRequestHandler handler = new ControlledRequestHandler(null, exe);
		ControlledRequest req4 = new ControlledRequest(id, receiver, true);
		assertTrue("", handler.handleRequest(req4));
	}
	
	public void testCollisionRequestHandler() {
		CollisionRequestHandler handler = new CollisionRequestHandler(null, exe);
		boolean[] sensor = {true, false, false, false, false, false, false, false};
		CollisionRequest req4 = new CollisionRequest(id, receiver, sensor);
		assertTrue("", handler.handleRequest(req4));
	}
}
