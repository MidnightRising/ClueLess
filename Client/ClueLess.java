/*
 * Instantiates the JFrame for the GUI and starts the game
 */

import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClueLess {
	private JFrame gameboard;
	private JTextArea messageArea;
	private JTextField textField;
	private static JLabel turnLabel;
	private Socket socket;
	boolean shownOnce = false;
		
	public ClueLess() {
		try {
			
			String name = getName();
			Gameboard gb = new Gameboard();
			turnLabel = new JLabel("Welcome to Clueless!", SwingConstants.CENTER);
			turnLabel.setFont(new Font("Serif", Font.PLAIN, 16));
		
			messageArea = new JTextArea();
			textField = new JTextField();

			textField.setEditable(false);
			messageArea.setEditable(false);
		
			gb.setLayout(null);
			gameboard = new JFrame();
			gameboard.setTitle("Clue-Less");
			textField.setBounds(20,830, 750, 25);
			turnLabel.setBounds(300,50,200,15);
			JScrollPane scroll = new JScrollPane (messageArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setBounds(20,725,750,100);
			gb.add(scroll);
			gb.add(textField);
			gb.add(turnLabel);
			gameboard.getContentPane().add(gb);
			gameboard.pack();
			gameboard.setVisible(true);
		
			socket = new Socket("localhost", 5555);
			gb.setSocket(socket);
			ChatClient cc = new ChatClient(socket, textField, messageArea, name, gameboard);
			SocketManager sm = new SocketManager(socket, gb, cc);
			sm.start();
		} catch (IOException e) {
			System.out.println("Something went terribly wrong!");
		}
	}
	
	private String getName() {
		return JOptionPane.showInputDialog(
		gameboard,
		"Welcome to Clue-Less! Please choose your display name:",
		"Screen name selection",
		JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void setLabel(String msg) {
		turnLabel.setText(msg);
	}
	
	public static void main(String[] args) {
		ClueLess cl = new ClueLess();
	}
}
