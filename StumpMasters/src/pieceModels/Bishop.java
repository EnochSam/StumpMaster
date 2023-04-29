package pieceModels;

import java.util.List;

public class Bishop extends Piece{
	
	public Bishop() {
		super();
		
	}
	
	public Bishop(int xpos, int ypos, int color, int ID){
		super(xpos,ypos,color, ID);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		return super.getDiagonalMoves(board);
	}

	@Override
	public String type() {
		return "Bishop";
	}
}
