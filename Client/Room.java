import java.util.ArrayList;

/*
 * It's a room connected by Hallways.
 */

public class Room extends Location {
	private String name;
	private Hallway[] hallways;
	private boolean isOccupied;
	private int numOccupants;
	private ArrayList<Token> occupants;
	
	public Room(String name) {
		this.name = name;
		isOccupied = false;
		numOccupants = 0;
		occupants = new ArrayList<Token>();
	}
	
	public void addOccupant(Token occupant) {
		occupants.add(occupant);
	}
	
	public void removeOccupant(Token occupant) {
		occupants.remove(occupant);
	}
	
	public ArrayList<Token> getOccupants() {
		return occupants;
	}
	
	public void changeOccupants(int occupants) {
		numOccupants = occupants;
	}
	
	public int getOccupantNumber() {
		return numOccupants;
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
