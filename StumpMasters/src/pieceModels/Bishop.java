package pieceModels;

import java.util.List;

public class Bishop extends Piece{
	
	public Bishop() {
		super();
		
	}
	
	public Bishop(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board,int playerColor){
		return super.getDiagonalMoves(board, playerColor);
	}

	@Override
	public String type() {
		return "Bishop";
	}
}
