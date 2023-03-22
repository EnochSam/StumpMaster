package pieceModels;

import java.util.ArrayList;
import java.util.List;

public abstract class PieceModel {
	public final static int BLACK = 0;
	public final static int WHITE = 1;
	private int xpos;
	private int ypos;
	private int color;
	
	public PieceModel(){
		xpos = -1;
		ypos = -1;
	}
	
	public PieceModel(int xpos, int ypos, int color){
		this.xpos = xpos;
		this.ypos = ypos;
		this.color = color;
	}
	
	public int getXpos() {
		return this.xpos;
	}
	
	public int getYpos() {
		return this.ypos;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public void setXpos(int xPos) {
		this.xpos = xPos;
	}
	
	public void setYpos(int yPos) {
		this.ypos = yPos;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	public void setPosition(int xpos, int ypos){
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public abstract List<Integer[]> getValidMoves(PieceModel[][] board);
	
	public List<Integer[]> getVerticalMoves(PieceModel[][] board){
List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse Left of Rook
		int x = xpos;
		Integer[] loc = {-1,-1};
		while(x >= 0) {
			if(board[ypos][x] != null) {
				//checks if piece is same color
				if(board[ypos][x].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = ypos;
			possibleMoves.add(loc);
			if(board[ypos][x] != null) {
				if(board[ypos][x].getColor() != color) x=0;
			}
			x--;
		}
		
		//Traverse Right of Rook
		x = xpos;
		while(x <= 7){
			if(board[ypos][x] != null) {
				//checks if piece is same color
				if(board[ypos][x].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = ypos;
			possibleMoves.add(loc);
			if(board[ypos][x] != null) {
				if(board[ypos][x].getColor() != color) x=7;
			}
			x++;
		}
		
		//Traverse Up of Rook
		int y = ypos;
		while(y >= 0) {
		if(board[y][xpos] != null) {
		//checks if piece is same color
		if(board[y][xpos].getColor() == color) break;
		}
			loc = new Integer[2];
			loc[0] = xpos;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][xpos] != null) {
				if(board[y][xpos].getColor() != color) y=0;
			}
			y--;
		}
		
		//Traverse Down of Rook
		y = ypos;
		while(y <= 7) {
		if(board[y][xpos] != null) {
		//checks if piece is same color
		if(board[y][xpos].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = xpos;
			loc[1] = y;
			possibleMoves.add(loc);
			
			if(board[y][xpos] != null) {
				if(board[y][xpos].getColor() != color) y=7;
			}
			y++;
		}
		
		return possibleMoves;
		
		
	}
	
	public List<Integer[]> getDiagonalMoves(PieceModel[][] board){
	List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse northwest of Rook
		int x = xpos;
		int y = ypos;
		Integer[] loc = {-1,-1};
		while(y >= 0 && x >= 0) {
			if(board[y][x] != null) {
				//checks if piece is same color
				if(board[y][x].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != color) x=0;
			}
			x--;
			y--;
		}
		
		//Traverse northeast of Rook
		x = xpos;
		y = ypos;
		while(y >= 0 && x <= 7){
			if(board[y][x] != null) {
				//checks if piece is same color
				if(board[y][x].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != color) x=7;
			}
			x++;
			y--;
		}
		
		//Traverse southeast of Rook
		y = ypos;
		x = xpos;
		while(y <= 7 && x <= 7) {
		if(board[y][x] != null) {
		//checks if piece is same color
		if(board[y][x].getColor() == color) break;
		}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			if(board[y][x] != null) {
				if(board[y][x].getColor() != color) y=0;
			}
			y++;
			x++;
		}
		
		//Traverse souhtwest of Rook
		y = ypos;
		x = xpos;
		while(y <= 7 && x >= 0) {
		if(board[y][x] != null) {
		//checks if piece is same color
		if(board[y][x].getColor() == color) break;
			}
			loc = new Integer[2];
			loc[0] = x;
			loc[1] = y;
			possibleMoves.add(loc);
			
			if(board[y][x] != null) {
				if(board[y][x].getColor() != color) y=7;
			}
			y++;
			x--;
		}
		
		return possibleMoves;
		
		
	}
	
}
