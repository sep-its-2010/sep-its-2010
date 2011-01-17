package sep.conquest.model.handler.test;

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
	
	
	public void setUp(){
		buildMap();
		
		//sim = new Simulator();
		puck = new VirtualPuck(id,sim,"");
		exe = new LogicThread(puck);
		hf = new HandlerFactory();
	}
	
	public void buildMap() {
		
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
