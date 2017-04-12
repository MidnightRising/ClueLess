/*
 * A really quick superclass for rooms and hallways. Allows for
 *  generic calls to find out what's next to a player.
 */
public class Location {
	
	public String getName() {
		return "";
	}
	
	public Location[] getConnections() {
		return new Location[]{};
	};
	
	public boolean isOccupied() {
		return true;
	}
	
	public void setOccupied(boolean isOccupied) {}
}
