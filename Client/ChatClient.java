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

public class ChatClient {
	
	private Socket socket;
	private JTextField input;
	private JTextArea chatbox;
	private PrintWriter out;
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
	
	
	public void serverCommand(String command) {
		
	try {		
		out = new PrintWriter(socket.getOutputStream(), true);
		
	            if (command.startsWith("SUBMITNAME")) {
	            	out.println(name);
	            } else if (command.startsWith("NAMEACCEPTED")) {
	                input.setEditable(true);
	            } else if (command.startsWith("MESSAGE")) {
	                chatbox.append(command.substring(7) + "\n");
	            } else if (command.startsWith("REJECTEDNAME")) {
	            	String newName = getNewName();
	                out.println(newName);
	                name = newName;
	            } else if (command.startsWith("EVENT")) {
	            	chatbox.append(command.substring(5) + "\n");
	            }
		} catch(IOException e) {
			System.out.println("Something went wrong.");
		}
	}
}
