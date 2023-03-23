package pieceModels;

import java.util.Collection;
import java.util.List;

public class QueenModel extends PieceModel{
	
	public QueenModel() {
		super();
		
	}
	
	public QueenModel(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(PieceModel[][] board){
		List<Integer[]> possibleMoves = super.getVerticalMoves(board);
		possibleMoves.addAll((Collection<Integer[]>)(super.getDiagonalMoves(board)));
		return possibleMoves;
	}
}
