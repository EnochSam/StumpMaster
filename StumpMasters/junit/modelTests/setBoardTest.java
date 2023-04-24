package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import controllers.GameController;
import models.Game;
import models.Player;
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

public class setBoardTest {
	@Test
	public void testOriginalSetup(){
		Game testModel = new Game();
		GameController testController = new GameController();
		testController.setModel(testModel);
		testController.setBoard("");
		Piece[][] exampleBoard = new Piece[8][8];
		for(Player p : testModel.getBothPlayers()) {
			for(Piece piece : p.getPieces()) {
				exampleBoard[piece.getYpos()][piece.getXpos()] = piece;
			}
		}
		assertTrue(sameBoard(exampleBoard,testModel.getBoard()));
	}
	
	@Test
	public void testRandomSetup1() {
		Game testModel = new Game();
		GameController testController = new GameController();
		testController.setModel(testModel);
		testController.setBoard("6264 5755 7273 6766 6455 6655 4244 5544 6172 6857 7183 5735 8161 5171 4443 5253 4342 3142 3553 4253 7866 6166 2725 2133 2816 3325 3827 7227 1635 5386 7786  ");
		
		Piece[][] exampleBoard = new Piece[8][8];
		List<Piece> piecesToAddToBoard = new ArrayList<Piece>();
		piecesToAddToBoard.addAll(Arrays.asList(new Rook(0,7,1),
				new Queen(3,7,1), new King(4,7,1), new Rook(7,7,1),
				new Pawn(0,6,1), new Bishop(1,6,0),new Pawn(2,6,1), 
				new Pawn(3,6,1), new Pawn(7,6,1), new Rook(5,5,0),
				new Pawn(7,5,1), new Knight(1,4,0), new Knight(2,4,1),
				new Pawn(6,2,0), new Knight(7,2,0), new Pawn(0,1,0),
				new Pawn(1,1,0), new Pawn(2,1,0), new Pawn(7,1,0),
				new Rook(0,0,0), new Queen(3,0,0), new King(6,0,0)));
		
		for(Piece piece :piecesToAddToBoard) {
			exampleBoard[piece.getYpos()][piece.getXpos()] = piece;
		}
		sameBoard(exampleBoard,testModel.getBoard());
	}
	
	@Test
	public void testRandomSetup2() {
		Game testModel = new Game();
		GameController testController = new GameController();
		testController.setModel(testModel);
		testController.setBoard("6264 5755 7273 6766 6455 6655 4244 5544 6172 6857 7183 5735 8161 5171 4443 5253 4342 3142 3553 4253 7866 6166 2725 2133 2816 3325 3827 7227 1635 5386 7786 8375 4745 4151 5847 5141 4544 4151 4443 5141 4342 4151 4251Q 47");
		
		Piece[][] exampleBoard = new Piece[8][8];
		List<Piece> piecesToAddToBoard = new ArrayList<Piece>();
		piecesToAddToBoard.addAll(Arrays.asList(new Rook(0,7,1),
				new Queen(3,7,1), new Rook(7,7,1),
				new Pawn(0,6,1), new Bishop(1,6,0),new Pawn(2,6,1), 
				new King(3,6,1), new Pawn(7,6,1), new Rook(5,5,0),
				new Pawn(7,5,1), new Knight(1,4,0), new Knight(2,4,1),
				new Pawn(6,2,0), new Knight(6,4,0), new Pawn(0,1,0),
				new Pawn(1,1,0), new Pawn(2,1,0), new Pawn(7,1,0),
				new Rook(0,0,0), new Queen(4,0,1), new King(6,0,0)));
		
		for(Piece piece :piecesToAddToBoard) {
			exampleBoard[piece.getYpos()][piece.getXpos()] = piece;
		}
		sameBoard(exampleBoard,testModel.getBoard());
	}
	
	public static boolean sameBoard(Piece[][] board1, Piece[][] board2){
		boolean equals = true;
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				System.out.println(x+":"+y);
				if(board1[y][x]== null || board2[y][x] == null) {
					assertTrue(board1[y][x] == board2[y][x]);
					
				}
				else{
					assertTrue(board1[y][x].equals(board2[y][x]));
				}
			}
		}
		return equals;
	}
}
