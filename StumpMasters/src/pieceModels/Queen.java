package pieceModels;

import java.util.Collection;
import java.util.List;

public class Queen extends Piece{
	
	public Queen() {
		super();
		
	}
	
	public Queen(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board,int playerColor){
		List<Integer[]> possibleMoves = super.getVerticalMoves(board, playerColor);
		possibleMoves.addAll((Collection<Integer[]>)(super.getDiagonalMoves(board, playerColor)));
		return possibleMoves;
	}

	@Override
	public String type() {
		return "Queen";
	}
}
