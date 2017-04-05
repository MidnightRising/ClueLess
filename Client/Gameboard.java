/** Author: Thomas Riley
 * 	Last Edited: 3/7/2017
 * 
 * 	This is the main gameboard for Clue-Less. It powers both the GUI gameboard as well as
 *   the logical graph behind it all. It is also responsible for instantiating all character
 *    tokens, though this may be moved to the server once serverside work is done.
 */

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.*;

public class Gameboard extends JPanel {
	private Room[] rooms; //Array of all rooms
	private Hallway[] hallways; //Array of all hallways
	private Shape[] boardGUI; //Shapes that make up the board GUI
	private HashMap<Shape, Location> roomToLocation; //A map to go from GUI Shapes -> Logical Rooms/Hallways
	private Character activeCharacter; //The current character moving around
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 900);
    }; 
	
	/*
	 * Paints the actual background as well as the character tokens.
	 *  In addition, it builds the hash map that relates logical
	 *   rooms to graphical rooms. This should be moved later.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Shape lounge = new Rectangle2D.Double(600, 100, 100, 100); //Lounge
		Shape hall = new Rectangle2D.Double(350, 100, 100, 100); //Hall
		Shape study = new Rectangle2D.Double(100, 100, 100, 100); //Study
		Shape diningroom = new Rectangle2D.Double(600, 350, 100, 100); //Dining Room
		Shape billiard = new Rectangle2D.Double(350, 350, 100, 100); //Billiard
		Shape library = new Rectangle2D.Double(100, 350, 100, 100); //Library
		Shape kitchen = new Rectangle2D.Double(600, 600, 100, 100); //Kitchen
		Shape ballroom = new Rectangle2D.Double(350, 600, 100, 100); //Ballroom
		Shape conservatory = new Rectangle2D.Double(100, 600, 100, 100); //Conservatory
		
		Shape study_hall = new Line2D.Double(200, 150, 350, 150);
		Shape hall_lounge = new Line2D.Double(600, 150, 450, 150);
		Shape library_billiard = new Line2D.Double(200, 400, 350, 400);
		Shape billiard_diningroom = new Line2D.Double(450, 400, 600, 400);
		Shape conservatory_ballroom = new Line2D.Double(200, 650, 350, 650);
		Shape ballroom_kitchen = new Line2D.Double(450, 650, 600, 650);
		Shape study_library = new Line2D.Double(150, 200, 150, 350);
		Shape hall_billiard = new Line2D.Double(400, 200, 400, 350);
		Shape lounge_diningroom = new Line2D.Double(650, 200, 650, 350);
		Shape library_conservatory = new Line2D.Double(150, 450, 150, 600);
		Shape billiard_ballroom = new Line2D.Double(400, 450, 400, 600);
		Shape diningroom_kitchen = new Line2D.Double(650, 450, 650, 600);
		
		
		
		
		boardGUI = new Shape[]{lounge, hall, study, diningroom, billiard, library, kitchen, ballroom, conservatory, study_hall, hall_lounge, 
				library_billiard, billiard_diningroom, conservatory_ballroom, ballroom_kitchen, study_library, hall_billiard, 
				lounge_diningroom, library_conservatory, billiard_ballroom, diningroom_kitchen};
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setFont(new Font("Arial", Font.BOLD, 14));
		
		g2d.drawString("Study", 125, 120);
		g2d.drawString("Hall", 375, 120);
		g2d.drawString("Lounge", 625, 120);
		g2d.drawString("Library", 125, 385);
		g2d.drawString("Billiards", 375, 385);
		g2d.drawString("Dining", 625, 385);
		g2d.drawString("Conservatory", 100, 635);
		g2d.drawString("Ballroom", 375, 635);
		g2d.drawString("Kitchen", 625, 635);
		for(int i = 0; i < boardGUI.length; i++) {
			if(i > 8) {
				g2d.setStroke(new BasicStroke(5));
			}
			g2d.draw(boardGUI[i]);
		}
		
		g2d.setColor(activeCharacter.getColor());
		g2d.fill(activeCharacter.getToken());
		
		roomToLocation = new HashMap<Shape, Location>();
		
		roomToLocation.put(study, rooms[0]);
		roomToLocation.put(hall, rooms[1]);
		roomToLocation.put(lounge, rooms[2]);
		roomToLocation.put(diningroom, rooms[3]);
		roomToLocation.put(billiard, rooms[4]);
		roomToLocation.put(library, rooms[5]);
		roomToLocation.put(conservatory, rooms[6]);
		roomToLocation.put(ballroom, rooms[7]);
		roomToLocation.put(kitchen, rooms[8]);
		
		roomToLocation.put(study_hall, hallways[0]);
		roomToLocation.put(hall_lounge, hallways[1]);
		roomToLocation.put(lounge_diningroom, hallways[2]);
		roomToLocation.put(billiard_diningroom, hallways[3]);
		roomToLocation.put(library_billiard, hallways[4]);
		roomToLocation.put(library_conservatory, hallways[5]);
		roomToLocation.put(conservatory_ballroom, hallways[6]);
		roomToLocation.put(ballroom_kitchen, hallways[7]);
		roomToLocation.put(study_library, hallways[8]);
		roomToLocation.put(hall_billiard, hallways[9]);
		roomToLocation.put(billiard_ballroom, hallways[10]);
		roomToLocation.put(diningroom_kitchen, hallways[11]);
		
	}
	
	/*
	 * Sets up the logical graph powering the GUI
	 */
	private void setUpGraph() {		
		Room kitchen = new Room("kitchen");
		Room diningroom = new Room("dining room");
		Room lounge = new Room("lounge");
		Room hall = new Room("hall");
		Room billiard = new Room("billiard");
		Room ballroom = new Room("ballroom");
		Room conservatory = new Room("conservatory");
		Room library = new Room("library");
		Room study = new Room("study");
		
		Hallway studyToHall = new Hallway("studyToHall", new Room[]{study, hall});
		Hallway hallToLounge = new Hallway("hallToLounge", new Room[]{hall, lounge});
		Hallway loungeToDiningRoom = new Hallway("loungeToDiningRoom", new Room[]{lounge, diningroom});
		Hallway diningRoomtoBilliards = new Hallway("diningRoomToBilliards", new Room[]{diningroom, billiard});
		Hallway billiardToLibrary = new Hallway("billiardToLibrary", new Room[]{billiard, library});
		Hallway libraryToConservatory = new Hallway("libraryToConservatory", new Room[]{library, conservatory});
		Hallway conservatoryToBallroom = new Hallway("conservatoryToBallroom", new Room[]{conservatory, ballroom});
		Hallway ballroomToKitchen = new Hallway("ballroomToConservatory", new Room[]{ballroom, kitchen});
		Hallway studyToLibrary = new Hallway("studyToLibrary", new Room[]{study, library});
		Hallway hallToBilliard = new Hallway("halltoBilliard", new Room[]{hall, billiard});
		Hallway billiardToBallroom = new Hallway("billiardToBallroom", new Room[]{billiard, ballroom});
		Hallway diningroomToKitchen = new Hallway("diningroomToKitchen", new Room[]{diningroom, kitchen});
		
		study.setHallways(new Hallway[]{studyToHall, studyToLibrary});
		hall.setHallways(new Hallway[]{studyToHall, hallToLounge, hallToBilliard});
		lounge.setHallways(new Hallway[]{hallToLounge, loungeToDiningRoom});
		diningroom.setHallways(new Hallway[]{loungeToDiningRoom, diningroomToKitchen, diningRoomtoBilliards});
		billiard.setHallways(new Hallway[]{hallToBilliard, diningRoomtoBilliards, billiardToLibrary, billiardToBallroom});
		library.setHallways(new Hallway[]{studyToLibrary, billiardToLibrary, libraryToConservatory});
		conservatory.setHallways(new Hallway[]{libraryToConservatory, conservatoryToBallroom});
		ballroom.setHallways(new Hallway[]{conservatoryToBallroom, billiardToBallroom, ballroomToKitchen});
		kitchen.setHallways(new Hallway[]{ballroomToKitchen, diningroomToKitchen});
		
		this.rooms = new Room[]{study, hall, lounge, diningroom, billiard, library, conservatory, ballroom, kitchen};
		this.hallways = new Hallway[]{studyToHall, hallToLounge, loungeToDiningRoom, diningRoomtoBilliards, billiardToLibrary, libraryToConservatory, conservatoryToBallroom, ballroomToKitchen, studyToLibrary, hallToBilliard, billiardToBallroom, diningroomToKitchen};
		
	}

	public void setActiveCharacter(Character active) {
		activeCharacter = active;
		initiateToken(650, 275, activeCharacter, Color.YELLOW);
	}
	
	/*
	 * Instantiates player tokens, allowing graphical movement around the board.
	 */
	public void initiateToken(double x, double y, Character character, Color color) {
		Shape characterToken = new Ellipse2D.Double(x - 10, y - 10, 25, 25);
		
		character.setToken(characterToken);
		character.setColor(color);
		repaint();
	}
	
	/*
	 * Moves token around the GUI board
	 */
	private void moveToken(Shape pixelLocation) {
		double newX = pixelLocation.getBounds().getCenterX();
		double newY = pixelLocation.getBounds().getCenterY();
		
		Shape characterToken = new Ellipse2D.Double(newX - 10, newY - 10, 25, 25);
		activeCharacter.setToken(characterToken);
		repaint();
	}
	
	/*
	 * Checks to make sure that the character can move there in logical space
	 *  If yes, then it calls the moveToken() method. Otherwise throw error
	 */
	private void moveCharacter(Location loc, Shape pixelLocation) {
		boolean moved = false;
		Location currentLocation = activeCharacter.getLocation();
		Location[] connections = currentLocation.getConnections();
		for(int i = 0; i < connections.length; i++) {
			if(connections[i].equals(loc)) {
				activeCharacter.move(loc);
				moved = true;
				moveToken(pixelLocation);
			}
		}
		
		if(moved == false) {
			System.out.println("Cannot move character to that room.");
		}
			
		
	}
	
	/*
	 * Constructor. Calls for graph set up and adds a mouse listener
	 */
	public Gameboard() {
		setUpGraph();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				

				super.mouseClicked(e);
				for(int i = 0; i < boardGUI.length; i++) {
					if(boardGUI[i].contains(e.getPoint()) || boardGUI[i].intersects(e.getX() - 5, e.getY() - 5, 15, 15)) {
						moveCharacter(roomToLocation.get(boardGUI[i]), boardGUI[i]);
					}
				}
			}
		});
	}
	
	//Quick way to set the starting position for yellow
	public Location getStartingLocation() {
		return this.hallways[2];
	}
	
	
}
