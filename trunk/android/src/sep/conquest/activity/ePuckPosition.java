package sep.conquest.activity;

public class ePuckPosition {
	
	private int x;
	private int y;
	private int id;
	
	public ePuckPosition(int _x, int _y, int _id) {
		x = _x;
		y = _y;
		id = _id;
	}
	
	public void setX(int _x) {
		x = _x;
	}
	
	public void setY(int _y) {
		y = _y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getID() {
		return id;
	}

}

