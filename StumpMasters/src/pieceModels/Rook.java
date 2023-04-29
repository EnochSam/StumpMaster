package pieceModels;

import java.util.List;

public class Rook extends Piece{
	
	public Rook() {
		super();
		
	}
	
	public Rook(int xpos, int ypos, int color, int ID){
		super(xpos,ypos,color, ID);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		return super.getVerticalMoves(board);
	}

	@Override
	public String type() {
		return "Rook";
	}
}
