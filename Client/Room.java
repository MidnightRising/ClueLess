/*
 * It's a room connected by Hallways.
 */

public class Room extends Location {
	private String name;
	private Hallway[] hallways;
	private boolean isOccupied;
	
	public Room(String name) {
		this.name = name;
		isOccupied = false;
	}
	
	public void setHallways(Hallway[] hallways) {
		this.hallways = hallways;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	
	public String getName() {
		return name;
	}
	
	public Hallway[] getConnections() {
		return hallways;
	}
}
