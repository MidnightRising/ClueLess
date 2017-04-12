import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ClueLessServer {
	 public static HashSet<String> names = new HashSet<String>();
	 public static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	 public static ArrayList<Player> players = new ArrayList<Player>();
	 public static Game game;
	 
	public static void main(String[] args) throws Exception {		
		ServerSocket listener = new ServerSocket(5555);
		try {
			while (true) {
				Socket player = listener.accept();
				SocketManager sm = new SocketManager(player);
				sm.start();
				players.add(new Player(player));
				if(players.size() == 6) {
					System.out.println("Starting the game!");
					players.get(0).setName("Colonel Mustard");
					players.get(1).setName("Mrs. White");
					players.get(2).setName("Mr. Green");
					players.get(3).setName("Mrs. Peacock");
					players.get(4).setName("Miss Scarlet");
					players.get(5).setName("Professor Plum");
					Collections.shuffle(players);
					game = new Game();
					
				} else {
					System.out.println("Players size: " + players.size());
				}
			}
		} finally {
			listener.close();
		}
		
	}
}