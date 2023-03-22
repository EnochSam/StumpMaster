package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import pieceModels.BishopModel;
import pieceModels.PieceModel;

class BishopModelTests {
	/*
	@Test
	void test() {
		s("Not yet implemented");
	}
	*/
	
	@Test
	void testMovePossibleMoves(){
		BishopModel testBishop = new BishopModel(4,4,PieceModel.WHITE);
		PieceModel[][] board= new PieceModel[8][8];
		PieceModel enemyPiece = new BishopModel(3,3,PieceModel.BLACK);
		PieceModel friendlyPiece = new BishopModel(2,2,PieceModel.WHITE);
		board[enemyPiece.getYpos()][enemyPiece.getXpos()] = enemyPiece;
		board[friendlyPiece.getYpos()][friendlyPiece.getXpos()] = friendlyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		List<Integer[]>possibleMoves = testBishop.getValidMoves(board);
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