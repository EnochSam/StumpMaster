package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Rook;

class PawnTests {
	
	@Test
	void testBlackPawn(){
		Piece testPiece = new Pawn(4,4,Piece.BLACK, 1);
		Piece[][] board= new Piece[8][8];
		Piece enemyPiece = new Rook(5,5,Piece.WHITE, 2);
		Piece enemyPiece2 = new Pawn(4,5,Piece.WHITE, 3);
		Piece friendlyPiece = new Rook(3,5,Piece.BLACK, 4);
		board[enemyPiece.getYpos()][enemyPiece.getXpos()] = enemyPiece;
		board[enemyPiece2.getYpos()][enemyPiece2.getXpos()] = enemyPiece2;
		board[friendlyPiece.getYpos()][friendlyPiece.getXpos()] = friendlyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		boolean pieceInFrontOfEnemy = false;
		List<Integer[]>possibleMoves = testPiece.getValidMoves(board);
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
			if(possibleMoves.get(i)[0].equals(enemyPiece2.getXpos()) && possibleMoves.get(i)[1].equals(enemyPiece2.getYpos())){
				pieceInFrontOfEnemy = true;
			}
		}
		assertEquals(true, foundPiece);
		assertFalse(pieceToLeftOfEnemy);
		assertFalse(friendlyPieceIsValidMove);
		assertFalse(pieceInFrontOfEnemy);
	}
	
	@Test
	void testWhitePawn(){
		Piece testPiece = new Pawn(4,4,Piece.WHITE, 5);
		Piece[][] board= new Piece[8][8];
		Piece enemyPiece = new Rook(3,3,Piece.BLACK, 6);
		Piece enemyPiece2 = new Pawn(4,3,Piece.BLACK, 7);
		Piece friendlyPiece = new Rook(2,3,Piece.WHITE, 8);
		board[enemyPiece.getYpos()][enemyPiece.getXpos()] = enemyPiece;
		board[enemyPiece2.getYpos()][enemyPiece2.getXpos()] = enemyPiece2;
		board[friendlyPiece.getYpos()][friendlyPiece.getXpos()] = friendlyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		boolean pieceInFrontOfEnemy = false;
		List<Integer[]>possibleMoves = testPiece.getValidMoves(board);
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
			if(possibleMoves.get(i)[0].equals(enemyPiece2.getXpos()) && possibleMoves.get(i)[1].equals(enemyPiece2.getYpos())){
				pieceInFrontOfEnemy = true;
			}
		}
		assertEquals(true, foundPiece);
		assertFalse(pieceToLeftOfEnemy);
		assertFalse(friendlyPieceIsValidMove);
		assertFalse(pieceInFrontOfEnemy);
	}
	

}