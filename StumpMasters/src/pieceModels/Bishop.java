package pieceModels;

import java.util.List;

public class Bishop extends Piece{
	
	public Bishop() {
		super();
		
	}
	
	public Bishop(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		return super.getDiagonalMoves(board);
	}
}
