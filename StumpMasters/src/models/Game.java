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
	private boolean inCheckmate;
	private boolean pawnPromotion;
	private List<Integer[]> availableMoves = null;
	private String gameMoves;
	private boolean chekForMateInOne = false;
	public Game() {
		
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
	
	public void setInCheckmate(boolean inCheckmate) {
		this.inCheckmate = inCheckmate;
	}
	
	public boolean getInCheckmate() {
		return this.inCheckmate;
	}

	public boolean getPawnPromotion() {
		return this.pawnPromotion;
	}
	
	public void setPawnPromotion(boolean pp) {
		this.pawnPromotion = pp;
	}
	
	public String getGameMoves() {
		return gameMoves;
	}
	
	public void setGameMoves(String gameMoves) {
		this.gameMoves = gameMoves;
	}

	public void setChekForMateInOne(boolean b) {
		this.chekForMateInOne = b;
		
	}
	
	public Boolean getChekForMateInOne() {
		return this.chekForMateInOne;
	}
}
