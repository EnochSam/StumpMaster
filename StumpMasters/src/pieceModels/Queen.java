package pieceModels;

import java.util.Collection;
import java.util.List;

public class Queen extends Piece{
	
	public Queen() {
		super();
		
	}
	
	public Queen(int xpos, int ypos, int color, int ID){
		super(xpos,ypos,color, ID);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		List<Integer[]> possibleMoves = super.getVerticalMoves(board);
		possibleMoves.addAll((Collection<Integer[]>)(super.getDiagonalMoves(board)));
		return possibleMoves;
	}

	@Override
	public String type() {
		return "Queen";
	}
}
