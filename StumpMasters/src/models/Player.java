package models;
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

public class Player {
	private Piece[] pieces;
	private boolean inCheck;
	private boolean inCheckmate;
	
	//PlayerModeld with no Parameter
	public Player() {
		//initializes Pieces
		pieces = new Piece[16];
		
		//King ==0
		//Queen == 1
		//Rook == 2,3
		//Bishop == 4,5
		//Knight == 6,7
		//Pawn == 8-15
		
		this.pieces[0] = new King();
		
		this.pieces[1] = new Queen();
		
		this.pieces[2] = new Rook();
		this.pieces[3] = new Rook();
		
		this.pieces[4] = new Bishop();
		this.pieces[5] = new Bishop();

		//PlaceHolderFor Knight
		this.pieces[6] = new Knight();
		this.pieces[7] = new Knight();
		
		//creates Pawns
		for(int i = 8; i < this.pieces.length; i++) {
			this.pieces[i] = new Pawn();
		}	
	}
	
	//Construction with color Parameter
	public Player(int color) {
		//initializes Pieces
		pieces = new Piece[16];
		
		//King ==0
		//Queen == 1
		//Rook == 2,3
		//Bishop == 4,5
		//Knight == 6,7
		//Pawn == 8-15
		
		this.pieces[0] = new King();
		
		this.pieces[1] = new Queen();
		
		this.pieces[2] = new Rook();
		this.pieces[3] = new Rook();
		
		this.pieces[4] = new Bishop();
		this.pieces[5] = new Bishop();

		//PlaceHolderFor Knight
		this.pieces[6] = new Knight();
		this.pieces[7] = new Knight();
		
		//creates Pawns
		for(int i = 8; i < this.pieces.length; i++) {
			this.pieces[i] = new Pawn();
		}	
	}
	
	//InCheck GETTER
	public boolean getInCheck() {
		return this.inCheck;
	}
	
	//InCheck SETTER
	public void setInCheck(boolean inCheck) {
		this.inCheck = inCheck;
	}
	
	//inCheckmate GETTER
	public boolean getInCheckmate() {
		return this.inCheckmate;
	}
	
	//inCheckmate SETTER
	public void setInCheckmate(boolean inCheckmate) {
		this.inCheckmate = inCheckmate;
	}
	
	//Pieces GETTER
	public Piece[] getPieces(){
		return this.pieces;
	}
	
	//Pieces SETTER
	public void setPieces(Piece[] pieces) {
		this.pieces = pieces;
	}

	
}
