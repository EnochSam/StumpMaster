package modelTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

public class DiagonalTesting {

	@Test
	public void testNoPiecesOnBoard() {
		Piece[][] board = new Piece[8][8];
		Piece testingPiece = new Bishop(); 
		board[4][3] = testingPiece;
		List<Integer[]> possibleMoves;
		
		List<Piece> PiecesToPass = new ArrayList<Piece>();
		PiecesToPass.addAll(Arrays.asList(new Bishop(), new Queen()));
		for(Piece piece: PiecesToPass) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		board[4][3] = testingPiece;
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), true);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), true);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), true);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), true);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), true);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), true);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), true);
		}
		
		List<Piece> PiecesToFail = new ArrayList<Piece>();
		PiecesToFail.addAll(Arrays.asList(new Rook(), new Pawn(),new Knight()));
		for(Piece piece: PiecesToFail) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), false);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), false);
		}
		
		testingPiece = new King();
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		board[4][3] = testingPiece;
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), true);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), true);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), true);
	}
	
	@Test
	public void testPiecesOnBoard() {
		Piece[][] board = new Piece[8][8];
		Piece testingPiece = new Bishop(); 
		board[4][3] = testingPiece;
		List<Integer[]> possibleMoves;
		board[5][2] = new Pawn(2,5,1);
		board[5][4] = new Pawn(4,5,0);
		board[2][5] = new Pawn(5,2,1);
		board[2][6] = new Pawn(6,1,0);
		board[6][6] = new King(6,6,1);
		board[1][0] = new King(0,1,0);
		
		List<Piece> PiecesToPass = new ArrayList<Piece>();
		PiecesToPass.addAll(Arrays.asList(new Bishop(), new Queen()));
		for(Piece piece: PiecesToPass) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		board[4][3] = testingPiece;
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), true);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), true);
		}
		
		List<Piece> PiecesToFail = new ArrayList<Piece>();
		PiecesToFail.addAll(Arrays.asList(new Rook(), new Knight()));
		for(Piece piece: PiecesToFail) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), false);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), false);
		}
		
		testingPiece = new King();
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		board[4][3] = testingPiece;
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), true);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), true);
		
		//White Pawn
		testingPiece = new Pawn(3,4,0);
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), true);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), false);
		
		
		//Black Pawn
		testingPiece = new Pawn(3,4,1);
		possibleMoves = testingPiece.getValidMoves(board, testingPiece.getColor());

		//Top Left
		assertEquals(inList(possibleMoves, 0, 7), false);
		//Top Right
		assertEquals(inList(possibleMoves, 6, 7), false);
		//Bottom Left
		assertEquals(inList(possibleMoves, 0, 1), false);
		//Bottom Right
		assertEquals(inList(possibleMoves, 7, 0), false);
		//Inner Top Left
		assertEquals(inList(possibleMoves, 2, 5), false);
		//Inner Top Right
		assertEquals(inList(possibleMoves, 4, 5), false);
		//Inner Bottom Left
		assertEquals(inList(possibleMoves, 2, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 4, 3), false);
	}
	
	
	public static Boolean inList(List<Integer[]> possibleMoves, int x, int y) {
		for(Integer[] loc : possibleMoves) {
			if(loc[0] == x && loc[1] == y) {
				
				return true;
			}
		}
		return false;
	}
}
