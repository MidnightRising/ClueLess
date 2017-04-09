import java.awt.Color;
import java.awt.Shape;

public class Token {
	private Color color;
	private Shape location;
	private String name;
	
	public Token(Color color, Shape location, String name) {
		this.color = color;
		this.location = location;
		this.name = name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Shape getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLocation(Shape location) {
		this.location = location;
	}
}
