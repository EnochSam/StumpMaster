package pieceModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends Piece{
	private boolean inCheck;
	private boolean inCheckMate;
	private List<Integer[]> getOutOfCheckMoves;
	public King() {
		super();
		
	}
	
	public King(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public boolean checkForCheckMate(Piece[][] board){	
		//3 WAYS TO Instances of Checkmate:
		//1. Can the king move out of the way
		//2. Can a piece of same color reach the opposing location reach opposing piece
		
		//1. create list of pieces of opposing team that can reach king
		Piece piecesthatcanReachKing = null;
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				if(board[j][i] != null) {
					boolean canreach = false;
					for(Integer[] loc : board[j][i].getValidMoves(board)) {
						if(loc[0] == super.getXpos() && loc[1] == super.getYpos()) {
							
							canreach = true;

						}
					}
					if(canreach) {
						piecesthatcanReachKing = board[j][i];
					}
				}
			}
		}
		if(piecesthatcanReachKing == null) {
			//If the King was in check, it no longer is, so set inCheck to false
			if(this.inCheck){
				this.inCheck = false;
			}
			//The King will now determine the distance between the attacking Piece by checking its position
			
			//Check if the Piece is Attacking Vertically
			
			return false;
		}
		// There is an opposing Piece, So the Player is in Check
		this.inCheck = true;
		//Adds Location of attacking Piece to getOutOfCheckMovesList
		Integer[] availableMove = {piecesthatcanReachKing.getXpos(),piecesthatcanReachKing.getYpos()};
		getOutOfCheckMoves = new ArrayList<Integer[]>();
		getOutOfCheckMoves.add(availableMove);
		//Check if the piece is attacking vertically or diagonal horizontally 
		if(piecesthatcanReachKing.getXpos() == super.getXpos() || piecesthatcanReachKing.getYpos() == super.getYpos()
			|| Math.abs(piecesthatcanReachKing.getXpos() - super.getXpos())
			== Math.abs(piecesthatcanReachKing.getYpos() - super.getYpos())) {
			int xincrement = 0;
			if(piecesthatcanReachKing.getXpos() - super.getXpos() > 0) xincrement = 1;
			if(piecesthatcanReachKing.getXpos() - super.getXpos() < 0) xincrement = -1;
			int yincrement = 0;
			if(piecesthatcanReachKing.getYpos() - super.getYpos() > 0) yincrement = 1;
			if(piecesthatcanReachKing.getYpos() - super.getYpos() < 0) yincrement = -1;
			int x = super.getXpos()+xincrement;
			int y = super.getYpos()+yincrement;

			while(board[y][x] != piecesthatcanReachKing) {
				Integer[] loc = {x,y};
				this.getOutOfCheckMoves.add(loc);
				x+=xincrement;
				y+=yincrement;
			}
		}
		//finally, check all pieces to see if any piece is able to reach any moves
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				if(board[j][i] != null) {
					for(Integer[] loc : board[j][i].getValidMoves(board)) {
						for(Integer[] possibleMoves : this.getOutOfCheckMoves) {
							if(loc[0] == possibleMoves[0] && loc[1] == possibleMoves[1]) {			
								return false;
							}
						}
					}
				}
			}
		}
		System.out.println("CHECKMATE");
		return true;
	}
	public Boolean getInCheck() {
		return inCheck;
	}
	public void setInCheck(boolean inCheck) {
		this.inCheck = inCheck;
	}
	public List<Integer[]> getGetOutOfCheckMoves(){
		return this.getOutOfCheckMoves;
	}
	public List<Integer[]> getValidMoves(Piece[][] board){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//X,Y locations to check are loaded into list
		List<Integer> listOfLocationsToCheck = Arrays.asList(0,1,0,-1,1,0,-1,0,1,1,-1,-1,-1,1,1,-1);
		Integer[] loc = {-1,-1};
		for(int i = 0; i < listOfLocationsToCheck.size(); i+=2){
			int x = super.getXpos()+listOfLocationsToCheck.get(i);
			int y = super.getYpos()+listOfLocationsToCheck.get(i+1);
			if(y>=0 && y < 8 && x>=0 && x<8) {
				//if(this.checkIfOpenMove(x, y, board)){
				if(board[y][x] !=null ) {
					if(board[y][x].getColor() != super.getColor()) {
						loc = new Integer[2];
						loc[0] = x;
						loc[1] = y;
						possibleMoves.add(loc);
					}
					
				}else {
					loc = new Integer[2];
					loc[0] = x;
					loc[1] = y;
					possibleMoves.add(loc);
				//}
				}
			}
		}
		return possibleMoves;
	}

	
	@Override
	public String type() {
		return "King";
	}
	
	
}
