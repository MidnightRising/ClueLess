/*
 * Hallway connecting two rooms.
 */

public class Hallway extends Location {
	private String name;
	private Room[] rooms;
	
	public Hallway(String name, Room[] rooms) {
		this.name = name;
		this.rooms = rooms;
	}
	
	public String getName() {
		return name;
	}
	
	public Room[] getConnections() {
		return rooms;
	}
	
	
}
