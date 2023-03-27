package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import pieceModels.Piece;
import pieceModels.King;

class KingTests {
	/*
	@Test
	void test() {
		s("Not yet implemented");
	}
	*/
	
	@Test
	void testMovePossibleMoves(){
		King testKing = new King(4,4,Piece.WHITE);
		Piece[][] board= new Piece[8][8];
		Piece enemyPiece = new King(2,4,Piece.BLACK);
		Piece friendlyPiece = new King(2,3,Piece.WHITE);
		board[enemyPiece.getYpos()][enemyPiece.getXpos()] = enemyPiece;
		board[friendlyPiece.getYpos()][friendlyPiece.getXpos()] = friendlyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		List<Integer[]>possibleMoves = testKing.getValidMoves(board);
		for(int i = 0; i < possibleMoves.size(); i++) {
			if(possibleMoves.get(i)[0].equals(enemyPiece.getXpos()) && possibleMoves.get(i)[1].equals(enemyPiece.getYpos())){
				foundPiece = true;
				if (possibleMoves.get(i)[0].equals(enemyPiece.getXpos()-1) && possibleMoves.get(i)[1].equals(enemyPiece.getYpos())){
					pieceToLeftOfEnemy = true;
				}
			}
			if(possibleMoves.get(i)[0].equals(friendlyPiece.getXpos()) && possibleMoves.get(i)[1].equals(friendlyPiece.getYpos())){
				friendlyPieceIsValidMove = true;
			}
		}
		assertEquals(true, foundPiece);
		assertFalse(pieceToLeftOfEnemy);
		assertFalse(friendlyPieceIsValidMove);
	}

}