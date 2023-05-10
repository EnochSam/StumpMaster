package databaseTesting;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import Database.DerbyDatabase;
import controllers.GameController;
import models.Player;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTests {
	@Test
	public void getPiece() {
		DerbyDatabase db = new DerbyDatabase();
		db.setUsername("test");
		db.resetLocations();
		Pawn pawn1 = new Pawn(0,1,Piece.WHITE);
		Pawn pawn2 = new Pawn(5,6,Piece.BLACK);
		King king = new King(4,0,Piece.WHITE);
		Knight knight = new Knight(1,7,Piece.BLACK);
		
		assertEquals(pawn1.getColor(), db.getPiece(0, 1).getColor());
		assertEquals(king.getColor(), db.getPiece(4, 0).getColor());
		assertEquals(knight.getColor(), db.getPiece(1, 7).getColor());
		assertEquals(pawn2.getColor(), db.getPiece(5, 6).getColor());
		assertEquals(pawn1.type(), db.getPiece(0, 1).type());
		assertEquals(king.type(), db.getPiece(4, 0).type());
		assertEquals(knight.type(), db.getPiece(1, 7).type());
		assertEquals(pawn2.type(), db.getPiece(5, 6).type());
		assertEquals(pawn1.getXpos(), db.getPiece(0, 1).getXpos());
		assertEquals(king.getXpos(), db.getPiece(4, 0).getXpos());
		assertEquals(knight.getXpos(), db.getPiece(1, 7).getXpos());
		assertEquals(pawn2.getXpos(), db.getPiece(5, 6).getXpos());
		assertEquals(pawn1.getYpos(), db.getPiece(0, 1).getYpos());
		assertEquals(king.getYpos(), db.getPiece(4, 0).getYpos());
		assertEquals(knight.getYpos(), db.getPiece(1, 7).getYpos());
		assertEquals(pawn2.getYpos(), db.getPiece(5, 6).getYpos());

		
	}
	
	@Test
	public void getCapturedPlayersList() throws IOException{
		DerbyDatabase db = new DerbyDatabase();
		db.setUsername("test");
		db.resetLocations();

		db.updateDatabase(0, 1, 0, 3);
		db.updateDatabase(1, 6, 1, 4);
		db.updateDatabase(0, 3, 1, 4);
		List<String> captured= new ArrayList<String>();
		captured = db.getCapturedPlayersList(Piece.BLACK);

		Boolean found = true;
		if(captured.isEmpty()) {
			found = false;
		}
		assertTrue(found);
		assertEquals(1, captured.size());
		
		
		captured = db.getCapturedPlayersList(Piece.WHITE);
		found = true;
		if(captured.isEmpty()) {
			found = false;
		}
		assertFalse(found);
		assertEquals(0, captured.size());
	}
	
	@Test
	public void getCapturedPlayersList1() throws IOException{
		DerbyDatabase db = new DerbyDatabase();
		db.setUsername("test");
		db.resetLocations();

		db.updateDatabase(2, 1, 2, 2);
		db.updateDatabase(2, 6, 2, 4);
		db.updateDatabase(6, 0, 5, 2);
		db.updateDatabase(1, 6, 1, 4);
		db.updateDatabase(5, 2, 3, 3);
		db.updateDatabase(6, 6, 6, 4);
		db.updateDatabase(5, 1, 5, 3);
		db.updateDatabase(2, 4, 3, 3);
		db.updateDatabase(5, 3, 6, 4);
		db.updateDatabase(1, 4, 1, 3);
		db.updateDatabase(2, 2, 3, 3);
		db.updateDatabase(1, 3, 1, 2);
		db.updateDatabase(4, 1, 4, 2);
		db.updateDatabase(1, 2, 0, 1);
		db.updateDatabase(5, 0, 0, 5);
		db.updateDatabase(0, 1, 1, 0);
		db.updateDatabase(0, 5, 2, 7);

		
		List<String> captured= new ArrayList<String>();
		captured = db.getCapturedPlayersList(Piece.BLACK);

		Boolean found = true;
		if(captured.isEmpty()) {
			found = false;
		}
		assertTrue(found);
		assertEquals(3, captured.size());
		
		
		captured = db.getCapturedPlayersList(Piece.WHITE);
		found = true;
		if(captured.isEmpty()) {
			found = false;
		}
		assertTrue(found);
		assertEquals(3, captured.size());

	}
	
	
	@Test
	public void populatePlayer() {
		DerbyDatabase db = new DerbyDatabase();
		GameController control = new GameController();
		db.setUsername("test");
		db.resetLocations();
		Player pl = new Player();
		Piece[] piece = new Piece[16];
		King k = new King(4, 0, Piece.WHITE);
		Pawn pawn = new Pawn(7, 1, Piece.WHITE);
		Queen q = new Queen(3, 0, Piece.WHITE);
		King bk = new King(4, 7, Piece.BLACK);
		Queen bq = new Queen(3, 7, Piece.BLACK);
		
		
		
		pl = db.populatePlayer(Piece.WHITE);
		piece = pl.getPieces();
		
		assertEquals(k.getColor(), piece[0].getColor());
		assertEquals(k.getXpos(), piece[0].getXpos());
		assertEquals(k.getYpos(), piece[0].getYpos());
		assertEquals(k.getCaptured(), piece[0].getCaptured());
		assertEquals(k.getHasMovedAlready(), piece[0].getHasMovedAlready());
		int i = 0;
		while(!(piece[i] instanceof Queen)){
			i++;
		}
		assertEquals(q.getColor(), piece[i].getColor());
		assertEquals(q.getXpos(), piece[i].getXpos());
		assertEquals(q.getYpos(), piece[i].getYpos());
		assertEquals(q.getCaptured(), piece[i].getCaptured());
		assertEquals(q.getHasMovedAlready(), piece[i].getHasMovedAlready());
		
		assertEquals(pawn.getColor(), piece[8].getColor());
		assertEquals(pawn.getXpos(), piece[8].getXpos());
		assertEquals(pawn.getYpos(), piece[8].getYpos());
		assertEquals(pawn.getCaptured(), piece[8].getCaptured());
		assertEquals(pawn.getHasMovedAlready(), piece[8].getHasMovedAlready());
		
		
		pl = db.populatePlayer(Piece.BLACK);
		piece = pl.getPieces();
		
		assertEquals(bk.getColor(), piece[0].getColor());
		assertEquals(bk.getXpos(), piece[0].getXpos());
		assertEquals(bk.getYpos(), piece[0].getYpos());
		assertEquals(bk.getCaptured(), piece[0].getCaptured());
		assertEquals(bk.getHasMovedAlready(), piece[0].getHasMovedAlready());
		int j = 0;
		while(!(piece[j] instanceof Queen)){
			j++;
		}
		assertEquals(bq.getColor(), piece[j].getColor());
		assertEquals(bq.getXpos(), piece[j].getXpos());
		assertEquals(bq.getYpos(), piece[j].getYpos());
		assertEquals(bq.getCaptured(), piece[j].getCaptured());
		assertEquals(bq.getHasMovedAlready(), piece[j].getHasMovedAlready());
		


	}
	
	@Test
	public void populateBoard() {
		DerbyDatabase db = new DerbyDatabase();
		GameController control = new GameController();
		Piece[][] board = new Piece[8][8];
		db.dropTables();
		db.createTables();
		db.setUsername("test");
		db.resetLocations();
		db.updateDatabase(2, 1, 2, 2);
		db.updateDatabase(2, 6, 2, 4);
		db.updateDatabase(6, 0, 5, 2);
		db.updateDatabase(1, 6, 1, 4);
		board = db.populateBoard("lkj;j");
		
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(board[j][i] != null && db.getPiece(i, j) != null){
				assertEquals(board[j][i].getColor(), db.getPiece(i, j).getColor());
				assertEquals(board[j][i].type(), db.getPiece(i, j).type());
				}
			}
		}
		
		
		
		
	}
	
}
