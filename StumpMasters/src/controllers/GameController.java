package controllers;

import models.Game;
import models.Player;
import pieceModels.Piece;

public class GameController {
	/*
	 * loadBoard
	 * 
	 */
	private Game model;
	
	public void setModel(Game model){
		this.model = model;
	}
	
	public GameController() {
		
	}
	
	public void setBoard(String boardLocations) {
		//initialize the Pieces and Board
		Piece[][] board = new Piece[8][8];
		Player[] players = model.getBothPlayers();
		
		//load initially on board
		for(int i = 0; i < players.length; i++){
			for(int j = 0; j < players[i].getPieces().length; j++) {
				Piece tempPiece= players[i].getPieces()[j];
				int tempPieceX =players[i].getPieces()[j].getXpos();
				int tempPieceY =(players[i].getPieces()[j].getYpos());
				board[tempPieceY][tempPieceX] = tempPiece;
			}
			
		}
		//BOARD LOCATIONS FORMULA
		//STRING = 1234 1234 1234
		//1= xpos of current location
		//2= ypos of current location
		//3= xpos of new location
		//4 = ypos of new location
		//5 = space
		for(int i = 0; i < boardLocations.length(); i+=5) {
			int curx = Integer.parseInt(""+boardLocations.charAt(i));
			int cury = Integer.parseInt(""+boardLocations.charAt(i+1));
			int newx = Integer.parseInt(""+boardLocations.charAt(i+2));
			int newy = Integer.parseInt(""+boardLocations.charAt(i+3));
			
			board[newy][newx] = board[cury][curx];
			board[cury][curx] = null;
		}
		this.model.setBoard(board);
			
	}
}
