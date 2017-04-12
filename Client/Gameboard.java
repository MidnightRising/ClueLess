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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.swing.*;

public class Gameboard extends JPanel {
	private Room[] rooms; //Array of all rooms
	private Hallway[] hallways; //Array of all hallways
	private Shape[] boardGUI; //Shapes that make up the board GUI
	private BiMap<Shape, Location> roomToLocation; //A map to go from GUI Shapes -> Logical Rooms/Hallways
	private Character activeCharacter; //The current character moving around
	private Socket socket;
	private Token[] tokens;
	
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
		
		Graphics2D g2d = (Graphics2D) g;
		
		if(boardGUI == null) {
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
			
		}
			
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
		
		if(activeCharacter != null && activeCharacter.getColor() != null && activeCharacter.getToken() != null) {
			g2d.setColor(activeCharacter.getColor());
			g2d.fill(activeCharacter.getToken());	
		}
		
		if(tokens != null) {
			for(Token t : tokens) {
				g2d.setColor(t.getColor());
				Shape s = new Ellipse2D.Double(t.getLocation().getBounds().getCenterX() - 10, t.getLocation().getBounds().getCenterY() - 10, 25, 25);
				g2d.fill(s);
			}
		}
		
		if(roomToLocation == null) {
			roomToLocation = HashBiMap.create();
			
			roomToLocation.put(boardGUI[2], rooms[0]);
			roomToLocation.put(boardGUI[1], rooms[1]);
			roomToLocation.put(boardGUI[0], rooms[2]);
			roomToLocation.put(boardGUI[3], rooms[3]);
			roomToLocation.put(boardGUI[4], rooms[4]);
			roomToLocation.put(boardGUI[5], rooms[5]);
			roomToLocation.put(boardGUI[8], rooms[6]);
			roomToLocation.put(boardGUI[7], rooms[7]);
			roomToLocation.put(boardGUI[6], rooms[8]);
			
			roomToLocation.put(boardGUI[9], hallways[0]);
			roomToLocation.put(boardGUI[10], hallways[1]);
			roomToLocation.put(boardGUI[17], hallways[2]);
			roomToLocation.put(boardGUI[12], hallways[3]);
			roomToLocation.put(boardGUI[11], hallways[4]);
			roomToLocation.put(boardGUI[18], hallways[5]);
			roomToLocation.put(boardGUI[13], hallways[6]);
			roomToLocation.put(boardGUI[14], hallways[7]);
			roomToLocation.put(boardGUI[15], hallways[8]);
			roomToLocation.put(boardGUI[16], hallways[9]);
			roomToLocation.put(boardGUI[19], hallways[10]);
			roomToLocation.put(boardGUI[20], hallways[11]);
						
			roomToLocation.get(boardGUI[10]).setOccupied(true);
			roomToLocation.get(boardGUI[17]).setOccupied(true);
			roomToLocation.get(boardGUI[12]).setOccupied(true);
			roomToLocation.get(boardGUI[11]).setOccupied(true);
			roomToLocation.get(boardGUI[18]).setOccupied(true);
			roomToLocation.get(boardGUI[13]).setOccupied(true);
		}
		
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
	
	/*
	 * Instantiates player tokens, allowing graphical movement around the board.
	 */
	public void initiateToken(double x, double y, Character character, Color color) {
		Shape characterToken = new Ellipse2D.Double(x - 10, y - 10, 25, 25);
		character.setToken(characterToken);
		character.setColor(color);
		repaint();
	}
	
	private void moveNonPlayerToken(String name, String location) {
		for(Token t : tokens) {
			if(t.getName().equals(name)) {
				ArrayList<Location> allLocations = new ArrayList<Location>();
				allLocations.addAll(Arrays.asList(hallways));
				allLocations.addAll(Arrays.asList(rooms));
				for(Location l : allLocations) {
					if(l.getName().equals(location)) {
						roomToLocation.get(t.getLocation()).setOccupied(false);
						t.setLocation(roomToLocation.inverse().get(l));
						roomToLocation.get(t.getLocation()).setOccupied(true);
						break;
					}
				}
				repaint();
				break;
			}			
		}
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
	private void moveCharacter(Location loc, Shape pixelLocation) throws IOException {
			boolean moved = false;
			if(activeCharacter.isMyTurn()) {
			Location currentLocation = activeCharacter.getLocation();
			Location[] connections = currentLocation.getConnections();
			for(int i = 0; i < connections.length; i++) {
				if(connections[i].equals(loc)) {
					if(loc.isOccupied()) {
						System.out.println("This room is already occupied!");
					} else {
						activeCharacter.getLocation().setOccupied(false);
						activeCharacter.move(loc);
						activeCharacter.getLocation().setOccupied(true);
						moved = true;
						moveToken(pixelLocation);
						PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
						pw.println("MOVE" + activeCharacter.getName() + ";" + loc.getName());
					}
				}
			}
		
			if(moved == false) {
				System.out.println("Cannot move character to that room.");
			}
		} else {
			System.out.println("It's not my turn!");
		}
	}
	
	/*
	 * Constructor. Calls for graph set up and adds a mouse listener
	 */
	public Gameboard() {
		setUpGraph();
		roomToLocation = null;
		boardGUI = null;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
					for(int i = 0; i < boardGUI.length; i++) {
						if(boardGUI[i].contains(e.getPoint()) || boardGUI[i].intersects(e.getX() - 5, e.getY() - 5, 15, 15)) {
							try {
								moveCharacter(roomToLocation.get(boardGUI[i]), boardGUI[i]);
							} catch (IOException e1) {
								//TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
		});
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void serverCommand(String command) {
		if(command.startsWith("MOVE")) {
			command = command.substring(4);
			String[] moveCommand = command.split(";");
			moveNonPlayerToken(moveCommand[0], moveCommand[1]);
		} else if(command.startsWith("NEWTURN")) {
			command = command.substring(7);
			if(command.equals(activeCharacter.getName())) {
				activeCharacter.setTurn(true);
				ClueLess.setLabel("It's your turn!");
			} else {
				activeCharacter.setTurn(false);
				ClueLess.setLabel("It's " + command + "\'s turn");
			}
		} else if(command.startsWith("ASSIGNED")) {
			String character = command.substring(8);
			Token pp;
			Token cm;
			Token mw;
			Token mg;
			Token mp;
			Token ms;
			
			switch(character) {
			case "Professor Plum":
				activeCharacter = new Character("Professor Plum", hallways[1]);
				initiateToken(boardGUI[10].getBounds().getCenterX(), boardGUI[10].getBounds().getCenterY(), activeCharacter, new Color(255, 0, 255));
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[12], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[11], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[13], "Miss Scarlet");
				tokens = new Token[]{cm, mw, mg, mp, ms};
				repaint();
				break;
			case "Colonel Mustard":
				activeCharacter = new Character("Colonel Mustard", hallways[2]);
				initiateToken(boardGUI[17].getBounds().getCenterX(), boardGUI[17].getBounds().getCenterY(), activeCharacter, Color.YELLOW);
				pp = new Token(new Color(255, 0, 255), boardGUI[10], "Professor Plum");
				mw = new Token(Color.WHITE, boardGUI[12], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[11], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[13], "Miss Scarlet");
				tokens = new Token[]{pp, mw, mg, mp, ms};
				repaint();
				break;
			case "Mrs. White":
				activeCharacter = new Character("Mrs. White", hallways[3]);
				initiateToken(boardGUI[12].getBounds().getCenterX(), boardGUI[12].getBounds().getCenterY(), activeCharacter, Color.WHITE);
				pp = new Token(new Color(255, 0, 255), boardGUI[10], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mg = new Token(Color.GREEN, boardGUI[11], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[13], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mg, mp, ms};
				repaint();
				break;
			case "Mr. Green":
				activeCharacter = new Character("Mr. Green", hallways[4]);
				initiateToken(boardGUI[11].getBounds().getCenterX(), boardGUI[11].getBounds().getCenterY(), activeCharacter, Color.GREEN);
				pp = new Token(new Color(255, 0, 255), boardGUI[10], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[12], "Mrs. White");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[13], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mw, mp, ms};
				repaint();
				break;
			case "Mrs. Peacock":
				activeCharacter = new Character("Mrs. Peacock", hallways[5]);
				initiateToken(boardGUI[18].getBounds().getCenterX(), boardGUI[18].getBounds().getCenterY(), activeCharacter, Color.BLUE);
				pp = new Token(new Color(255, 0, 255), boardGUI[10], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[12], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[11], "Mr. Green");
				ms = new Token(Color.RED, boardGUI[13], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mw, mg, ms};
				repaint();
				break;
			case "Miss Scarlet":
				activeCharacter = new Character("Miss Scarlet", hallways[6]);
				initiateToken(boardGUI[13].getBounds().getCenterX(), boardGUI[13].getBounds().getCenterY(), activeCharacter, Color.RED);
				pp = new Token(new Color(255, 0, 255), boardGUI[10], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[12], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[11], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				tokens = new Token[]{pp, cm, mw, mg, mp};
				repaint();
				break;
			}
		}
	}
	
	
}
