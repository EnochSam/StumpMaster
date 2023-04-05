package models;

import java.util.List;

import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

//import pieceModels.PieceModel;

public class Game {
	private Player white;
	private Player black;
	private Player[] player;
	private Piece[][] board;
	private boolean inCheck;
	private List<Integer[]> availableMoves = null;
	public Game() {
		white = new Player(Piece.WHITE);
		black = new Player(Piece.BLACK);
		player =new Player[]{white,black};
		//initializes  Pieces
		for(int i = 0; i <= 1; i++) {
		//Kings	
		player[i].getPieces()[0].setColor(i);
		player[i].getPieces()[0].setXpos(3);
		player[i].getPieces()[0].setYpos(i*7);
		//Queens
		player[i].getPieces()[1].setColor(i);
		player[i].getPieces()[1].setXpos(4);
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
	
	public Player getWhitePlayer() {
		return white;
	}
	
	public void setWhitePlayer(Player whitePlayer) {
		this.white = whitePlayer;
	}
	
	public Player getBlackPlayer(){
		return black;
	}
	
	public void setBlackPlayer(Player blackPlayer) {
		this.black = blackPlayer;
	}
	
	public Piece[][] getBoard(){
		return this.board;
	}
	
	public void setBoard(Piece[][] board){
		this.board = board;
	}
	
	public Player[] getBothPlayers() {
		return player;
	}
	
	public void setInCheck(boolean inCheck) {
		this.inCheck = inCheck;
	}
	
	public boolean getInCheck() {
		return this.inCheck;
	}
	public void setAvailableMoves(List<Integer[]> availableMoves){
		this.availableMoves = availableMoves;
	}
	public List<Integer[]> getAvailableMoves(){
		return this.availableMoves;
	}
	

	
}
