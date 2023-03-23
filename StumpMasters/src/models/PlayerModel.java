package models;
import pieceModels.BishopModel;
import pieceModels.PawnModel;
import pieceModels.PieceModel;
import pieceModels.QueenModel;
import pieceModels.RookModel;

public class PlayerModel {
	private PieceModel[] pieces;
	public PlayerModel(int color) {
		//initializes Pieces
		pieces = new PieceModel[16];
		
		//King ==0
		//Queen == 1
		//Rook == 2,3
		//Bishop == 4,5
		//Knight == 6,7
		//Pawn == 8-15
		//creates Pawns
		
		//PlaceHolderFor King
		this.pieces[0] = new PawnModel();
		
		this.pieces[1] = new QueenModel();
		
		this.pieces[2] = new RookModel();
		this.pieces[3] = new RookModel();
		
		this.pieces[4] = new BishopModel();
		this.pieces[5] = new BishopModel();

		//PlaceHolderFor Knight
		this.pieces[6] = new PawnModel();
		this.pieces[7] = new PawnModel();
		
		for(int i = 8; i < this.pieces.length; i++) {
			this.pieces[i] = new PawnModel();
		}		
	}
}
