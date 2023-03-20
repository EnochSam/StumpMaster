package PieceModels;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class PawnTest {
	
	@Test
	void testBlackPawn(){
		Piece testPiece = new Pawn(4,4,Piece.BLACK);
		Piece[][] board= new Piece[8][8];
		Piece enemyPiece = new Rook(5,5,Piece.WHITE);
		Piece enemyPiece2 = new Pawn(4,5,Piece.WHITE);
		Piece friendlyPiece = new Rook(3,5,Piece.BLACK);
		board[enemyPiece.getXpos()][enemyPiece.getYpos()] = enemyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		boolean pieceInFrontOfEnemy = false;
		List<Integer[]>possibleMoves = testPiece.getValidMoves(board);
		for(int i = 0; i < possibleMoves.size(); i++) {
			System.out.println(possibleMoves.get(i)[0]+possibleMoves.get(i)[1]);
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
		Piece testPiece = new Pawn(4,4,Piece.WHITE);
		Piece[][] board= new Piece[8][8];
		Piece enemyPiece = new Rook(3,3,Piece.BLACK);
		Piece enemyPiece2 = new Pawn(4,3,Piece.BLACK);
		Piece friendlyPiece = new Rook(2,3,Piece.WHITE);
		board[enemyPiece.getXpos()][enemyPiece.getYpos()] = enemyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		boolean pieceInFrontOfEnemy = false;
		List<Integer[]>possibleMoves = testPiece.getValidMoves(board);
		for(int i = 0; i < possibleMoves.size(); i++) {
			System.out.println(possibleMoves.get(i)[0]+possibleMoves.get(i)[1]);
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