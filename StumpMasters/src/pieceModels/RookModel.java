package pieceModels;

import java.util.List;

public class RookModel extends PieceModel{
	
	public RookModel() {
		super();
		
	}
	
	public RookModel(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(PieceModel[][] board){
		return super.getVerticalMoves(board);
	}
}
