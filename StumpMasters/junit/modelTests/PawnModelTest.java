package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import models.PawnModel;
import models.PieceModel;
import models.RookModel;

class PawnModelTest {
	
	@Test
	void testBlackPawn(){
		PieceModel testPiece = new PawnModel(4,4,PieceModel.BLACK);
		PieceModel[][] board= new PieceModel[8][8];
		PieceModel enemyPiece = new RookModel(5,5,PieceModel.WHITE);
		PieceModel enemyPiece2 = new PawnModel(4,5,PieceModel.WHITE);
		PieceModel friendlyPiece = new RookModel(3,5,PieceModel.BLACK);
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
		PieceModel testPiece = new PawnModel(4,4,PieceModel.WHITE);
		PieceModel[][] board= new PieceModel[8][8];
		PieceModel enemyPiece = new RookModel(3,3,PieceModel.BLACK);
		PieceModel enemyPiece2 = new PawnModel(4,3,PieceModel.BLACK);
		PieceModel friendlyPiece = new RookModel(2,3,PieceModel.WHITE);
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