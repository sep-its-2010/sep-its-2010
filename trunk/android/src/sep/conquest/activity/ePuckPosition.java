package sep.conquest.activity;

import java.util.UUID;

public class ePuckPosition {
	
  private final UUID ROBOT_ID;

	private int posX;
	private int posY;
	
	public ePuckPosition(int x, int y, UUID ID) {
		posX = x;
		posY = y;
		ROBOT_ID = ID;
	}
	
	public void setX(int x) {
		posX = x;
	}
	
	public void setY(int y) {
		posY = y;
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}
	
	public UUID getID() {
		return ROBOT_ID;
	}
}

