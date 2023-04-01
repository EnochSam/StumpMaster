package pieceModels;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
	public final static int BLACK = 1;
	public final static int WHITE = 0;
	private int xpos;
	private int ypos;
	private int color;
	private boolean captured;
	private boolean hasMovedAlready;
	public Piece(){
		xpos = -1;
		ypos = -1;
		this.captured = false;
	}
	
	public Piece(int xpos, int ypos, int color){
		this.xpos = xpos;
		this.ypos = ypos;
		this.color = color;
		this.captured = false;
		this.hasMovedAlready = false; 
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
	
	public boolean getCaptured() {
		return this.captured;
	}
	
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}
	
	public void setHasMovedAlready(boolean hasMovedAlready) {
		this.hasMovedAlready = hasMovedAlready;
	}
	
	public boolean getHasMovedAlready() {
		return this.hasMovedAlready;
	}
	public abstract List<Integer[]> getValidMoves(Piece[][] board);
	
	public List<Integer[]> getVerticalMoves(Piece[][] board){
List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse Left of Rook
		int x = xpos-1;
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
		x = xpos+1;
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
		int y = ypos-1;
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
		y = ypos+1;
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
	
	public List<Integer[]> getDiagonalMoves(Piece[][] board){
	List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//Traverse southwest of Rook
		int x = xpos-1;
		int y = ypos-1;
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
		
		//Traverse southeast of Rook
		x = xpos+1;
		y = ypos-1;
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
		y = ypos+1;
		x = xpos+1;
		while(y <= 7 && x <= 7) {
			if(board[y][x] != null) {
				//checks if piece is same color
				if(board[y][x].getColor() == color) {
					break;
				}
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
		y = ypos+1;
		x = xpos-1;
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
