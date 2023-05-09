package controllers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Database.DBUtil;
import Database.DatabaseProvider;
import Database.DerbyDatabase;
import Database.FakeGameDatabase;
import Database.IDatabase;
import models.Game;
import models.Player;
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;
//'5254 7775 4185 7886 8586 7574 8687 4746 8788 7473 8868 5847 6867 4868 6766 4748 6668 '
public class GameController {
	//zELLER gAE 
	//0 for Fake
	//1 for Real
	private int dbTest = 0;
	private IDatabase db = null;
	private Game model;
	private String username;
	public void setModel(Game model){
		this.model = model;
	}
	
	public GameController() {
		if(dbTest == 0) {
			db = new FakeGameDatabase();
		}else {
			db = new DerbyDatabase();
		}
	}
	
	public void setBoard(String boardLocations,String turn) {		
		
		//Sets the Game String to be passed back and forth
		this.model.setGameMoves(boardLocations);
		
		//if there are no moves is boardLocations, the game is reset
		checkForPawnPromotion();
		if(boardLocations.length() == 0) {
			db.resetLocations();
		}
		//initialize the Pieces and Board
		this.model.setBoard(db.populateBoard(boardLocations));
		
		//Set En Passant
		setEnPassant();
		//initializes Players
		this.model.setWhitePlayer(db.populatePlayer(Piece.WHITE));
		this.model.setBlackPlayer(db.populatePlayer(Piece.BLACK));
			if(this.model.getWhitePlayer().getPieces()[0].checkForCheck(this.model.getBoard(), 0)
				|| this.model.getBlackPlayer().getPieces()[0].checkForCheck(this.model.getBoard(), 1)) {
			this.model.setInCheck(true);
			if(checkForCheckMate(this.model.getWhitePlayer()) || checkForCheckMate(this.model.getBlackPlayer())) {
				this.model.setInCheckmate(true);
			}
			
		}
			
	}
	
	private void checkForPawnPromotion() {
		try{Integer.parseInt(""+this.model.getGameMoves().charAt(model.getGameMoves().length()-4));
		}catch(NumberFormatException e){
			db.updatDatabaseForPawnPromotion(Integer.parseInt(""+this.model.getGameMoves().charAt(model.getGameMoves().length()-6))-1,
					Integer.parseInt(""+this.model.getGameMoves().charAt(model.getGameMoves().length()-5))-1,
					this.model.getGameMoves().charAt(model.getGameMoves().length()-4));
		}catch(StringIndexOutOfBoundsException e) {
			
		}
	}

	public String getPossibleMoves(String clickedOnLocation, String playerTurn) {
		
		//Creates String to Send to Server that has list of locations the Selected Piece can Move 
		String listOfLocations = "";
		int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
		int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;

		//Grabs selected Piece from database
		Piece selectedPiece = db.getPiece(pieceXpos,pieceYpos);
		this.model.getBoard()[selectedPiece.getYpos()][selectedPiece.getXpos()] = selectedPiece;
		this.setEnPassant();
		//checks to make sure that the piece selected is the of the same color 
		if((playerTurn.equals("White") && selectedPiece.getColor() == Piece.WHITE) || (playerTurn.equals("Black") && selectedPiece.getColor() == Piece.BLACK)) {
		//Adds all possible Moves to list of Locations String
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
				//If Move is a Valid Move, update the board
				if(this.model.getBoard()[newPieceYpos][newPieceXpos] != null) {
					this.model.getBoard()[newPieceYpos][newPieceXpos].setCaptured(true); 
				}
				this.model.getBoard()[newPieceYpos][newPieceXpos] = this.model.getBoard()[selectedPieceYpos][selectedPieceXpos];
				this.model.getBoard()[selectedPieceYpos][selectedPieceXpos] = null;
				this.model.getBoard()[newPieceYpos][newPieceXpos].setXpos(newPieceXpos);
				this.model.getBoard()[newPieceYpos][newPieceXpos].setYpos(newPieceYpos);
				
				//update Database
				db.updateDatabase(selectedPieceXpos, selectedPieceYpos, newPieceXpos, newPieceYpos);
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
							if(Integer.parseInt(castleLoc.charAt(0)+"") == newPieceXpos
									&& Integer.parseInt(castleLoc.charAt(1)+"") == newPieceYpos) {
							int rookOldXpos = -1;
							int rookNewXpos = -1;
							if(castleLoc.charAt(0) == '6') {
								if( this.model.getBoard()[newPieceYpos][7] instanceof Rook && !this.model.getBoard()[newPieceYpos][7].getHasMovedAlready()) {
								//Rook is on the Right and needs to be moved to the Left
								Piece rook = this.model.getBoard()[newPieceYpos][7];
								this.model.getBoard()[newPieceYpos][7] = null;
								this.model.getBoard()[newPieceYpos][newPieceXpos-1] = rook;
								rookOldXpos = 8;
								rookNewXpos = 6;
								db.updateDatabaseForCastling(rookOldXpos-1, newPieceYpos);
								}
							}else {
								if( this.model.getBoard()[newPieceYpos][0] instanceof Rook && !this.model.getBoard()[newPieceYpos][0].getHasMovedAlready()) {
								//Rook is on the Left and needs to be moved to the left
								Piece rook = this.model.getBoard()[newPieceYpos][0];
								this.model.getBoard()[newPieceYpos][0] = null;
								this.model.getBoard()[newPieceYpos][newPieceXpos+1] = rook;							
								rookOldXpos = 1;
								rookNewXpos = 4;
								db.updateDatabaseForCastling(rookOldXpos-1, newPieceYpos);
								}
							}
							//Split String into 2 and add the string into GameMoves
							String leftOfSplit = this.model.getGameMoves().substring(0, this.model.getGameMoves().length() -2 );
							String rightOfSplit = this.model.getGameMoves().substring(this.model.getGameMoves().length() -2 );
							this.model.setGameMoves( leftOfSplit+rookOldXpos+(newPieceYpos+1)+rookNewXpos+(newPieceYpos+1)+" "+rightOfSplit);
						
						}
						}
					}
					//Checks For EnPassant
					}else if(this.model.getBoard()[newPieceYpos][newPieceXpos] instanceof Pawn && 
							this.model.getBoard()[newPieceYpos][newPieceXpos] != null) {
						
						Pawn pawn = (Pawn) this.model.getBoard()[newPieceYpos][newPieceXpos];
						if(pawn.getEnPassantLoc() != null && pawn.getEnPassantLoc()[0] == x[0] && pawn.getEnPassantLoc()[1] == x[1]) {
							int capturedYpos = pawn.getColor() == Piece.WHITE?
									pawn.getEnPassantLoc()[1]-1: pawn.getEnPassantLoc()[1]+1; 
							String leftOfSplit = this.model.getGameMoves().substring(0, this.model.getGameMoves().length() -2 );
							String rightOfSplit = this.model.getGameMoves().substring(this.model.getGameMoves().length() -2 );
							this.model.setGameMoves( leftOfSplit+(pawn.getEnPassantLoc()[0]+1)+(capturedYpos+1)+(pawn.getEnPassantLoc()[0]+1)+(newPieceYpos+1)+" "+rightOfSplit);
							this.model.getBoard()[capturedYpos][newPieceXpos] = null;
							db.updateDatabaseForEnPassant(pawn.getXpos(),pawn.getYpos(),pawn.getColor());
						}
					}
					
				
				//Checks for Check/Checkmate on King
				Player opponent = this.model.getBoard()[newPieceYpos][newPieceXpos].getColor() == 0 ? this.model.getBlackPlayer(): this.model.getWhitePlayer(); 
				King opposingKing = (King) opponent.getPieces()[0];
				
				if(opposingKing.checkForCheck(this.model.getBoard(), opposingKing.getColor())){
					this.model.setInCheck(true);
					if(checkForCheckMate(opponent)) {
						this.model.setInCheckmate(true);
					}
				}else {
					this.model.setInCheck(false);
					//if(this.chekForMateInOne(1,this.model.getBlackPlayer(),this.model.getBoard()) || this.chekForMateInOne(0,this.model.getWhitePlayer(),this.model.getBoard())) {
					//	this.model.setChekForMateInOne(true);
					//}else {
					//	this.model.setChekForMateInOne(false);
					//}
				}
				
				
				return true;
			}
		}
		return false;
	}
	
	private Boolean checkForCheckMate(Player player) {
		Piece p;
		for(Piece piece : player.getPieces()) {
			if(!piece.getCaptured()) {
				if(piece.getValidMoves(this.model.getBoard(), piece.getColor()).size() > 0) {
					return false;
				}
			
			}
		}
		return true;
	}
	private Boolean setEnPassant() {
		String gameMoves =this.model.getGameMoves();
		Piece[][] board = model.getBoard();
		//If the last move was a pawn,its never been called before, delta y = 2, and theres a pawn next to it
		//en passant is true for that pawn next to it
		//Step 1, grab the last part of the game string
		//if last move was pawn promotion , break
		int possiblePawnOldXpos = -1;int possiblePawnOldYpos = -1;int	possiblePawnNewXpos = -1;int possiblePawnNewYpos = -1;
		if(gameMoves.length() > 8) {
			try {
			possiblePawnOldXpos = Integer.parseInt(""+gameMoves.charAt(gameMoves.length()-7))-1;
			possiblePawnOldYpos = Integer.parseInt(""+gameMoves.charAt(gameMoves.length()-6))-1;
			possiblePawnNewXpos = Integer.parseInt(""+gameMoves.charAt(gameMoves.length()-5))-1;
			
			possiblePawnNewYpos = Integer.parseInt(""+gameMoves.charAt(gameMoves.length()-4))-1;
			}catch(NumberFormatException e) {
				return false;
			}
			if(this.model.getBoard()[possiblePawnNewYpos][possiblePawnNewXpos] instanceof Pawn){
			if((possiblePawnOldYpos == 1 || possiblePawnOldYpos == 6) && Math.abs(possiblePawnNewYpos-possiblePawnOldYpos)==2) {
				//checks left of Pawn
				if(possiblePawnNewXpos-1 >=0) {if(board[possiblePawnNewYpos][possiblePawnNewXpos-1] != null){
					 if(board[possiblePawnNewYpos][possiblePawnNewXpos-1] instanceof Pawn) {
						Pawn enPassantablePawn = (Pawn) board[possiblePawnNewYpos][possiblePawnNewXpos-1];
						if(board[possiblePawnNewYpos][possiblePawnNewXpos].getColor() == Piece.WHITE){
							enPassantablePawn.setEnPassantLoc(new Integer[] {possiblePawnOldXpos,possiblePawnOldYpos+1});
						}else {enPassantablePawn.setEnPassantLoc(new Integer[] {possiblePawnOldXpos,possiblePawnOldYpos-1});}
						 return true;
					}}
				}if(possiblePawnNewXpos+1 <=7){if(board[possiblePawnNewYpos][possiblePawnNewXpos+1] != null) {
					if(board[possiblePawnNewYpos][possiblePawnNewXpos+1] instanceof Pawn) {
						Pawn enPassantablePawn = (Pawn) board[possiblePawnNewYpos][possiblePawnNewXpos+1];
						if(board[possiblePawnNewYpos][possiblePawnNewXpos].getColor() == Piece.WHITE){
						enPassantablePawn.setEnPassantLoc(new Integer[] {possiblePawnOldXpos,possiblePawnOldYpos+1});
					}else {enPassantablePawn.setEnPassantLoc(new Integer[] {possiblePawnOldXpos,possiblePawnOldYpos-1});}
						 return true;
					}}}}}}return false;
	}

	public List<String> getCapturedPieces(int playerColor) {
		return db.getCapturedPlayersList(playerColor);
	}
	public Boolean chekForMateInOne(int playColor,Player player,Piece[][] board){
		Boolean mateIn1 = false;
		//opp king
		Player opponent = playColor == Piece.WHITE? this.model.getBlackPlayer(): this.model.getWhitePlayer();
		King king = (King) opponent.getPieces()[0];
		for(int j = 0; j < 8;j++) {
			
		
		for(Piece piece: board[j]) {
			if(piece != null && piece.getColor() == playColor) {
				for(Integer[] loc : piece.getValidMoves(board, playColor)) {
					if((loc[0]!= king.getXpos() || loc[1]!= king.getYpos())){

					Piece old = board[loc[1]][loc[0]];
					if(old != null) {
					old.setCaptured(true);
					}
					int oldx = piece.getXpos(); int oldy = piece.getYpos();
					board[piece.getYpos()][piece.getXpos()] = null;
					piece.setXpos(loc[0]);
					piece.setYpos(loc[1]);
					board[piece.getYpos()][piece.getXpos()] = piece;
					if(this.checkForCheckMate(opponent)) {
						System.out.println(piece.type()+" at pos "+piece.getXpos()+1+":"+piece.getYpos()+1);
						mateIn1 = true;
					}
					if(old != null) {
					old.setCaptured(false);
					}
					board[piece.getYpos()][piece.getXpos()] = old;
					piece.setXpos(oldx);
					piece.setYpos(oldy);
					board[piece.getYpos()][piece.getXpos()] = piece;
					}
				}
				}
			}
		}
		
		return mateIn1;
	}
	
	public void serverCheckForMateIn(){
		if(chekForMateInOne(0,this.model.getWhitePlayer(),this.model.getBoard()) || chekForMateInOne(1,this.model.getBlackPlayer(),this.model.getBoard())) {
			this.model.setChekForMateInOne(true);
		}else {
				this.model.setChekForMateInOne(false);
		}
	}

	public void setUsername(String username) {
		this.username = username;
		db.setUsername(username);
	}

	public void setTurn(String playerTurn) {
		if(playerTurn == "White") {
			this.model.getWhitePlayer().setTurn(true);
			this.model.getBlackPlayer().setTurn(false); 
		}else {
			this.model.getWhitePlayer().setTurn(false);
			this.model.getBlackPlayer().setTurn(true);
		}
		
	}

	public void switchTurns() {
		if(this.model.getWhitePlayer().getTurn()) {
			this.model.getBlackPlayer().setTurn(true);
			this.model.getWhitePlayer().setTurn(false);
		}
		
	}

	public void saveGame() {
		db.saveGame();
		
	}
}
