/** Author: Thomas Riley
 * 	Last Edited: 3/7/2017
 * 
 * 	This is the main gameboard for Clue-Less. It powers both the GUI gameboard as well as
 *   the logical graph behind it all. It is also responsible for instantiating all character
 *    tokens, though this may be moved to the server once serverside work is done.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Gameboard extends JPanel {
	private Room[] rooms; //Array of all rooms
	private Hallway[] hallways; //Array of all hallways
	private Shape[] boardGUI; //Shapes that make up the board GUI
	private BiMap<Shape, Location> roomToLocation; //A map to go from GUI Shapes -> Logical Rooms/Hallways
	private Character activeCharacter; //The current character moving around
	private Socket socket;
	private Token[] tokens;
	private JButton skipTurn = null;
	private JButton suggestion = null;
	private JButton accusation = null;
	private JButton showNotepad = null;
	private ArrayList<JCheckBox> suspects = null;
	private ArrayList<JCheckBox> weapons = null;
	private ArrayList<JCheckBox> locations = null;
	private boolean alreadySuggested = false;
	private boolean alreadyMoved = false;
	private boolean stayedInRoom = false;
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(900, 900);
    }; 
	
	/*
	 * Paints the actual background as well as the character tokens.
	 *  In addition, it builds the hash map that relates logical
	 *   rooms to graphical rooms. This should be moved later.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(activeCharacter != null && activeCharacter.isMyTurn()) {
			skipTurn.setEnabled(true);
			suggestion.setEnabled(!alreadySuggested && activeCharacter.getLocation() instanceof Room && !stayedInRoom);
			accusation.setEnabled(activeCharacter.getLocation() instanceof Room);
		} else {
			skipTurn.setEnabled(false);
			suggestion.setEnabled(false);
			accusation.setEnabled(false);
		}
		
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
			
		//Draw images
			try {
				BufferedImage study = ImageIO.read(new File("Images\\Rooms\\study.jpg"));
				BufferedImage hall = ImageIO.read(new File("Images\\Rooms\\hall.jpg"));
				BufferedImage lounge = ImageIO.read(new File("Images\\Rooms\\lounge.jpg"));
				BufferedImage library = ImageIO.read(new File("Images\\Rooms\\library.jpg"));
				BufferedImage billiard_room = ImageIO.read(new File("Images\\Rooms\\billiard_room.jpg"));
				BufferedImage dining_room = ImageIO.read(new File("Images\\Rooms\\dining_room.jpg"));
				BufferedImage conservatory = ImageIO.read(new File("Images\\Rooms\\conservatory.jpg"));
				BufferedImage ballroom = ImageIO.read(new File("Images\\Rooms\\ballroom.jpg"));
				BufferedImage kitchen = ImageIO.read(new File("Images\\Rooms\\kitchen.jpg"));
				
				BufferedImage horizontal_hallway = ImageIO.read(new File("Images\\Hallways\\horizontal_hallway.jpg"));
				BufferedImage vertical_hallway = ImageIO.read(new File("Images\\Hallways\\vertical_hallway.jpg"));
				
				if(activeCharacter != null) {
					BufferedImage portrait = activeCharacter.getPortrait();
					g.drawImage(portrait, 770, 715, null);	
				}
				
				g.drawImage(horizontal_hallway, 200, 150, null);
				g.drawImage(horizontal_hallway, 450, 150, null);
				g.drawImage(horizontal_hallway, 200, 400, null);
				g.drawImage(horizontal_hallway, 450, 400, null);
				g.drawImage(horizontal_hallway, 200, 650, null);
				g.drawImage(horizontal_hallway, 450, 650, null);
				
				g.drawImage(vertical_hallway, 150, 200, null);
				g.drawImage(vertical_hallway, 400, 200, null);
				g.drawImage(vertical_hallway, 650, 200, null);
				g.drawImage(vertical_hallway, 150, 450, null);
				g.drawImage(vertical_hallway, 400, 450, null);
				g.drawImage(vertical_hallway, 650, 450, null);
				
				g.drawImage(study.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 100, 100, null);
				g.drawImage(hall.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 350, 100, null);
				g.drawImage(lounge.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 600, 100, null);
				g.drawImage(library.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 100, 350, null);
				g.drawImage(billiard_room.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 350, 350, null);
				g.drawImage(dining_room.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 600, 350, null);
				g.drawImage(conservatory.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 100, 600, null);
				g.drawImage(ballroom.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 350, 600, null);
				g.drawImage(kitchen.getScaledInstance(100, 100, Image.SCALE_DEFAULT), 600, 600, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(tokens != null) {
			ArrayList<Token> toBeDrawn = new ArrayList<Token>();
			toBeDrawn.addAll(Arrays.asList(tokens));
			for(Room r : rooms) {
				ArrayList<Token> occupantsToDraw = r.getOccupants();
				Shape roomSquare = roomToLocation.inverse().get(r);
				Shape characterToken = null;
				double newX = roomSquare.getBounds().getCenterX();
				double newY = roomSquare.getBounds().getCenterY();
				for(int i = 0; i < occupantsToDraw.size(); i++) {
					Token occupant = occupantsToDraw.get(i);
					g2d.setColor(occupant.getColor());
					if(i == 0) {
						characterToken = new Ellipse2D.Double(newX - 50, newY - 35, 25, 25);
					} else if(i == 1) {
						characterToken = new Ellipse2D.Double(newX - 20, newY - 35, 25, 25);
					} else if(i == 2) {
						characterToken = new Ellipse2D.Double(newX + 10, newY - 35, 25, 25);
					} else if(i == 3) {
						characterToken = new Ellipse2D.Double(newX - 50, newY + 20, 25, 25);
					} else if(i == 4) {
						characterToken = new Ellipse2D.Double(newX - 20, newY + 20, 25, 25);
					} else if(i == 5) {
						characterToken = new Ellipse2D.Double(newX + 10, newY + 20, 25, 25);
					}
					
					toBeDrawn.remove(occupant);
					g2d.fill(characterToken);
				}
			}
			
			for(Token t : toBeDrawn) {
				double newX = t.getLocation().getBounds().getCenterX();
				double newY = t.getLocation().getBounds().getCenterY();
				Shape characterToken = new Ellipse2D.Double(newX - 10, newY - 10, 25, 25);
				g2d.setColor(t.getColor());
				g2d.fill(characterToken);
			}
			
		}
		
		if(activeCharacter != null && activeCharacter.getColor() != null && activeCharacter.getToken() != null) {
			g2d.setColor(activeCharacter.getColor());
			Shape playerToken = activeCharacter.getToken();
			double newX = playerToken.getBounds().getCenterX();
			double newY = playerToken.getBounds().getCenterY();
			if(activeCharacter.getLocation() instanceof Room) {
				Room currentLoc = (Room) activeCharacter.getLocation();
				int currentOccupants = currentLoc.getOccupants().size();
				
				switch(currentOccupants) {
				case 0:
					playerToken = new Ellipse2D.Double(newX - 50, newY - 35, 25, 25);
					break;
				case 1:
					playerToken = new Ellipse2D.Double(newX - 20, newY - 35, 25, 25);
					break;
				case 2:
					playerToken = new Ellipse2D.Double(newX + 10, newY - 35, 25, 25);
					break;
				case 3:
					playerToken = new Ellipse2D.Double(newX - 50, newY + 20, 25, 25);
					break;
				case 4:
					playerToken = new Ellipse2D.Double(newX - 20, newY + 20, 25, 25);
					break;
				case 5:
					playerToken = new Ellipse2D.Double(newX + 10, newY + 20, 25, 25);
					break;
				}
			} else {
				playerToken = new Ellipse2D.Double(newX - 10, newY - 10, 25, 25);
			}
			
			g2d.fill(playerToken);
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
		Room ballroom = new Room("ballroom room");
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
		
		study.setSecretRoom(kitchen);
		kitchen.setSecretRoom(study);
		lounge.setSecretRoom(conservatory);
		conservatory.setSecretRoom(lounge);
		
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
						if(roomToLocation.get(t.getLocation()) instanceof Room) {
							Room r = (Room) roomToLocation.get(t.getLocation());
							r.removeOccupant(t);
							r.changeOccupants(r.getOccupantNumber() - 1);
						}
						t.setLocation(roomToLocation.inverse().get(l));
						roomToLocation.get(t.getLocation()).setOccupied(true);
						if(roomToLocation.get(t.getLocation()) instanceof Room) {
							((Room) roomToLocation.get(t.getLocation())).addOccupant(t);
							((Room) roomToLocation.get(t.getLocation())).changeOccupants(((Room) roomToLocation.get(t.getLocation())).getOccupantNumber() + 1);
						}
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
		activeCharacter.setToken(pixelLocation);
		repaint();
	}
	
	private void showNotepad() {
		if(suspects == null) {
			suspects = new ArrayList<JCheckBox>();
			weapons = new ArrayList<JCheckBox>();
			locations = new ArrayList<JCheckBox>();
			
			String[] suspectStrings = {"Professor Plum", "Mrs. White", "Mr. Green", "Ms. Scarlet", "Mrs. Peacock", "Colonel Mustard"};
			String[] weaponsStrings = {"Candlestick", "Lead Pipe", "Rope", "Revolver", "Wrench", "Knife"};
			String[] locationStrings = {"Hall", "Ballroom", "Kitchen", "Lounge", "Study", "Conservatory", "Billiard Room", "Library", "Dining Room"};
			
			for(String string : suspectStrings) {
				JCheckBox checkbox = new JCheckBox(string);
				suspects.add(checkbox);
			}
			
			for(String string : weaponsStrings) {
				JCheckBox checkbox = new JCheckBox(string);
				weapons.add(checkbox);
			}
			
			for(String string : locationStrings) {
				JCheckBox checkbox = new JCheckBox(string);
				locations.add(checkbox);
			}
		}
		
		JPanel notepad = new JPanel();
		notepad.setLayout(new GridLayout(24, 1));
		
		JLabel suspectLabel = new JLabel("Suspects:");
		JLabel weaponsLabel = new JLabel("Weapons:");
		JLabel locationsLabel = new JLabel("Locations");
		suspectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		weaponsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		locationsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		
		notepad.add(suspectLabel);
		for(JCheckBox jcb : suspects) {
			notepad.add(jcb);
		}
		notepad.add(weaponsLabel);
		for(JCheckBox jcb : weapons) {
			notepad.add(jcb);
		}
		
		notepad.add(locationsLabel);
		for(JCheckBox jcb : locations) {
			notepad.add(jcb);
		}
		
		JOptionPane.showMessageDialog(this, notepad, null, JOptionPane.OK_OPTION);
	}
	
	/*
	 * Checks to make sure that the character can move there in logical space
	 *  If yes, then it calls the moveToken() method. Otherwise throw error
	 *  
	 *  The exception is if there's a boolean passed in as well. If that happens
	 *  just move them to wherever they're supposed to be for suggestions
	 */
	private void moveCharacter(Location loc, Shape pixelLocation, boolean... override) throws IOException {
			boolean moved = false;
			if(activeCharacter.isMyTurn() || override.length > 0) {
			Location currentLocation = activeCharacter.getLocation();
			Location[] connections = null;
			if(override.length == 0) {
				connections = currentLocation.getConnections();
			} else {
				connections = rooms;
			}
			for(int i = 0; i < connections.length; i++) {
				if((connections[i].equals(loc) || override.length > 0) && !alreadyMoved) {
					if(loc.isOccupied() && loc instanceof Hallway) {
						System.out.println("This room is already occupied!");
					} else {
						activeCharacter.getLocation().setOccupied(false);
						activeCharacter.move(loc);
						activeCharacter.getLocation().setOccupied(true);
						moved = true;
						moveToken(pixelLocation);
						PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
						pw.println("MOVE" + activeCharacter.getName() + ";" + loc.getName());
						if(override.length == 0) {
							alreadyMoved = true;
							stayedInRoom = false;
						} else {
							stayedInRoom = false;
						}
						break;
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
	
	public void showSuggestionAccusation(String suggestionAccusation) {
		JPanel suggestion = new JPanel();
		suggestion.setLayout(new GridLayout(8, 2));
		
		JLabel selectWeapon = new JLabel("Weapon:");
		JLabel selectSuspect = new JLabel("Suspect:");
		
		JRadioButton rope = new JRadioButton("Rope");
		JRadioButton candlestick = new JRadioButton("Candlestick");
		JRadioButton pipe = new JRadioButton("Pipe");
		JRadioButton knife = new JRadioButton("Knife");
		JRadioButton revolver = new JRadioButton("Revolver");
		JRadioButton wrench = new JRadioButton("Wrench");
		
		rope.setActionCommand("Rope");
		candlestick.setActionCommand("Candlestick");
		pipe.setActionCommand("Pipe");
		knife.setActionCommand("Knife");
		revolver.setActionCommand("Revolver");
		wrench.setActionCommand("Wrench");
		
		JRadioButton white = new JRadioButton("Mrs. White");
		JRadioButton green = new JRadioButton("Mr. Green");
		JRadioButton scarlet = new JRadioButton("Miss Scarlet");
		JRadioButton plum = new JRadioButton("Professor Plum");
		JRadioButton mustard = new JRadioButton("Colonel Mustard");
		JRadioButton peacock = new JRadioButton("Mrs. Peacock");
		
		white.setActionCommand("Mrs. White");
		green.setActionCommand("Mr. Green");
		scarlet.setActionCommand("Miss Scarlet");
		plum.setActionCommand("Professor Plum");
		mustard.setActionCommand("Colonel Mustard");
		peacock.setActionCommand("Mrs. Peacock");
		
		ButtonGroup weapons = new ButtonGroup();
		
		weapons.add(rope);
		weapons.add(candlestick);
		weapons.add(pipe);
		weapons.add(knife);
		weapons.add(revolver);
		weapons.add(wrench);
		
		ButtonGroup suspects = new ButtonGroup();
		
		suspects.add(white);
		suspects.add(green);
		suspects.add(scarlet);
		suspects.add(plum);
		suspects.add(mustard);
		suspects.add(peacock);
		
		suggestion.add(selectWeapon);
		suggestion.add(selectSuspect);
		
		suggestion.add(rope);
		suggestion.add(white);
		
		suggestion.add(candlestick);
		suggestion.add(green);
		
		suggestion.add(pipe);
		suggestion.add(scarlet);
		
		suggestion.add(knife);
		suggestion.add(plum);
		
		suggestion.add(revolver);
		suggestion.add(mustard);
		
		suggestion.add(wrench);
		suggestion.add(peacock);
		
		
		int option = JOptionPane.showConfirmDialog(this, suggestion, null, JOptionPane.YES_NO_OPTION);
		
		if(option == JOptionPane.YES_OPTION) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(suggestionAccusation + activeCharacter.getLocation().getName() + ";" + weapons.getSelection().getActionCommand() + ";" + suspects.getSelection().getActionCommand() + ";" + activeCharacter.getName());
				alreadySuggested = true;
				suggestion.setEnabled(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * Constructor. Calls for graph set up and adds a mouse listener
	 */
	public Gameboard() {
		setUpGraph();
		roomToLocation = null;
		boardGUI = null;		
		
		if(showNotepad == null) {
			showNotepad = new JButton("Show Notepad");
			showNotepad.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showNotepad();
				}
			});
		}
		
		if(suggestion == null) {
			suggestion = new JButton("Make a Suggestion");
			suggestion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(activeCharacter.isMyTurn() && alreadySuggested == false) {
						showSuggestionAccusation("SUGGESTION");
					}
				}
			});
		}
		
		if(accusation == null) {
			accusation = new JButton("Make an Accusation");
			accusation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(activeCharacter.isMyTurn()) {
						showSuggestionAccusation("ACCUSATION");
					}
				}
			});
		}

		if(skipTurn == null) {
			skipTurn = new JButton("End Turn");
			skipTurn.addActionListener(new ActionListener()
			{
			  public void actionPerformed(ActionEvent e)
			  {
				  try {
					  if(activeCharacter != null && activeCharacter.isMyTurn()) {
						  PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
							pw.println("SKIP");
					  }
					  
					  if(activeCharacter.getLocation() instanceof Room) {
						  stayedInRoom = true;
					  } else {
						  stayedInRoom = false;
					  }
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }
			});
		}
		
		accusation.setBounds(725, 570, 150, 40);
		suggestion.setBounds(725, 610, 150, 40);
		showNotepad.setBounds(725,530, 150, 40);
		skipTurn.setBounds(725, 650, 150, 40);
		this.add(suggestion);
		this.add(skipTurn);
		this.add(accusation);
		this.add(showNotepad);
		accusation.setEnabled(false);
		suggestion.setEnabled(false);
		skipTurn.setEnabled(false);
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

	public void serverCommand(String command) throws IOException {
		if(command.startsWith("MOVE")) {
			command = command.substring(4);
			System.out.println("Move string:" + command);
			String[] moveCommand = command.split(";");
			if(moveCommand[0].equals(activeCharacter.getName()) && !activeCharacter.getLocation().getName().equals(moveCommand[1])) {
				System.out.println("Active character being moved");
				for(int i = 0; i < rooms.length; i++) {
					if(rooms[i].getName().equals(moveCommand[1])) {
						System.out.println("Found room");
						moveCharacter(rooms[i], roomToLocation.inverse().get(rooms[i]), true);
						break;
					}
				}
			} else {
				moveNonPlayerToken(moveCommand[0], moveCommand[1]);
			}
		} else if(command.startsWith("WIN")) {
			command = command.substring(3);
			if(command.equalsIgnoreCase(activeCharacter.getName())) {
				JOptionPane.showMessageDialog(this, "Congratulations! That was the correct accusation! You win!");
			} else {
				JOptionPane.showMessageDialog(this, command + " correctly guessed the murder!");
			}
		
		} else if(command.startsWith("LOSE")) {
			command = command.substring(4);
			
			if(command.equalsIgnoreCase(activeCharacter.getName())) {
				JOptionPane.showMessageDialog(this, "That was not the correct accusation. You lose.");
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println("SKIP");
			} else {
				JOptionPane.showMessageDialog(this, command + " did not correctly guess the murder, and has lost.");
			}
		} else if(command.startsWith("NEWTURN")) {
		
			command = command.substring(7);
			if(command.equals(activeCharacter.getName())) {
				activeCharacter.setTurn(true);
				alreadySuggested = false;
				alreadyMoved = false;
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
				activeCharacter = new Character("Professor Plum", hallways[8], ImageIO.read(new File("Images\\Characters\\professor_plum.png")));
				initiateToken(boardGUI[15].getBounds().getCenterX(), boardGUI[15].getBounds().getCenterY(), activeCharacter, new Color(255, 0, 255));
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[14], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[13], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[10], "Miss Scarlet");
				tokens = new Token[]{cm, mw, mg, mp, ms};
				repaint();
				break;
			case "Colonel Mustard":
				activeCharacter = new Character("Colonel Mustard", hallways[2], ImageIO.read(new File("Images\\Characters\\colonel_mustard.png")));
				initiateToken(boardGUI[17].getBounds().getCenterX(), boardGUI[17].getBounds().getCenterY(), activeCharacter, Color.YELLOW);
				pp = new Token(new Color(255, 0, 255), boardGUI[15], "Professor Plum");
				mw = new Token(Color.WHITE, boardGUI[14], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[13], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[10], "Miss Scarlet");
				tokens = new Token[]{pp, mw, mg, mp, ms};
				repaint();
				break;
			case "Mrs. White":
				activeCharacter = new Character("Mrs. White", hallways[7], ImageIO.read(new File("Images\\Characters\\mrs_white.png")));
				initiateToken(boardGUI[14].getBounds().getCenterX(), boardGUI[14].getBounds().getCenterY(), activeCharacter, Color.WHITE);
				pp = new Token(new Color(255, 0, 255), boardGUI[15], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mg = new Token(Color.GREEN, boardGUI[13], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[10], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mg, mp, ms};
				repaint();
				break;
			case "Mr. Green":
				activeCharacter = new Character("Mr. Green", hallways[6], ImageIO.read(new File("Images\\Characters\\mr_green.png")));
				initiateToken(boardGUI[13].getBounds().getCenterX(), boardGUI[13].getBounds().getCenterY(), activeCharacter, Color.GREEN);
				pp = new Token(new Color(255, 0, 255), boardGUI[15], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[14], "Mrs. White");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				ms = new Token(Color.RED, boardGUI[10], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mw, mp, ms};
				repaint();
				break;
			case "Mrs. Peacock":
				activeCharacter = new Character("Mrs. Peacock", hallways[5], ImageIO.read(new File("Images\\Characters\\mrs_peacock.png")));
				initiateToken(boardGUI[18].getBounds().getCenterX(), boardGUI[18].getBounds().getCenterY(), activeCharacter, Color.BLUE);
				pp = new Token(new Color(255, 0, 255), boardGUI[15], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[14], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[13], "Mr. Green");
				ms = new Token(Color.RED, boardGUI[10], "Miss Scarlet");
				tokens = new Token[]{pp, cm, mw, mg, ms};
				repaint();
				break;
			case "Miss Scarlet":
				activeCharacter = new Character("Miss Scarlet", hallways[1], ImageIO.read(new File("Images\\Characters\\ms_scarlet.png")));
				initiateToken(boardGUI[10].getBounds().getCenterX(), boardGUI[10].getBounds().getCenterY(), activeCharacter, Color.RED);
				pp = new Token(new Color(255, 0, 255), boardGUI[15], "Professor Plum");
				cm = new Token(Color.YELLOW, boardGUI[17], "Colonel Mustard");
				mw = new Token(Color.WHITE, boardGUI[14], "Mrs. White");
				mg = new Token(Color.GREEN, boardGUI[13], "Mr. Green");
				mp = new Token(Color.BLUE, boardGUI[18], "Mrs. Peacock");
				tokens = new Token[]{pp, cm, mw, mg, mp};
				repaint();
				break;
			}
		}
	}
	
	
}
