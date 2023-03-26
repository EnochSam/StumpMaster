package models;
import pieceModels.BishopModel;
import pieceModels.KingModel;
import pieceModels.KnightModel;
import pieceModels.PawnModel;
import pieceModels.PieceModel;
import pieceModels.QueenModel;
import pieceModels.RookModel;

public class PlayerModel {
	private PieceModel[] pieces;
	private boolean inCheck;
	private boolean inCheckmate;
	
	//PlayerModeld with no Parameter
	public PlayerModel() {
		//initializes Pieces
		pieces = new PieceModel[16];
		
		//King ==0
		//Queen == 1
		//Rook == 2,3
		//Bishop == 4,5
		//Knight == 6,7
		//Pawn == 8-15
		
		this.pieces[0] = new KingModel();
		
		this.pieces[1] = new QueenModel();
		
		this.pieces[2] = new RookModel();
		this.pieces[3] = new RookModel();
		
		this.pieces[4] = new BishopModel();
		this.pieces[5] = new BishopModel();

		//PlaceHolderFor Knight
		this.pieces[6] = new KnightModel();
		this.pieces[7] = new KnightModel();
		
		//creates Pawns
		for(int i = 8; i < this.pieces.length; i++) {
			this.pieces[i] = new PawnModel();
		}	
	}
	
	//Construction with color Parameter
	public PlayerModel(int color) {
		//initializes Pieces
		pieces = new PieceModel[16];
		
		//King ==0
		//Queen == 1
		//Rook == 2,3
		//Bishop == 4,5
		//Knight == 6,7
		//Pawn == 8-15
		
		this.pieces[0] = new KingModel();
		
		this.pieces[1] = new QueenModel();
		
		this.pieces[2] = new RookModel();
		this.pieces[3] = new RookModel();
		
		this.pieces[4] = new BishopModel();
		this.pieces[5] = new BishopModel();

		//PlaceHolderFor Knight
		this.pieces[6] = new KnightModel();
		this.pieces[7] = new KnightModel();
		
		//creates Pawns
		for(int i = 8; i < this.pieces.length; i++) {
			this.pieces[i] = new PawnModel();
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
	public PieceModel[] getPieces(){
		return this.pieces;
	}
	
	//Pieces SETTER
	public void setPieces(PieceModel[] pieces) {
		this.pieces = pieces;
	}

	
}
