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

public class VerticalHorizontalTesting {

	@Test
	public void testNoPiecesOnBoard() {
		Piece[][] board = new Piece[8][8];
		Piece testingPiece = new Bishop(); 
		board[4][3] = testingPiece;
		List<Integer[]> possibleMoves;
		
		List<Piece> PiecesToPass = new ArrayList<Piece>();
		PiecesToPass.addAll(Arrays.asList(new Rook(), new Queen()));
		for(Piece piece: PiecesToPass) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), true);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), true);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), true);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), true);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), true);
		}
		
		List<Piece> PiecesToFail = new ArrayList<Piece>();
		PiecesToFail.addAll(Arrays.asList(new Bishop(),new Knight()));
		for(Piece piece: PiecesToFail) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
		}
		
		testingPiece = new King();
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), true);
		
		testingPiece = new Pawn(3,4,0, 1);
		possibleMoves = testingPiece.getValidMoves(board);
		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), true);
		
		testingPiece = new Pawn(3,4,1, 2);
		possibleMoves = testingPiece.getValidMoves(board);
		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
	}
	
	@Test
	public void testPiecesOnBoard() {
		Piece[][] board = new Piece[8][8];
		Piece testingPiece = new Bishop(); 
		board[4][3] = testingPiece;
		List<Integer[]> possibleMoves;
		board[4][0] = new Pawn(0,4,1, 3);
		board[5][3] = new Pawn(3,5,0, 4);
		board[4][4] = new Pawn(4,4,1, 5);
		board[4][6] = new Pawn(6,4,0, 6);
		
		List<Piece> PiecesToPass = new ArrayList<Piece>();
		PiecesToPass.addAll(Arrays.asList(new Rook(), new Queen()));
		for(Piece piece: PiecesToPass) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), true);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), true);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
		}
		
		List<Piece> PiecesToFail = new ArrayList<Piece>();
		PiecesToFail.addAll(Arrays.asList(new Bishop(),new Knight()));
		for(Piece piece: PiecesToFail) {
		testingPiece = piece;
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
		}
		
		testingPiece = new King();
		testingPiece.setColor(0);
		testingPiece.setXpos(3);
		testingPiece.setYpos(4);
		possibleMoves = testingPiece.getValidMoves(board);

		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), true);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
		
		testingPiece = new Pawn(3,4,0, 7);
		possibleMoves = testingPiece.getValidMoves(board);
		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), false);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
		
		testingPiece = new Pawn(3,4,1, 6);
		possibleMoves = testingPiece.getValidMoves(board);
		//Far Top
		assertEquals(inList(possibleMoves, 3, 7), false);
		//Far Bottom
		assertEquals(inList(possibleMoves, 3, 0), false);
		//Far Left
		assertEquals(inList(possibleMoves, 0, 4), false);
		//Far Right
		assertEquals(inList(possibleMoves, 7, 4), false);
		//Inner Left
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Right
		assertEquals(inList(possibleMoves, 2, 4), false);
		//Inner Bottom
		assertEquals(inList(possibleMoves, 3, 3), true);
		//Inner Bottom Right
		assertEquals(inList(possibleMoves, 3, 5), false);
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
