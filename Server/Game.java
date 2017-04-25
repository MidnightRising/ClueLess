import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Game {
	private Card[] winningGuess;
	public int currentTurn;
	
	public Game() {
		setup();
		currentTurn = 0;
	}
	
	private void setup() {
		ArrayList<Card> weapons = new ArrayList<Card>();
		ArrayList<Card> suspects = new ArrayList<Card>();
		ArrayList<Card> locations = new ArrayList<Card>();
		ArrayList<Card> finalDeck = new ArrayList<Card>();

		weapons.add(new Card("Candlestick", "Weapon"));
		weapons.add(new Card("Knife", "Weapon"));
		weapons.add(new Card("Lead Pipe", "Weapon"));
		weapons.add(new Card("Revolver", "Weapon"));
		weapons.add(new Card("Rope", "Weapon"));
		weapons.add(new Card("Wrench", "Weapon"));
		
		suspects.add(new Card("Colonel Mustard", "Suspect"));
		suspects.add(new Card("Miss Scarlet", "Suspect"));
		suspects.add(new Card("Professor Plum", "Suspect"));
		suspects.add(new Card("Mr. Green", "Suspect"));
		suspects.add(new Card("Mrs. White", "Suspect"));
		suspects.add(new Card("Mrs. Peacock", "Suspect"));
		
		locations.add(new Card("Hall", "Location"));
		locations.add(new Card("Dining Room", "Location"));
		locations.add(new Card("Kitchen", "Location"));
		locations.add(new Card("Ballroom", "Location"));
		locations.add(new Card("Conservatory", "Location"));
		locations.add(new Card("Billiard Room", "Location"));
		locations.add(new Card("Library", "Location"));
		locations.add(new Card("Study", "Location"));
		locations.add(new Card("Lounge", "Location"));
		
		Collections.shuffle(weapons);
		Collections.shuffle(suspects);
		Collections.shuffle(locations);
		
		winningGuess = new Card[3];
		
		winningGuess[0] = weapons.get(0);
		winningGuess[1] = suspects.get(0);
		winningGuess[2] = locations.get(0);
		
		weapons.remove(0);
		suspects.remove(0);
		locations.remove(0);
		
		finalDeck.addAll(weapons);
		finalDeck.addAll(suspects);
		finalDeck.addAll(locations);
		Collections.shuffle(finalDeck);
		
		System.out.print("The winning deck is: ");
		for(int i = 0; i < winningGuess.length; i++) {
			System.out.print(winningGuess[i].getName() + ", ");
		}
		
		for(int i = 0; i < 3; i++) {
			ClueLessServer.players.get(0).addCard(finalDeck.get(i));
		}
		
		for(int i = 3; i < 6; i++) {
			ClueLessServer.players.get(1).addCard(finalDeck.get(i));
		}
		
		for(int i = 6; i < 9; i++) {
			ClueLessServer.players.get(2).addCard(finalDeck.get(i));
		}
		
		for(int i = 9; i < 12; i++) {
			ClueLessServer.players.get(3).addCard(finalDeck.get(i));
		}
		
		for(int i = 12; i < 15; i++) {
			ClueLessServer.players.get(4).addCard(finalDeck.get(i));
		}
		
		for(int i = 15; i < 18; i++) {
			ClueLessServer.players.get(5).addCard(finalDeck.get(i));
		}
		
		for(Player p : ClueLessServer.players) {
			Socket s = p.getSocket();
			try {
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
				pw.println("ASSIGNED" + p.getName());
				pw.println("EVENT" + "You have been assigned: " + p.getName());
				StringBuilder sb = new StringBuilder();
				sb.append("Your cards are: ");
				for(int i = 0; i < p.getHand().size(); i++) {
					sb.append(p.getHand().get(i).getName() + ", ");
				}
				
				sb.setLength(sb.length() - 2);
				pw.println("EVENT" + sb.toString());
				
				//Start the first turn!
				
				pw.println("NEWTURN" + ClueLessServer.players.get(currentTurn).getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	public String serverCommand(String command) {
		if(command.startsWith("MOVE")) {
			for(PrintWriter writer : ClueLessServer.writers) {
				writer.println(command);				
			}
		} else if(command.startsWith("SKIP")) {
			if(currentTurn < 5) {
				currentTurn++;
			} else {
				currentTurn = 0;
			}
			
			for(PrintWriter writer : ClueLessServer.writers) {				
				writer.println("NEWTURN" + ClueLessServer.players.get(currentTurn).getName());
			}
		} else if(command.startsWith("SUGGESTION")) {
			command = command.substring(10);
			String[] suggestion = command.split(";");
			serverCommand("MOVE" + suggestion[2] + ";" + suggestion[0]);
			String cardFound = null;
			for(Player p : ClueLessServer.players)  {
				if(!p.getName().equals(suggestion[3])) {
					for(Card c : p.getHand()) {
						if(c.getName().equalsIgnoreCase(suggestion[0]) || c.getName().equalsIgnoreCase(suggestion[1]) || c.getName().equalsIgnoreCase(suggestion[2])) {
							cardFound = c.getName();
							break;
						}
					}
				
					if(cardFound != null) {
						return ("EVENT" + p.getName() + " had the " + cardFound + " card");
					}
				}
			}
			
			if(cardFound == null) {
				return ("EVENTNo player had those cards!");
			}
		}
		
		return null;
	}
}
