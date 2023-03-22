package models;

public class GameModel {
	PlayerModel white;
	PlayerModel black;
	PlayerModel[] player= {white,black};
	int[][] board;
	
	public void createGame(){
		this.white = new PlayerModel();
		this.black = new PlayerModel();
		this.board = new int[8][8];
		
		
	}
	
}
