import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatClient {
	
	private Socket socket;
	private JTextField input;
	private JTextArea chatbox;
	private JTextArea eventArea;
	private PrintWriter out;
	private String name;
	private JFrame gb;

	ChatClient(Socket s, JTextField input, JTextArea chatbox, String name, JFrame gb, JTextArea eventArea) {
		this.socket = s;
		this.input = input;
		this.chatbox = chatbox;
		this.name = name;
		this.gb = gb;
		this.eventArea = eventArea;
		addListeners();
	}
	
	private void addListeners() {
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                out.println("CHATMESSAGE" + name + ": " + input.getText());
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
	            	out.println("NAME" + name);
	            } else if (command.startsWith("NAMEACCEPTED")) {
	                input.setEditable(true);
	            } else if (command.startsWith("MESSAGE")) {
	                chatbox.append(command.substring(7) + "\n");
	            } else if (command.startsWith("REJECTEDNAME")) {
	            	String newName = getNewName();
	                out.println("NAME" + newName);
	                name = newName;
	            } else if (command.startsWith("EVENT")) {
	            	eventArea.append(command.substring(5) + "\n\n");
	            } else if (command.startsWith("MOVE")) {
	            	command = command.substring(4);
	            	String[] commandArray = command.split(";");
	            	if(commandArray[1].indexOf("To") > -1) {
	            		String[] hallwayCommandArray = commandArray[1].split("To");
	            		commandArray[1] = "hallway between the " + hallwayCommandArray[0].toLowerCase() + " and the " + hallwayCommandArray[1].toLowerCase();
	            	}
	            	eventArea.append(commandArray[0] + " moved to the " + commandArray[1] + "\n\n");
	            }
	            
	            chatbox.setCaretPosition(chatbox.getDocument().getLength());
				eventArea.setCaretPosition(eventArea.getDocument().getLength());
		} catch(IOException e) {
			System.out.println("Something went wrong.");
		}
	}
}
