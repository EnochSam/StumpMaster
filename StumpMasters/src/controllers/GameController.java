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
		//checks to make sure that the piece selected is the of the same color 
		if((playerTurn.equals("White") && selectedPiece.getColor() == Piece.WHITE) || (playerTurn.equals("Black") && selectedPiece.getColor() == Piece.BLACK)) {
		//Checks if the piece is pinned
		
		//Finds the selected Piece's King
		King king;
		if(this.model.getWhitePlayer().getPieces()[0].getColor() == selectedPiece.getColor()) {
			king = (King) this.model.getWhitePlayer().getPieces()[0];
		}else {
			king = (King) this.model.getBlackPlayer().getPieces()[0];
		}
		List<Integer[]> possibleMoves = selectedPiece.getValidMoves(this.model.getBoard());
		for(Integer[] loc : possibleMoves) {
			//Temporarily changes board to make sure that moving the piece does not put the King in Check
			Piece attemptingToMove = this.model.getBoard()[loc[1]][loc[0]];
			this.model.getBoard()[pieceYpos][pieceXpos] = null;
			this.model.getBoard()[loc[1]][loc[0]] = selectedPiece;
			int origX = selectedPiece.getXpos();
			int origY = selectedPiece.getYpos();
			selectedPiece.setXpos(loc[0]);
			selectedPiece.setYpos(loc[1]);
			Boolean checkIfOpenMove;
			checkIfOpenMove = checkIfOpenMove(king,this.model.getBoard());
			this.model.getBoard()[loc[1]][loc[0]] = attemptingToMove;
			this.model.getBoard()[pieceYpos][pieceXpos]= selectedPiece;
			selectedPiece.setXpos(origX);
			selectedPiece.setYpos(origY);
			//if the king will not be put in check, add to the list of valid moves
			if(checkIfOpenMove) {
			listOfLocations+= ""+(loc[0]+1);
			listOfLocations+= ""+(loc[1]+1);
			listOfLocations+=" ";
			}
		}
		if(this.model.getInCheck() && !(selectedPiece instanceof King)) {
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
					stringOfAvailableMoves+= ""+listOfLocations.charAt(i)+""+listOfLocations.charAt(i+1)+" ";
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
		if(selectedPieceXpos == newPieceXpos && selectedPieceYpos == newPieceYpos) {
			return true;
		}
		Integer[] loc;
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>(); 
		for(int i = 0; i < validMoves.length(); i+=3) {
			loc = new Integer[2];
			loc[0] = Integer.parseInt(""+validMoves.charAt(i))-1;
			loc[1] = Integer.parseInt(""+validMoves.charAt(i+1))-1;
			possibleMoves.add(loc);
		}
		for(Integer[] x : possibleMoves) {
			if(x[0] == newPieceXpos && x[1] == newPieceYpos) {
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] != null) {
					this.model.getBoard()[newPieceYpos][newPieceXpos].setCaptured(true); 
				}
				this.model.getBoard()[newPieceYpos][newPieceXpos] = this.model.getBoard()[selectedPieceYpos][selectedPieceXpos];
				this.model.getBoard()[selectedPieceYpos][selectedPieceXpos] = null;
				this.model.getBoard()[newPieceYpos][newPieceXpos].setXpos(newPieceXpos);
				this.model.getBoard()[newPieceYpos][newPieceXpos].setYpos(newPieceYpos);
				King possibleCheckedKing = checkForCheck();
				if(possibleCheckedKing!= null) {
					checkForCheckMate(possibleCheckedKing);
				}
				return true;
			}
		}
		return false;
	}
	
	private King checkForCheck() {
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
			return WhiteKing;
		}else if(BlackKing.getInCheck()) {
			this.model.setAvailableMoves(BlackKing.getGetOutOfCheckMoves());
			this.model.setInCheck(true);
			return BlackKing;
		}else {
			this.model.setInCheck(false);
			return null;
		}
		
	}
	
	private boolean checkIfOpenMove(King king, Piece[][] board) {
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				//Move Check
				// Checks all pieces on the Board that ISNT the King in Question
				if(board[j][i] != null) {
					if(king.getColor() != board[j][i].getColor() ){

					for(Integer[] checkingLoc : board[j][i].getValidMoves(board)) {

						if(checkingLoc[0] == king.getXpos() && checkingLoc[1] == king.getYpos()) {
							return false;
						}
					}
					}
				}
			}
		}
		return true;
	}
	
	private Boolean checkForCheckMate(King king) {
		//Check if king can move
		String checkKingTile = ""+(king.getXpos()+1)+":"+(king.getYpos()+1);
		Player player;
		String playerTurn;
		if(king.getColor() == Piece.WHITE) {
			player = this.model.getWhitePlayer();
			playerTurn ="White";
		}
		else{
			player = this.model.getBlackPlayer();
			playerTurn = "Black";
		}
		if(this.getPossibleMoves(checkKingTile, playerTurn)!="") {
			return false;
		}
		//3 WAYS TO Instances of Checkmate:
		//1. Can the king move out of the way
		//2. Can a piece of same color reach the opposing location reach opposing piece
			
		//1. Find the piece of opposing team that can reach king
		Piece piecesthatcanReachKing = null;
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				if(this.model.getBoard()[j][i] != null) {
					boolean canreach = false;
					for(Integer[] loc : this.model.getBoard()[j][i].getValidMoves(this.model.getBoard())) {
						if(loc[0] == king.getXpos() && loc[1] == king.getYpos()) {
						
							canreach = true;

							}
						}
						if(canreach) {
							piecesthatcanReachKing = this.model.getBoard()[j][i];
						}
					}
				}
			}
			
			//Adds Location of attacking Piece to getOutOfCheckMovesList
			Integer[] availableMove = {};
			String getOutOfCheckMoves = ""+piecesthatcanReachKing.getXpos()+""+piecesthatcanReachKing.getYpos()+" ";
			//Check if the piece is attacking vertically or diagonal horizontally 
			if(piecesthatcanReachKing.getXpos() == king.getXpos() || piecesthatcanReachKing.getYpos() == king.getYpos()
				|| Math.abs(piecesthatcanReachKing.getXpos() - king.getXpos())
				== Math.abs(piecesthatcanReachKing.getYpos() - king.getYpos())) {
				int xincrement = 0;
				if(piecesthatcanReachKing.getXpos() - king.getXpos() > 0) xincrement = 1;
				if(piecesthatcanReachKing.getXpos() - king.getXpos() < 0) xincrement = -1;
				int yincrement = 0;
				if(piecesthatcanReachKing.getYpos() - king.getYpos() > 0) yincrement = 1;
				if(piecesthatcanReachKing.getYpos() - king.getYpos() < 0) yincrement = -1;
				int x = king.getXpos()+xincrement;
				int y = king.getYpos()+yincrement;

				while(this.model.getBoard()[y][x] != piecesthatcanReachKing) {
					getOutOfCheckMoves+=""+x+""+y+" ";
					x+=xincrement;
					y+=yincrement;
				}
			}
			//finally, check all pieces to see if any piece is able to reach any moves
			
			for(int i = 1; i < player.getPieces().length; i++) {
				Piece testingPiece = player.getPieces()[i];
				if(testingPiece.getCaptured() == false) {
				String tileSelected = ""+(testingPiece.getXpos()+1)+":"+(testingPiece.getYpos()+1);
				String possibleMoves = this.getPossibleMoves(tileSelected, playerTurn);
				try{
				for(int j = 0; j < possibleMoves.length(); j+=3) {
					for(int k = 0; k < getOutOfCheckMoves.length(); k+=3){
						int possibleMovesXpos = Integer.parseInt(""+possibleMoves.charAt(j)) -1;
						int possibleMovesYpos = Integer.parseInt(""+possibleMoves.charAt(j+1)) -1;
						int getOutofCheckXpos = Integer.parseInt(""+getOutOfCheckMoves.charAt(k));
						int getOutofCheckYpos = Integer.parseInt(""+getOutOfCheckMoves.charAt(k+1));
						if(possibleMovesXpos == getOutofCheckXpos && possibleMovesYpos == getOutofCheckYpos) {
							return false;
						}
					}
				
				}
				}catch(NumberFormatException e) {
					
				}
				}
			}	
			this.model.setInCheckmate(true);
			return true;
		}
	
}
