/*
 * Hallway connecting two rooms.
 */

public class Hallway extends Location {
	private String name;
	private Room[] rooms;
	private boolean isOccupied;
	
	public Hallway(String name, Room[] rooms) {
		this.name = name;
		this.rooms = rooms;
		isOccupied = false;
	}
	
	public String getName() {
		return name;
	}
	
	public Room[] getConnections() {
		return rooms;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
}
