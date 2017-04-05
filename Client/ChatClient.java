import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ChatClient extends Thread {
	
	private Socket socket;
	private JTextField input;
	private JTextArea chatbox;
	private PrintWriter out;
	private BufferedReader in;
	private String name;
	private JFrame gb;

	ChatClient(Socket s, JTextField input, JTextArea chatbox, String name, JFrame gb) {
		this.socket = s;
		this.input = input;
		this.chatbox = chatbox;
		this.name = name;
		this.gb = gb;
		addListeners();
	}
	
	private void addListeners() {
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                out.println(name + ": " + input.getText());
                input.setText("");
			}
		});
	}
	
	private String getNewName() {
		return JOptionPane.showInputDialog(
				gb,
	            "Your screenname has already been selected. Please choose another:",
	            "Screen name selection",
	            JOptionPane.PLAIN_MESSAGE);
	}
	
	public void run() {
		
		try {
			
		
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		 while (true) {
			 
	            String line = in.readLine();
	            if (line.startsWith("SUBMITNAME")) {
	            	out.println(name);
	            } else if (line.startsWith("NAMEACCEPTED")) {
	                input.setEditable(true);
	            } else if (line.startsWith("MESSAGE")) {
	                chatbox.append(line.substring(7) + "\n");
	            } else if (line.startsWith("REJECTEDNAME")) {
	            	String newName = getNewName();
	                out.println(newName);
	                name = newName;
	            } else if (line.startsWith("EVENT")) {
	            	chatbox.append(line.substring(5) + "\n");
	            }
	        }
		} catch(IOException e) {
			System.out.println("Something went wrong.");
		}
	}
}
