package pieceModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends Piece{
	private boolean inCheck;
	private boolean inCheckMate;
	private List<Integer[]> getOutOfCheckMoves;
	//Due to the amount of times the castling function is con due to the set board method,
	//The CastlingLocatoin needs to be an array contain the left and right locations
	private String[] castlingLocation = new String[2];
	public King() {
		super();
		
	}
	
	public King(int xpos, int ypos, int color, int id){
		super(xpos,ypos,color, id);
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
			return false;
		}
		// There is an opposing Piece, So the Player is in Check
		if(piecesthatcanReachKing.getColor() != super.getColor()) {
		this.inCheck = true;
		}
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
		
		//Checks For Castling
		for(Integer[] castleLoc : this.castling(board)) {
			possibleMoves.add(castleLoc);
		}
		return possibleMoves;
	}

	public List<Integer[]> castling(Piece board[][]) {
		List<Integer[]> locs = new ArrayList<Integer[]>();
		//check for of Castling
		if(!super.getHasMovedAlready() && !this.inCheck) {
			//check Left of King
			//assuming that the king hasn't move, if theres a rook at a specific x pos, then the rook hasnt moved either
			if(board[super.getYpos()][0] != null) {
				if(board[super.getYpos()][0] instanceof Rook && board[super.getYpos()][0].getColor() == super.getColor()) {
					//traverses to the Rook
					int origX = super.getXpos();
					//Removing King from current location
					Piece self = board[super.getYpos()][origX];
					board[super.getYpos()][origX] = null;
					
					int i = super.getXpos();
					boolean allowedToCastle = true;
					while(i > 0) {
						//move King over to make sure it wouldn't be in check
						super.setXpos(i);
						if(board[super.getYpos()][super.getXpos()] != null) {
							allowedToCastle = false;
							break;
						}
						//NOT ADDING KING BACK TO BOARD to Call the Check For Check Function
						this.checkForCheckMate(board);
						//If King Can Be Put in Check, set Boolean to false 
						if(this.inCheck){
							allowedToCastle =  false;
						}
						i--;
						 
					}
					//Add King Back to board and set check back to 0
					board[super.getYpos()][origX] = self;
					super.setXpos(origX);
					this.inCheck = false;
					
					//At This Point, King Is Allowed to Castle, So add new position to list
					if(allowedToCastle) {
					Integer[] castleToLeft= {super.getXpos()-2,super.getYpos()};
					locs.add(castleToLeft);
					
					castlingLocation[0] = (castleToLeft[0]+""+castleToLeft[1]);
					}
				}
			} 
			if(board[super.getYpos()][7] != null) {
				if(board[super.getYpos()][7] instanceof Rook && board[super.getYpos()][7].getColor() == super.getColor()) {
					//traverses to Right of the Rook
					int origX = super.getXpos();
					//Removing King from current location
					Piece self = board[super.getYpos()][origX];
					board[super.getYpos()][origX] = null;
					
					int i = super.getXpos();
					boolean allowedToCastle = true;
					while(i < 7  ) {
						//move King over to make sure it wouldn't be in check
						super.setXpos(i);
						
						if(board[super.getYpos()][super.getXpos()] != null) {
							allowedToCastle = false;
							break;
						}
						//NOT ADDING KING BACK TO BOARD to Call the Check For Check Function
						this.checkForCheckMate(board);
						//If King Can Be Put in Check, set Boolean to false 
						if(this.inCheck){
							allowedToCastle =  false;
						}
						i++;
						 
					}
					//Add King Back to board and set check back to 0
					board[super.getYpos()][origX] = self;
					super.setXpos(origX);
					this.inCheck = false;
					
					//At This Point, King Is Allowed to Castle, So add new position to list
					if(allowedToCastle) {
					Integer[] castleToRigt= {super.getXpos()+2,super.getYpos()};
					locs.add(castleToRigt);
					castlingLocation[1]=(castleToRigt[0]+""+castleToRigt[1]);
					}
				}
			} 
		}
		return locs;
	}
	
	@Override
	public String type() {
		return "King";
	}
	
	public String[] getCastlingLocation() {
		return this.castlingLocation;
	}
	
	
}
