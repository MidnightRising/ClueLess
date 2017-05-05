import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatHandler {
	private PrintWriter out;
	private Socket socket;
	
	public ChatHandler(Socket socket) throws IOException {
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream(), true);
		out.println("SUBMITNAME");
	}
	
	public void addName(String name) throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		synchronized (ClueLessServer.names) {
			if(!ClueLessServer.names.contains(name)) {
				ClueLessServer.names.add(name);
				out.println("NAMEACCEPTED");
				ClueLessServer.writers.add(out);
			} else {
				out.println("REJECTEDNAME");
			}
		}
	}
	
	public void chatMessage(String message) {
		for(PrintWriter writer : ClueLessServer.writers) {
			writer.println("MESSAGE" + message);
		}
	}
}
