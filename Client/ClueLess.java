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
	
	private Character getCharacter() {
		return character;
	}
		
	public ClueLess() {
		Gameboard gb = new Gameboard();
		character = new Character("Colonel Mustard", gb.getStartingLocation());
		gb.setActiveCharacter(character);
		gameboard = new JFrame();
		gameboard.setTitle("Clue-Less");
		gameboard.setSize(800,900);
		gameboard.setVisible(true);
		gameboard.getContentPane().add(gb);
	}
	
	public static void main(String[] args) {
		try {
			Socket s = new Socket("185.21.217.77", 1337);
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			System.out.println(input.readLine());
			ClueLess cl = new ClueLess();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection to the server failed. Is the IP correct? Is the server running?");
		}
	}
}
