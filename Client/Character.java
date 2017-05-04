/*
 * Keeps track of everything important to the playable character, including location
 */

import java.awt.*;
import java.awt.image.BufferedImage;

public class Character {
	private String name;
	private Location location;
	private Shape token;
	private Color color;
	private boolean myTurn = false;
	private BufferedImage portrait;
	
	public Character(String name, Location location, BufferedImage portrait) {
		this.name = name;
		this.location = location;
		this.portrait = portrait;
	}
	
	public BufferedImage getPortrait() {
		return portrait;
	}
	
	public void setTurn(Boolean turn) {
		myTurn = turn;
	}
	
	public boolean isMyTurn() {
		return myTurn;
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
	
	public String getName() {
		return name;
	}
}
