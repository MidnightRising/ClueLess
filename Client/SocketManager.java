import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketManager extends Thread {
	private Socket socket;
	private BufferedReader reader;
	private Gameboard gameboard;
	private ChatClient chat;
	
	public SocketManager(Socket socket, Gameboard gameboard, ChatClient chat) {
		this.socket = socket;
		this.gameboard = gameboard;
		this.chat = chat;
	}
	
	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				String line = reader.readLine();
				
				if(line != null) {					
					if(line.startsWith("MESSAGE") || line.startsWith("SUBMITNAME") || line.startsWith("NAMEACCEPTED") || line.startsWith("REJECTEDNAME") || line.startsWith("EVENT")) {
						chat.serverCommand(line);
					} else if(line.startsWith("ASSIGNED") || line.startsWith("MOVE") || line.startsWith("NEWTURN")) {
						gameboard.serverCommand(line);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
