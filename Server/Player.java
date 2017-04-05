import java.net.Socket;
import java.util.ArrayList;

public class Player {
	private Socket socket;
	private String characterName;
	private ArrayList<Card> hand = new ArrayList<Card>();
	
	public Player(Socket socket) {
		this.socket = socket;
	}
	
	public void addCard(Card card) {
		hand.add(card);
	}
	
	public void setName(String name) {
		characterName = name;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
