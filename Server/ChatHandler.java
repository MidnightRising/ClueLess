import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatHandler extends Thread {
	private String name;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	public ChatHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Running");
			
			out.println("SUBMITNAME");
			while (true) {
				name = in.readLine();
				System.out.println("I have a new name!");
				
				if (name == null) {
					return;
				}
				
				synchronized (ClueLessServer.names) {
					if(!ClueLessServer.names.contains(name)) {
						ClueLessServer.names.add(name);
						System.out.println("Someone has connected. Their name is: " + name);
						break;
					} else {
						out.println("REJECTEDNAME");
					}
				}
			}
			
			out.println("NAMEACCEPTED");
			ClueLessServer.writers.add(out);
			
			while(true) {
				String input = in.readLine();
				
				if (input == null) {
					return;
				}
				
				for(PrintWriter writer : ClueLessServer.writers) {
					writer.println("MESSAGE" + input);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (name != null) {
				ClueLessServer.names.remove(name);
			}
			
			if (out != null) {
				ClueLessServer.writers.remove(out);
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Something's gone terribly wrong.");
			}
		}
	}
}
