package pieceModels;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
	
	public Pawn() {
		super();
		
	}
	
	public Pawn(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		Integer[] loc = {-1,-1};
		
		//White Pawn
		if(super.getColor() == Piece.WHITE) {
			if(board[super.getYpos()-1][super.getXpos()] ==null) {
				loc = new Integer[2];
				loc[0] = super.getYpos()-1;
				loc[1] = super.getXpos();
				possibleMoves.add(loc);
			}
			//check left diagonal
			if(board[super.getYpos()-1][super.getXpos()-1] !=null){
				if(board[super.getYpos()-1][super.getXpos()-1].getColor() != super.getColor()){
					loc = new Integer[2];
					loc[0] = super.getYpos()-1;
					loc[1] = super.getXpos()-1;
					possibleMoves.add(loc);
				}
			}
			
			//check right diagonal
			if(board[super.getYpos()-1][super.getXpos()+1] !=null){
				if(board[super.getYpos()-1][super.getXpos()+1].getColor() != super.getColor()){
					loc = new Integer[2];
					loc[0] = super.getYpos()-1;
					loc[1] = super.getXpos()+1;
					possibleMoves.add(loc);
				}
			}
		}
		
		//Black Pawn
		if(super.getColor() == Piece.BLACK) {
			if(board[super.getYpos()+1][super.getXpos()] ==null) {
				loc = new Integer[2];
				loc[0] = super.getYpos()+1;
				loc[1] = super.getXpos();
				possibleMoves.add(loc);
			}
			
			//check left diagonal
			if(board[super.getYpos()+1][super.getXpos()-1] !=null){
				
				if(board[super.getYpos()+1][super.getXpos()-1].getColor() != super.getColor()){
					loc = new Integer[2];
					loc[0] = super.getYpos()+1;
					loc[1] = super.getXpos()-1;
					possibleMoves.add(loc);
					}
			}
					
			//check right diagonal
			if(board[super.getYpos()+1][super.getXpos()+1] !=null){
				if(board[super.getYpos()+1][super.getXpos()+1].getColor() != super.getColor()){
					loc = new Integer[2];
					loc[0] = super.getYpos()+1;
					loc[1] = super.getXpos()+1;
					possibleMoves.add(loc);
				}
			}
		}
		return possibleMoves;
		
		
	}
}
