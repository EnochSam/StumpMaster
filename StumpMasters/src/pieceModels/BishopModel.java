package pieceModels;

import java.util.List;

public class BishopModel extends PieceModel{
	
	public BishopModel() {
		super();
		
	}
	
	public BishopModel(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(PieceModel[][] board){
		return super.getDiagonalMoves(board);
	}
}
