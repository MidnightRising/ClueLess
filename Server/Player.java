import java.net.Socket;
import java.util.ArrayList;

public class Player {
	private Socket socket;
	private String characterName;
	private ArrayList<Card> hand = new ArrayList<Card>();
	private boolean lost;
	
	public Player(Socket socket) {
		this.socket = socket;
		lost = false;
	}
	
	public boolean hasLost() {
		return lost;
	}
	
	public void lost() {
		lost = true;
	}
	
	public void addCard(Card card) {
		hand.add(card);
	}
	
	public void setName(String name) {
		characterName = name;
	}
	
	public String getName() {
		return characterName;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
