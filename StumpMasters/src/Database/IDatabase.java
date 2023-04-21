package Database;

import java.sql.SQLException;

public interface IDatabase {
	public String getPossibleMoves(String clickedOnLocation, String playerTurn);
	public void setBoard(String boardLocations) throws SQLException;
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove,String player);
	
}
