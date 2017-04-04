/** @author Thomas Riley
 *  @description Handles the chat client in ClueLess
 *  @lastModified 4/4/2017 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

public class ChatHandler extends Thread {
	private HashSet<String> names = new HashSet<String>();  //Names of the chatters
	private HashSet<PrintWriter> writers = new HashSet<PrintWriter>(); //The output stream required for updating
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
			
			while (true) {
				out.println("Please Submit Your Name");
				name = in.readLine();
				if (name == null) {
					return;
				}
				
				synchronized (names) {
					if(!names.contains(name)) {
						names.add(name);
						break;
					}
				}
			}
			
			out.println("Name accepted, connecting to the chat.");
			writers.add(out);
			
			while(true) {
				String input = in.readLine();
				
				if (input == null) {
					return;
				}
				
				for(PrintWriter writer : writers) {
					writer.println(name + ":" + input);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (name != null) {
				names.remove(name);
			}
			
			if (out != null) {
				writers.remove(out);
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Something's gone terribly wrong.");
			}
		}
	}
}
