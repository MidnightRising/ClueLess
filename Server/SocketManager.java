import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SocketManager extends Thread {
	private ChatHandler chat;
	private Socket socket;
	
	public SocketManager(Socket socket) throws IOException {
		this.socket = socket;
		chat = new ChatHandler(socket);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
				String line = reader.readLine();
				if(line != null) {
					System.out.println("SERVER-SM: " + line);
				}
				
				if(line.startsWith("CHATMESSAGE")) {
					line = line.substring(11);
					chat.chatMessage(line);
				} else if(line.startsWith("NAME")) {
					line = line.substring(4);
					chat.addName(line);
				} else if(line.startsWith("MOVE") || line.startsWith("SKIP")) {
					ClueLessServer.game.serverCommand(line);
				}
			} catch (Exception e) {
				try {
					socket.close();
					break;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}	
	}
}
