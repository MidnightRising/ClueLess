import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
				String playerName = null;
				if(line != null) {
					for(Player p : ClueLessServer.players) {
						if(p.getSocket() == socket) {
							playerName = p.getName();
						}
					}
					System.out.println("SERVER-SM: " + line + " from " + playerName);
				}
				
				if(line.startsWith("CHATMESSAGE")) {
					line = line.substring(11);
					chat.chatMessage(line);
				} else if(line.startsWith("NAME")) {
					line = line.substring(4);
					chat.addName(line);
				} else if(line.startsWith("MOVE") || line.startsWith("SKIP")) {
					ClueLessServer.game.serverCommand(line);
				} else if(line.startsWith("SUGGESTION") || line.startsWith("ACCUSATION")) {
					String output = ClueLessServer.game.serverCommand(line);
					if(output.startsWith("WIN") || output.startsWith("LOSE")) {
						for(Player p : ClueLessServer.players) {
							PrintWriter pw = new PrintWriter(p.getSocket().getOutputStream(), true);
							pw.println(output);
						}
					} else {
						PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
						pw.println(output);
						line = line.substring(10);
						String[] suggestionInformation = line.split(";");
						String eventLine = "EVENT" + suggestionInformation[3] + " suggested " + suggestionInformation[2] + " killed Mr. Body with the " + suggestionInformation[1] + " in the " + suggestionInformation[0] + " and ";
						if(output.startsWith("EVENTNo")) {
							eventLine += "no one could disprove it!";
						} else {
							eventLine += "was proven wrong by ";
							output = output.substring(5);
							String[] nameSplice = output.split(" ");
							eventLine += nameSplice[0] + " " + nameSplice[1];
						}
						
						for(Player p : ClueLessServer.players) {
							if(!p.getName().equalsIgnoreCase(suggestionInformation[3])) {
								PrintWriter eventPrinter = new PrintWriter(p.getSocket().getOutputStream(), true);
								eventPrinter.println(eventLine);	
							}
						}
					}
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
