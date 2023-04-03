package controllers;


import java.util.List;

import models.Game;
import models.Player;
import pieceModels.Pawn;
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
	
	public String getPossibleMoves(String clickedOnLocation) {
		String listOfLocations = "";
		try {
		int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
		int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;
		
		Piece selectedPiece = this.model.getBoard()[pieceYpos][pieceXpos];
		List<Integer[]> possibleMoves = selectedPiece.getValidMoves(this.model.getBoard());
		for(Integer[] loc : possibleMoves) {
			listOfLocations+= ""+(loc[0]+1);
			listOfLocations+= ""+(loc[1]+1);
			listOfLocations+=" ";
		}
		}catch(Exception e) {
			return "False";
		}

		return listOfLocations;
	}
	
	public void setBoard(String boardLocations) {
		//initialize the Pieces and Board
		Piece[][] board = new Piece[8][8];
		Player[] players = model.getBothPlayers();
		
		//load initially on board
		for(int i = 0; i < players.length; i++){

			for(int j = 0; j < players[i].getPieces().length; j++) {
				
				Piece tempPiece= players[i].getPieces()[j];
				int tempPieceX =tempPiece.getXpos();
				int tempPieceY =tempPiece.getYpos();
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
			int curx = Integer.parseInt(""+boardLocations.charAt(i))-1;
			int cury = Integer.parseInt(""+boardLocations.charAt(i+1))-1;
			int newx = Integer.parseInt(""+boardLocations.charAt(i+2))-1;
			int newy = Integer.parseInt(""+boardLocations.charAt(i+3))-1;
			//if piece is on moved location set that piece to captured
			if(board[newy][newx] != null) {
				board[newy][newx].setCaptured(true);
			}
			board[newy][newx] = board[cury][curx];
			board[cury][curx] = null;
			board[newy][newx].setXpos(newx);
			board[newy][newx].setYpos(newy);
			board[newy][newx].setHasMovedAlready(true);
		}
		this.model.setBoard(board);
			
	}
	
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove) {
		int selectedPieceXpos = Integer.parseInt(""+attemptingToMove.charAt(0))-1;
		int selectedPieceYpos = Integer.parseInt(""+attemptingToMove.charAt(1))-1;
		
		int newPieceXpos = Integer.parseInt(""+newPieceLoc.charAt(0))-1;
		int newPieceYpos = Integer.parseInt(""+newPieceLoc.charAt(2))-1;
		if(selectedPieceXpos == newPieceXpos && selectedPieceYpos == newPieceYpos) {
			return true;
		}
		Piece pieceAttemptingToMove = this.model.getBoard()[selectedPieceYpos][selectedPieceXpos];
		List<Integer[]> possibleMoves = pieceAttemptingToMove.getValidMoves(this.model.getBoard());
		for(Integer[] x : possibleMoves) {
			

			if(x[0] == newPieceXpos && x[1] == newPieceYpos) {
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] != null) {
					this.model.getBoard()[newPieceYpos][newPieceXpos].setCaptured(true); 
				}
				this.model.getBoard()[newPieceYpos][newPieceXpos] = this.model.getBoard()[selectedPieceYpos][selectedPieceXpos];
				this.model.getBoard()[selectedPieceYpos][selectedPieceXpos] = null;
				return true;
			}
		}
		return false;
	}
	
}
