/*
 * It's a room connected by Hallways.
 */

public class Room extends Location {
	private String name;
	private Hallway[] hallways;
	
	public Room(String name) {
		this.name = name;
	}
	
	public void setHallways(Hallway[] hallways) {
		this.hallways = hallways;
	}
	
	public String getName() {
		return name;
	}
	
	public Hallway[] getConnections() {
		return hallways;
	}
}
