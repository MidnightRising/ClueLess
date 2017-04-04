import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClueLessServer {
	public static void main(String[] args) {
		try {
			//Createa a socket listener on port 1337
			ServerSocket listener = new ServerSocket(1337);
			
			while(true) {
				//As long as the server is running, accept new people.
				Socket socket = listener.accept();
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println("Connected");
				while (true) {
					new ChatHandler(listener.accept()).start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}