package PieceModels;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece{
	
	public Rook() {
		super();
		
	}
	
	public Rook(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse Left of Rook
		int x = super.getXpos();
		while(x >= 0) {
			if(board[super.getYpos()][x] != null) {
				//checks if piece is same color
				if(board[super.getYpos()][x].getColor() == super.getColor()) break;
			}
			Integer[] loc = {x,super.getYpos()};
			possibleMoves.add(loc);
			if(board[super.getYpos()][x].getColor() != super.getColor()) x=0;
			x--;
		}
		
		//Traverse Right of Rook
		x = super.getXpos();
		while(x <= 7){
			if(board[super.getYpos()][x] != null) {
				//checks if piece is same color
				if(board[super.getYpos()][x].getColor() == super.getColor()) break;
			}
			Integer[] loc = {x,super.getYpos()};
			possibleMoves.add(loc);
			if(board[super.getYpos()][x].getColor() != super.getColor()) x=7;
			x++;
		}
		
		//Traverse Up of Rook
		int y = super.getYpos();
		while(y >= 0) {
		if(board[y][super.getXpos()] != null) {
		//checks if piece is same color
		if(board[y][super.getXpos()].getColor() == super.getColor()) break;
		}
		Integer[] loc = {y,super.getXpos()};
			possibleMoves.add(loc);
			if(board[y][super.getXpos()].getColor() != super.getColor()) y=0;
			y--;
		}
		
		//Traverse Down of Rook
		y = super.getYpos();
		while(y <= 7) {
		if(board[y][super.getXpos()] != null) {
		//checks if piece is same color
		if(board[y][super.getXpos()].getColor() == super.getColor()) break;
			}
			Integer[] loc = {y,super.getXpos()};
			possibleMoves.add(loc);
			if(board[y][super.getXpos()].getColor() != super.getColor()) y=7;
			y++;
		}
		
		return possibleMoves;
		
		
	}
}
