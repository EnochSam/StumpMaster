package Database;

public interface IDatabase {
	public String getPossibleMoves(String clickedOnLocation, String playerTurn);
	public void setBoard(String boardLocations);
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove,String player);
	
}
