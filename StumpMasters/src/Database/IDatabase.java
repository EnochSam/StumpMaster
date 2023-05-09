package Database;

import java.sql.SQLException;
import java.util.List;

import models.Game;
import models.Player;
import pieceModels.Piece;

public interface IDatabase {
	/*public String getPossibleMoves(String clickedOnLocation, String playerTurn);
	public void setBoard(String boardLocations) throws SQLException;
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove,String player);
	public void setModel(Game model);
	*/
	public Piece[][] populateBoard(String boardLocations);

	public void resetLocations();

	public Player populatePlayer(int playerColor);

	public Piece getPiece(int pieceXpos, int pieceYpos);

	public void updateDatabase(int oldX, int oldY, int newX, int newY);
	
	//Passes in The x and ypos of Pawn that is capturing to update the pawn that will be captured
	public void updateDatabaseForEnPassant(int xpos, int ypos, int color);

	void updateDatabaseForCastling(int rookXpos, int rookYpos);

	public void updatDatabaseForPawnPromotion(int x, int y, char promotedPawnChar);

	public List<String> getCapturedPlayersList(int playerColor);

	public void setUsername(String username);

	public void saveGame();
	
}
