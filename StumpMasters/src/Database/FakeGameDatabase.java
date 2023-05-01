package Database;

import models.Player;
import pieceModels.Bishop;
import pieceModels.Knight;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

public class FakeGameDatabase implements IDatabase{

	public Player whitePlayer;
	public Player blackPlayer;
	public Player[] player;
	public Piece[][] board;
	
	public FakeGameDatabase() {
		whitePlayer = new Player(Piece.WHITE);
		blackPlayer = new Player(Piece.BLACK);
		player =new Player[]{whitePlayer,blackPlayer};
		//initializes  Pieces
		for(int i = 0; i <= 1; i++) {
		//Kings	
		player[i].getPieces()[0].setColor(i);
		player[i].getPieces()[0].setXpos(4);
		player[i].getPieces()[0].setYpos(i*7);
		//Queens
		player[i].getPieces()[1].setColor(i);
		player[i].getPieces()[1].setXpos(3);
		player[i].getPieces()[1].setYpos(i*7);
		
		//Rooks
		player[i].getPieces()[2].setColor(i);
		player[i].getPieces()[2].setXpos(7);
		player[i].getPieces()[2].setYpos(i*7);
		player[i].getPieces()[3].setColor(i);
		player[i].getPieces()[3].setXpos(0);
		player[i].getPieces()[3].setYpos(i*7);
		
		//Bishops
		player[i].getPieces()[4].setColor(i);
		player[i].getPieces()[4].setXpos(2);
		player[i].getPieces()[4].setYpos(i*7);
		player[i].getPieces()[5].setColor(i);
		player[i].getPieces()[5].setXpos(5);
		player[i].getPieces()[5].setYpos(i*7);
		
		//Knights
		player[i].getPieces()[6].setColor(i);
		player[i].getPieces()[6].setXpos(1);
		player[i].getPieces()[6].setYpos(i*7);
		player[i].getPieces()[7].setColor(i);
		player[i].getPieces()[7].setXpos(6);
		player[i].getPieces()[7].setYpos(i*7);
		
		for(int j = 0; j < 8; j++) {
			int ypos = 6;
			if(i == 0) ypos = 1;
			player[i].getPieces()[8+j].setColor(i);
			player[i].getPieces()[8+j].setXpos(j);
			player[i].getPieces()[8+j].setYpos(ypos);
		}
		}
	}

	
	@Override
	public Piece[][] populateBoard(String boardLocations) {
		Piece[][] board = new Piece[8][8];
		Player[] players = player;

		
		//load initially on board
		for(int i = 0; i < players.length; i++){

			for(int j = 0; j < players[i].getPieces().length; j++) {
				
				Piece tempPiece= players[i].getPieces()[j];
				int tempPieceX =tempPiece.getXpos();
				int tempPieceY =tempPiece.getYpos();
				board[tempPieceY][tempPieceX] = tempPiece;
			}
			
		}
		//BOARD LOCATIONS FORMULA
		//STRING = 1234 1234 1234
		//1= xpos of current location
		//2= ypos of current location
		//3= xpos of new location
		//4 = ypos of new location
		//5 = space

		for(int i = 0; i < boardLocations.length() & boardLocations.length()-i >5; i+=5) {
			int curx = Integer.parseInt(""+boardLocations.charAt(i))-1;
			int cury = Integer.parseInt(""+boardLocations.charAt(i+1))-1;
			int newx = Integer.parseInt(""+boardLocations.charAt(i+2))-1;
			int newy = Integer.parseInt(""+boardLocations.charAt(i+3))-1;
			
			//if piece is on moved location set that piece to captured
			if(board[newy][newx] != null) {
				board[newy][newx].setCaptured(true);
			}
			board[newy][newx] = board[cury][curx];
			board[cury][curx] = null;
			board[newy][newx].setXpos(newx);
			board[newy][newx].setYpos(newy);
			board[newy][newx].setHasMovedAlready(true);
			
			//Check For Pawn Promotion
			if(i+4 <boardLocations.length() ) {
				if(!(""+boardLocations.charAt(i+4)).equals(" ")) {
					int color = board[newy][newx].getColor();
					//Check Which Character the Pawn is Set To
					if((""+boardLocations.charAt(i+4)).equals("Q")){
						board[newy][newx] = new Queen();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("R")){
						board[newy][newx] = new Rook();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setColor(color);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("K")){
						board[newy][newx] = new Knight();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					if((""+boardLocations.charAt(i+4)).equals("B")){
						board[newy][newx] = new Bishop();
						board[newy][newx].setXpos(newx);
						board[newy][newx].setYpos(newy);
						board[newy][newx].setColor(color);
						board[newy][newx].setHasMovedAlready(true);
					}
					i++;
				}
			}
		}
		this.board = board;
		return board;
	}


	@Override
	public void resetLocations() {
		// Since the Fake Database is continuously reset, there is no implementations to be made
		
	}


	
	@Override
	public Player populatePlayer(int playerColor) {
		if(playerColor == Piece.WHITE) {
			return this.whitePlayer;
		}else {
			return this.blackPlayer;
		}
	}


	@Override
	public Piece getPiece(int pieceXpos, int pieceYpos) {
		return this.board[pieceYpos][pieceXpos];
	}


	@Override
	public void updateDatabaseForCastling(int rookXpos, int rookYpos) {
		
	}


	@Override
	public void updateDatabaseForEnPassant(int xpos, int ypos, int color) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateDatabase(int oldX, int oldY, int newX, int newY) {
		// TODO Auto-generated method stub
		
	}

}
