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
		this.color = -1;
		this.captured = false;
		this.hasMovedAlready = false; 

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
	public abstract List<Integer[]> getValidMoves(Piece[][] board,int playerColor);
	
	//Used i Database
	public abstract String type();
	
	public List<Integer[]> getVerticalMoves(Piece[][] board,int playerColor){
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
		
		//Check to make sure move will not jeapardize King
		int origx = xpos;
		int origy = ypos;
		Piece self = board[ypos][xpos];
		board[ypos][xpos] = null;
		for(int i = 0; i< possibleMoves.size(); i++) {
			//ONLY CALL CHECKFORCHECK ON ENEMY TEAM
			Integer[] locs = possibleMoves.get(i);
			xpos = locs[0];
			ypos = locs[1];
			Piece oldPiece = board[ypos][xpos]; 
			board[ypos][xpos] = self;
			//If this piece is the color that needs to call check for check, call it
			if(this.color == playerColor) {
			if(this.checkForCheck(board, playerColor)) {
				possibleMoves.remove(i);
				i--;
			}
			}
			board[ypos][xpos] = oldPiece;
			
		}
		xpos = origx;
		ypos = origy;
		board[ypos][xpos] = self;
		return possibleMoves;
		
		
	}
	
	public List<Integer[]> getDiagonalMoves(Piece[][] board, int playerColor){
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
				if(board[y][x].getColor() != color) y=7;
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
		
		//Check to make sure move will not jeapardize King
		if(this.color == playerColor) {
			int origx = xpos;
				int origy = ypos;
				Piece self = board[ypos][xpos];
				board[ypos][xpos] = null;
				for(int i = 0; i< possibleMoves.size(); i++) {
					//ONLY CALL CHECKFORCHECK ON ENEMY TEAM
					Integer[] locs = possibleMoves.get(i);
					xpos = locs[0];
					ypos = locs[1];
					Piece oldPiece = board[ypos][xpos]; 
					board[ypos][xpos] = self;
					//If this piece is the color that needs to call check for check, call it
					
					if(this.checkForCheck(board, playerColor)) {
						possibleMoves.remove(locs);
						i--;
					
					}
					board[ypos][xpos] = oldPiece;
					
				}
				xpos = origx;
				ypos = origy;
				board[ypos][xpos] = self;
		}
				return possibleMoves;
		
		
	}

	public King getKing(Piece[][] board) {
		
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				if(board[j][i] instanceof King && board[j][i].getColor() == color) {
					return (King)board[j][i];
				}
			}
		}
		return null;
	}
	
	public Boolean checkForCheck(Piece[][] board,int playerColor) {
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				if(board[j][i] != null) {
					if(board[j][i].getColor() != playerColor){
					boolean canreach = false;
					King king = this.getKing(board);
					for(Integer[] loc : board[j][i].getValidMoves(board,playerColor)) {
						if(loc[0] == king.getXpos() && loc[1] == king.getYpos()) {
							
							canreach = true;

						}
						if(board[j][i] instanceof Queen) {
						}
					}
					if(canreach) {
						king.setInCheck(false);
						return true;
					}
				}
				}
			}
		}
		return false;
	}
	//used in the load initial data of the Database
	public static Piece findPiece(String pieceName) {
		if(pieceName.equals("King")){
			return new King();
		}
		if(pieceName.equals("Queen")){
			return new Queen();
		}
		if(pieceName.equals("Bishop")) {
			return new Bishop();
		}
		if(pieceName.equals("Rook")) {
			return new Rook();
		}
		if(pieceName.equals("Knight")){
			return new Knight();
		}
		if(pieceName.equals("Pawn")) {
			return new Pawn();
		}
		return null;
	}
	
}
