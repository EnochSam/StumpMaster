package chessGame;
import 

public class GameModel {
	Player white;
	Player black;
	Player[] player= {white,black};
	int[][] board;
	
	createGame(){
		this.white = new Player();
		this.black = new Player();
		this.board = new int[8][8];
		
		
	}
	
}
