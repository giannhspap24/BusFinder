//package Reversi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	//public static final boolean first = true;
	public static boolean first;
	public static String opponentsColor;
	public static String myColor;
	
	public static void main (String Args[]) {
		
		turn();
		Board gameBoard = new Board();
		
		if (first) {
			// code if opponent goes first
			gameBoard.findLegalMoves(opponentsColor);
			gameBoard.printBoard();
			gameBoard.readMove();
			gameBoard.makeMove();

		}else {
			// code if algorithm goes first
			gameBoard.findLegalMoves(myColor);

		}

	}
	
	// sets variable "first" true if the opponent wants to play first,
	// and false if the opponent wants to play second.
	static void turn () {
		System.out.println("Do you wish to play first?\nType Y or N.");
		String answer = "";		
		do {
			try {	
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
				answer = br.readLine();	
				if (answer.equals("Y") | answer.equals("y")) {
					first = true;
					opponentsColor = "BLACK";
					myColor= "WHITE";
					break;
				}else if (answer.equals("N") | answer.equals("n")){
					first = false;
					opponentsColor = "WHITE";
					myColor = "BLACK";
					break;
				}else {	
					throw new IOException();
				}	
			}catch (IOException e){
				System.out.println("Wrong input. Please type the letter Y for yes and N for no.");
			}
		} while (true);	
	}
}
