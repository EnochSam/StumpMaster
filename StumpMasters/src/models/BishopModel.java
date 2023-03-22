package models;

import java.util.ArrayList;
import java.util.List;

public class BishopModel extends PieceModel{
	
	public BishopModel() {
		super();
		
	}
	
	public BishopModel(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(PieceModel[][] board){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse northwest of Rook
		int x = super.getXpos();
		int y = super.getYpos();
		Integer[] loc = {-1,-1};
		while(y >= 0 && x >= 0) {
			if(board[y][x] != null) {
				//checks if piece is same color
				if(board[y][x].getColor() == super.getColor()) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != super.getColor()) x=0;
			}
			x--;
			y--;
		}
		
		//Traverse northeast of Rook
		x = super.getXpos();
		y = super.getYpos();
		while(y >= 0 && x <= 7){
			if(board[y][x] != null) {
				//checks if piece is same color
				if(board[y][x].getColor() == super.getColor()) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != super.getColor()) x=7;
			}
			x++;
			y--;
		}
		
		//Traverse southeast of Rook
		y = super.getYpos();
		x = super.getXpos();
		while(y <= 7 && x <= 7) {
		if(board[y][x] != null) {
		//checks if piece is same color
		if(board[y][x].getColor() == super.getColor()) break;
		}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != super.getColor()) y=0;
			}
			y++;
			x++;
		}
		
		//Traverse souhtwest of Rook
		y = super.getYpos();
		x = super.getXpos();
		while(y <= 7 && x >= 0) {
		if(board[y][x] != null) {
		//checks if piece is same color
		if(board[y][x].getColor() == super.getColor()) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			
			if(board[y][x] != null) {
				if(board[y][x].getColor() != super.getColor()) y=7;
			}
			y++;
			x--;
		}
		
		return possibleMoves;
		
		
	}
}
