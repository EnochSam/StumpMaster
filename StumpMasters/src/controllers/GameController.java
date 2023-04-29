package controllers;


import java.util.ArrayList;
import java.util.List;

import Database.DerbyDatabase;
import models.Game;
import models.Player;
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

public class GameController {
	/*
	 * loadBoard
	 * 
	 */
	private DerbyDatabase db = null;
	private Game model;
	public void setModel(Game model){
		this.model = model;
	}
	
	public GameController() {
	}
	
	public void setBoard(String boardLocations) {		
//		db.setBoard(boardLocations);
		this.model.setGameMoves(boardLocations);
//		//initialize the Pieces and Board
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

		for(int i = 0; i < boardLocations.length() & boardLocations.length()-i >5; i+=5) {
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
			
			//Check For Pawn Promotion
			if(i+4 <boardLocations.length() ) {
				if(!(""+boardLocations.charAt(i+4)).equals(" ")) {
					int color = board[newy][newx].getColor();
					//Check Which Character the Pawn is Set To
					if((""+boardLocations.charAt(i+4)).equals("Q")){
						board[newy][newx] = new Queen();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("R")){
						board[newy][newx] = new Rook();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setColor(color);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("K")){
						board[newy][newx] = new Knight();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("B")){
						board[newy][newx] = new Bishop();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					i++;
				}
			}
		}
		this.model.setBoard(board);
	}
	
	public String getPossibleMoves(String clickedOnLocation, String playerTurn) {
		//return db.getPossibleMoves(clickedOnLocation, playerTurn);
		String listOfLocations = "";
		int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
		int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;
		
		Piece selectedPiece = this.model.getBoard()[pieceYpos][pieceXpos];
		//checks to make sure that the piece selected is the of the same color 
		if((playerTurn.equals("White") && selectedPiece.getColor() == Piece.WHITE) || (playerTurn.equals("Black") && selectedPiece.getColor() == Piece.BLACK)) {
		//Checks if the piece is pinned
		System.out.print(selectedPiece);
		
		// Looks to see if Castling is allowed
		List<Integer[]> possibleMoves = selectedPiece.getValidMoves(this.model.getBoard(),selectedPiece.getColor());
		for(Integer[] loc : possibleMoves) {
			listOfLocations+= ""+(loc[0]+1);
			listOfLocations+= ""+(loc[1]+1);
			listOfLocations+=" ";
		}
		
		return listOfLocations;
		}else {
			return "False";
		}
	}
	
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove,String player) {
		System.out.println(player);
		String validMoves = getPossibleMoves(attemptingToMove,player);
		System.out.println("Valid Move Validated");
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
				//CheckForCheck
				this.model.getBoard()[newPieceYpos][newPieceXpos].getKing(this.model.getBoard()).checkForCheckMate(this.model.getBoard());
				//Checks for Pawn Promotion
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] instanceof Pawn && 
						(this.model.getBoard()[newPieceYpos][newPieceXpos].getYpos() == 0
					  || this.model.getBoard()[newPieceYpos][newPieceXpos].getYpos() == 7)) {
					this.model.setPawnPromotion(true);
				}else
				//Checks for Castling
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] instanceof King) {
					King king =(King) this.model.getBoard()[newPieceYpos][newPieceXpos];
					for(String castleLoc : king.getCastlingLocation()) {
						if(castleLoc != null) {
							int rookOldXpos = -1;
							int rookNewXpos = -1;
							if(castleLoc.charAt(0) == '8') {
								if( this.model.getBoard()[newPieceYpos][7] instanceof Rook && !this.model.getBoard()[newPieceYpos][7].getHasMovedAlready()) {
								//Rook is on the Right and needs to be moved to the Left
								Piece rook = this.model.getBoard()[newPieceYpos][7];
								this.model.getBoard()[newPieceYpos][7] = null;
								this.model.getBoard()[newPieceYpos][newPieceXpos-1] = rook;
								rookOldXpos = 8;
								rookNewXpos = 6;
								}
							}else {
								if( this.model.getBoard()[newPieceYpos][0] instanceof Rook && !this.model.getBoard()[newPieceYpos][0].getHasMovedAlready()) {
								//Rook is on the Left and needs to be moved to the left
								Piece rook = this.model.getBoard()[newPieceYpos][0];
								this.model.getBoard()[newPieceYpos][0] = null;
								this.model.getBoard()[newPieceYpos][newPieceXpos+1] = rook;							
								rookOldXpos = 1;
								rookNewXpos = 4;
								}
							}
							//Split String into 2 and add the string into GameMoves
							String leftOfSplit = this.model.getGameMoves().substring(0, this.model.getGameMoves().length() -2 );
							String rightOfSplit = this.model.getGameMoves().substring(this.model.getGameMoves().length() -2 );
							this.model.setGameMoves( leftOfSplit+rookOldXpos+(newPieceYpos+1)+rookNewXpos+(newPieceYpos+1)+" "+rightOfSplit);
						
						}
					}
					
				}
				King opposingKing = this.model.getBoard()[newPieceYpos][newPieceXpos].getColor() == 0 ? (King) this.model.getBlackPlayer().getPieces()[0]: (King)this.model.getWhitePlayer().getPieces()[0];
				if(opposingKing.checkForCheck(this.model.getBoard(), opposingKing.getColor())){
					this.model.setInCheck(true);
				}
				return true;
			}
		}
		return false;
	}
	
}
