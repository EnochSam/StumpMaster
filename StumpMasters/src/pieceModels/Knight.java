package pieceModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knight extends Piece{
	
	public Knight() {
		super();
		
	}
	
	public Knight(int xpos, int ypos, int color){
		super(xpos,ypos,color);
	}
	
	public List<Integer[]> getValidMoves(Piece[][] board,int playerColor){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		
		//X,Y locations to check are loaded into list
		List<Integer> listOfLocationsToCheck = Arrays.asList(2,1,2,-1,1,2,1,-2,-1,2,-1,-2,-2,1,-2,-1);
		Integer[] loc = {-1,-1};
		for(int i = 0; i < listOfLocationsToCheck.size(); i+=2){
			int x = super.getXpos()+listOfLocationsToCheck.get(i);
			int y = super.getYpos()+listOfLocationsToCheck.get(i+1);
			if(y>=0 && y < 8 && x>=0 && x<8) {
				if(board[y][x] !=null) {
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
				}
			}
		}
		//Check to make sure move will not jeapardize King
				if(super.getColor() == playerColor) {
					int origx = super.getXpos();
						int origy = super.getYpos();
						Piece self = board[super.getYpos()][super.getXpos()];
						board[super.getYpos()][super.getXpos()] = null;
						for(int i = 0; i< possibleMoves.size(); i++) {
							//ONLY CALL CHECKFORCHECK ON ENEMY TEAM
							Integer[] locs = possibleMoves.get(i);
							super.setXpos(locs[0]);
							super.setYpos(locs[1]);
							Piece oldPiece = board[super.getYpos()][super.getXpos()]; 
							board[super.getYpos()][super.getXpos()] = self;
							//If this piece is the color that needs to call check for check, call it
							
							System.out.println("Checking Position "+super.getXpos()+":"+super.getYpos()+" if valid move");
							if(this.checkForCheck(board, playerColor)) {
								possibleMoves.remove(locs);
								i--;
								System.out.println(""+super.getXpos()+":"+super.getYpos()+" will lead to check!");
							
							}
							board[super.getYpos()][super.getXpos()] = oldPiece;
							
						}
						super.setXpos(origx);
						super.setYpos(origy);
						board[super.getYpos()][super.getXpos()] = self;
				}
		return possibleMoves;
		
		
	}

	
	@Override
	public String type() {
		return "Knight";
	}
}
