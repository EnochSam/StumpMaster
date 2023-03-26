package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import pieceModels.PieceModel;
import pieceModels.KnightModel;

class KnightModelTests {

	
	@Test
	void testMovePossibleMoves(){
		KnightModel testKnight = new KnightModel(4,4,PieceModel.WHITE);
		PieceModel[][] board= new PieceModel[8][8];
		PieceModel enemyPiece = new KnightModel(5,6,PieceModel.BLACK);
		PieceModel friendlyPiece = new KnightModel(2,4,PieceModel.WHITE);
		board[enemyPiece.getYpos()][enemyPiece.getXpos()] = enemyPiece;
		board[friendlyPiece.getYpos()][friendlyPiece.getXpos()] = friendlyPiece;
		boolean foundPiece = false;
		boolean pieceToLeftOfEnemy = false;
		boolean friendlyPieceIsValidMove = false;
		List<Integer[]>possibleMoves = testKnight.getValidMoves(board);
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