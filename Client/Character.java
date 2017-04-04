/*
 * Keeps track of everything important to each character, including location
 */

import java.awt.*;

public class Character {
	private String name;
	private Location location;
	private Shape token;
	private Color color;
	
	public Character(String name, Location location) {
		this.name = name;
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void move(Location loc) {
		this.location = loc;
	}
	
	public Shape getToken() {
		return token;
	}
	
	public void setToken(Shape shape) {
		token = shape;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
}
