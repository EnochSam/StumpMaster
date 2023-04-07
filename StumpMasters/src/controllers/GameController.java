package controllers;


import java.util.ArrayList;
import java.util.List;

import models.Game;
import models.Player;
import pieceModels.King;
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
	
	public String getPossibleMoves(String clickedOnLocation, String playerTurn) {
		String listOfLocations = "";
		int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
		int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;
		
		Piece selectedPiece = this.model.getBoard()[pieceYpos][pieceXpos];
		if((playerTurn.equals("White") && selectedPiece.getColor() == Piece.WHITE) || (playerTurn.equals("Black") && selectedPiece.getColor() == Piece.BLACK)) {
		List<Integer[]> possibleMoves = selectedPiece.getValidMoves(this.model.getBoard());
		for(Integer[] loc : possibleMoves) {
			listOfLocations+= ""+(loc[0]+1);
			listOfLocations+= ""+(loc[1]+1);
			listOfLocations+=" ";
		}
		
		if(this.model.getInCheck()) {
			//if the player is in check, we will resize the list of moves to only show moves that can reach opposing piece
			String stringOfAvailableMoves = "";
			//Grabs list of AvailaleMoves from Model
			List<Integer[]> availableMoves = this.model.getAvailableMoves();
			
			//If Location != location of attackingPiece, remove from list
			for(int i = 0; i < listOfLocations.length(); i+=3){
				boolean inListOfAvailableLocations = false;
				for(Integer[] x : availableMoves) {
					String locx = (""+(x[0]+1));
					String locy = (""+(x[1]+1));
					if(locx.equals(""+listOfLocations.charAt(i)) && locy.equals(""+listOfLocations.charAt(i+1))) {
						inListOfAvailableLocations = true;
					}
				}
				if(inListOfAvailableLocations){
					stringOfAvailableMoves+= ""+listOfLocations.charAt(i)+""+listOfLocations.charAt(i+1);
				}
			}
			return stringOfAvailableMoves;

		}
		return listOfLocations;
		}else {
			return "False";
		}
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
		checkForCheck();
	}
	
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove,String player) {
		String validMoves = getPossibleMoves(attemptingToMove,player);
		
		int selectedPieceXpos = Integer.parseInt(""+attemptingToMove.charAt(0))-1;
		int selectedPieceYpos = Integer.parseInt(""+attemptingToMove.charAt(2))-1;
		
		int newPieceXpos = Integer.parseInt(""+newPieceLoc.charAt(0))-1;
		int newPieceYpos = Integer.parseInt(""+newPieceLoc.charAt(2))-1;
		System.out.println("loc: "+newPieceXpos+":"+newPieceYpos);
		if(selectedPieceXpos == newPieceXpos && selectedPieceYpos == newPieceYpos) {
			return true;
		}
		Integer[] loc;
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>(); 
		for(int i = 0; i < validMoves.length(); i+=3) {
			loc = new Integer[2];
			loc[0] = Integer.parseInt(""+validMoves.charAt(i))-1;
			loc[1] = Integer.parseInt(""+validMoves.charAt(i+1))-1;
			System.out.println(loc[0]+":"+loc[1]);
			possibleMoves.add(loc);
		}
		for(Integer[] x : possibleMoves) {
			System.out.println(x[0]+"=="+newPieceXpos+"  "+x[1]+"=="+newPieceYpos);
			if(x[0] == newPieceXpos && x[1] == newPieceYpos) {
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] != null) {
					this.model.getBoard()[newPieceYpos][newPieceXpos].setCaptured(true); 
				}
				this.model.getBoard()[newPieceYpos][newPieceXpos] = this.model.getBoard()[selectedPieceYpos][selectedPieceXpos];
				this.model.getBoard()[selectedPieceYpos][selectedPieceXpos] = null;
				this.model.getBoard()[newPieceYpos][newPieceXpos].setXpos(newPieceXpos);
				this.model.getBoard()[newPieceYpos][newPieceXpos].setYpos(newPieceYpos);
				checkForCheck();
				System.out.println("Passed");
				return true;
			}
		}
		return false;
	}
	
	public void checkForCheck() {
		//Creates King of Both Players
		King WhiteKing = (King) this.model.getWhitePlayer().getPieces()[0];
		King BlackKing = (King) this.model.getBlackPlayer().getPieces()[0];
		
		//Checks Both Kings to See if they Are in Check
		WhiteKing.checkForCheckMate(this.model.getBoard());
		BlackKing.checkForCheckMate(this.model.getBoard());
		
		if(WhiteKing.getInCheck()) {
			//if the WhiteKing is in Check, set list of availableMoves to Kings getOutOfCheckList
			this.model.setAvailableMoves(WhiteKing.getGetOutOfCheckMoves());
			this.model.setInCheck(true);
		}else if(BlackKing.getInCheck()) {
			this.model.setAvailableMoves(BlackKing.getGetOutOfCheckMoves());
			this.model.setInCheck(true);
		}else {
			this.model.setInCheck(false);
		}
	}
	
}
