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
	private Character character;
	private JFrame gameboard;
	private JTextArea messageArea;
	private JTextField textField;
	private Socket socket;
	boolean shownOnce = false;
	
	private Character getCharacter() {
		return character;
	}
		
	public ClueLess() {
		try {
			
			String name = getName();
			socket = new Socket("localhost", 5555);	
		
		messageArea = new JTextArea();
		textField = new JTextField();

		textField.setEditable(false);
		messageArea.setEditable(false);
		
		Gameboard gb = new Gameboard();
		gb.setLayout(null);
		character = new Character("Colonel Mustard", gb.getStartingLocation());
		gb.setActiveCharacter(character);
		gameboard = new JFrame();
		gameboard.setTitle("Clue-Less");
		textField.setBounds(20,830, 750, 25);
		JScrollPane scroll = new JScrollPane (messageArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(20,725,750,100);
		gb.add(scroll);
		gb.add(textField);
		
		ChatClient cc = new ChatClient(socket, textField, messageArea, name, gameboard);
		cc.start();
		gameboard.getContentPane().add(gb);
		gameboard.pack();
		gameboard.setVisible(true);
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
	
	public static void main(String[] args) {
			ClueLess cl = new ClueLess();
	}
}
