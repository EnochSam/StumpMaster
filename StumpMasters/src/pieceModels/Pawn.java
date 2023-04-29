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
	
	@Override
	public List<Integer[]> getValidMoves(Piece[][] board,int playerColor){
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		Integer[] loc = {-1,-1};
		//White Pawn
		if(super.getColor() == Piece.WHITE){
			if(super.getYpos()+1 <= 7) {
			if(board[super.getYpos()+1][super.getXpos()] ==null) {
				loc = new Integer[2];
				loc[0] = super.getXpos();
				loc[1] = super.getYpos()+1;
				possibleMoves.add(loc);
				if(!super.getHasMovedAlready()) {
					if(board[super.getYpos()+2][super.getXpos()] ==null) {
							loc = new Integer[2];
							loc[1] = super.getYpos()+2;
							loc[0] = super.getXpos();
							possibleMoves.add(loc);
					}
				}
			}
			if(super.getXpos()-1 >= 0) {
				//check left diagonal
				if(board[super.getYpos()+1][super.getXpos()-1] != null){
					if(board[super.getYpos()+1][super.getXpos()-1].getColor() != super.getColor()){
						loc = new Integer[2];
						loc[1] = super.getYpos()+1;
						loc[0] = super.getXpos()-1;
						possibleMoves.add(loc);
					}
				}
			}
			//check right diagonal
			if(super.getXpos()+1 < 8) {
			if(board[super.getYpos()+1][super.getXpos()+1] !=null){
				if(board[super.getYpos()+1][super.getXpos()+1].getColor() != super.getColor()){
					loc = new Integer[2];
					loc[1] = super.getYpos()+1;
					loc[0] = super.getXpos()+1;
					possibleMoves.add(loc);
				}
				
			}
			}
			}
		}
		
		//Black Pawn
		if(super.getYpos()-1 >= 0) {
		if(super.getColor() == Piece.BLACK) {
			if(board[super.getYpos()-1][super.getXpos()] ==null) {
				loc = new Integer[2];
				loc[0] = super.getXpos();
				loc[1] = super.getYpos()-1;
				possibleMoves.add(loc);
				if(!super.getHasMovedAlready()) {
					if(board[super.getYpos()-2][super.getXpos()] ==null){
							loc = new Integer[2];
							loc[1] = super.getYpos()-2;
							loc[0] = super.getXpos();
							possibleMoves.add(loc);
					}
				}
			}
			
			//check left diagonal
			if(super.getXpos()-1 >= 0) {

				if(board[super.getYpos()-1][super.getXpos()-1] !=null){
					if(board[super.getYpos()-1][super.getXpos()-1].getColor() != super.getColor()){
						loc = new Integer[2];
						loc[1] = super.getYpos()-1;
						loc[0] = super.getXpos()-1;
						possibleMoves.add(loc);
					}
				}
			}

			//check right diagonal
			if(super.getXpos()+1 < 8) {
				if(board[super.getYpos()-1][super.getXpos()+1] !=null){

					if(board[super.getYpos()-1][super.getXpos()+1].getColor() != super.getColor()){
						loc = new Integer[2];
						loc[1] = super.getYpos()-1;
						loc[0] = super.getXpos()+1;
						possibleMoves.add(loc);
					}
				}
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
		return "Pawn";
	}
}
