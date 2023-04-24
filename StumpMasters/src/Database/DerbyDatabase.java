package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Game;
import models.Player;
import pieceModels.King;
import pieceModels.Piece;

public class DerbyDatabase implements IDatabase {
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver"+ e.getMessage());
		}
	}
	
	private interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;

	Game model;
	
	public void setModel(Game model) {
		this.model = model;
	}
	public Game getModel() {
		return this.model;
	}
	
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}
	
	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();
		
		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}

	
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				
				try {
					stmt1 = conn.prepareStatement(
							"create table players (" +
							"	color integer primary key " +
							"		generated always as identity (start with 0, increment by 1), " +
							"	type varchar(70) " +
							")"
					);
					stmt1.executeUpdate();
					
					stmt2 = conn.prepareStatement(
						"create table pieces (" +
						"	piece_id integer primary key " +
						"		generated always as identity (start with 0, increment by 1), " +
						"	color integer constraint color references players, " +
						"	type varchar(40)," +
						"	x integer," +
						"	y integer,"+
						"	isCaptured varchar(10)"+
						
						")"
					);	
					stmt2.executeUpdate();
					
					
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
			}
		});
	}
	
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Player> playerList;
				List<Piece> pieceList;

				try {
					playerList = InitialData.getPlayer();
					pieceList = InitialData.getPieces();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertPlayer = null;
				PreparedStatement insertPiece = null;
				
				try {
					// populate authors table (do authors first, since author_id is foreign key in books table)
					insertPlayer = conn.prepareStatement("insert into players (type) values (?)");
					for (Player player : playerList) {
//						insertAuthor.setInt(1, author.getAuthorId());	// auto-generated primary key, don't insert this
						insertPlayer.setString(1,player.getType());
						insertPlayer.addBatch();
					}
					insertPlayer.executeBatch();
					
					// populate books table (do this after authors table,
					// since author_id must exist in authors table before inserting book)
					insertPiece = conn.prepareStatement("insert into pieces (color, type, x, y, isCaptured) values (?, ?, ?, ?,?)");
					for (Piece piece : pieceList) {
//						insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertPiece.setInt(1, piece.getColor());
						insertPiece.setString(2, piece.type());
						insertPiece.setInt(3, piece.getXpos());
						insertPiece.setInt(4,  piece.getYpos());
						insertPiece.setString(5,  ""+piece.getCaptured());
						insertPiece.addBatch();
					}
					insertPiece.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(insertPlayer);
					DBUtil.closeQuietly(insertPiece);
				}
			}
		});
	}
	
	private boolean checkIfOpenMove(King king, Piece[][] board) {
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				//Move Check
				// Checks all pieces on the Board that ISNT the King in Question
				if(board[j][i] != null) {
					if(king.getColor() != board[j][i].getColor() ){

					for(Integer[] checkingLoc : board[j][i].getValidMoves(board)) {

						if(checkingLoc[0] == king.getXpos() && checkingLoc[1] == king.getYpos()) {
							return false;
						}
					}
					}
				}
			}
		}
		return true;
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		DerbyDatabase db = new DerbyDatabase();
		Game model = new Game();
		db.setModel(model);
		db.setBoard("");
			
		
		
		//DerbyDatabase db = new DerbyDatabase();
		//db.createTables();
		
		//db.loadInitialData();
		
	}
	
	@Override
	public String getPossibleMoves(String clickedOnLocation, String playerTurn) {
		return executeTransaction(new Transaction<String>() {
			@Override
			public String execute(Connection conn) throws SQLException {
				String listOfLocations = "";
				int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
				int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;
				
				Piece selectedPiece = model.getBoard()[pieceYpos][pieceXpos];
				//checks to make sure that the piece selected is the of the same color 
				if((playerTurn.equals("White") && selectedPiece.getColor() == Piece.WHITE) || (playerTurn.equals("Black") && selectedPiece.getColor() == Piece.BLACK)) {
				//Checks if the piece is pinned
				
				//Finds the selected Piece's King
				King king;
				if(model.getWhitePlayer().getPieces()[0].getColor() == selectedPiece.getColor()) {
					king = (King) model.getWhitePlayer().getPieces()[0];
				}else {
					king = (King) model.getBlackPlayer().getPieces()[0];
				}
				//looks for EnPassant
				//if() {
					
				//}
				
				// Looks to see if Castling is allowed
				//if(selectedPiece instanceof King) {
					//check 
				//}
				
				List<Integer[]> possibleMoves = selectedPiece.getValidMoves(model.getBoard());
				for(Integer[] loc : possibleMoves) {
					//Temporarily changes board to make sure that moving the piece does not put the King in Check
					Piece attemptingToMove = model.getBoard()[loc[1]][loc[0]];
					model.getBoard()[pieceYpos][pieceXpos] = null;
					model.getBoard()[loc[1]][loc[0]] = selectedPiece;
					int origX = selectedPiece.getXpos();
					int origY = selectedPiece.getYpos();
					selectedPiece.setXpos(loc[0]);
					selectedPiece.setYpos(loc[1]);
					Boolean checkIfOpenMove;
					checkIfOpenMove = checkIfOpenMove(king,model.getBoard());
					model.getBoard()[loc[1]][loc[0]] = attemptingToMove;
					model.getBoard()[pieceYpos][pieceXpos]= selectedPiece;
					selectedPiece.setXpos(origX);
					selectedPiece.setYpos(origY);
					//if the king will not be put in check, add to the list of valid moves
					if(checkIfOpenMove) {
					listOfLocations+= ""+(loc[0]+1);
					listOfLocations+= ""+(loc[1]+1);
					listOfLocations+=" ";
					}
				}
				if(model.getInCheck() && !(selectedPiece instanceof King)) {
					//if the player is in check, we will resize the list of moves to only show moves that can reach opposing piece
					String stringOfAvailableMoves = "";
					//Grabs list of AvailaleMoves from Model
					List<Integer[]> availableMoves = model.getAvailableMoves();
					
					//If Location != location of attackingPiece, remove from list
					for(int i = 0; i < listOfLocations.length(); i+=3){
						boolean inListOfAvailableLocations = false;
						for(Integer[] x : availableMoves) {
							String locx = (""+(x[0]+1));
							String locy = (""+(x[1]+1));
							if(locx.equals(""+listOfLocations.charAt(i)) && locy.equals(""+listOfLocations.charAt(i+1))) {
								inListOfAvailableLocations = true;
							}
						}
						if(inListOfAvailableLocations){
							stringOfAvailableMoves+= ""+listOfLocations.charAt(i)+""+listOfLocations.charAt(i+1)+" ";
						}

					}
					return stringOfAvailableMoves;
				
				}
				return listOfLocations;
				}else {
					return "False";
				}
			}});
		}

	@Override
	public void setBoard(String boardLocations) {
		if(boardLocations.length() == 0) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;
				boolean tableExistPi = true;
				boolean tableExistPl = true;
				try {
					try {
					stmt1 = conn.prepareStatement(
							"select * from pieces"
					);
					stmt1.executeQuery();
					}
					catch(SQLException e) {

						tableExistPi = false;
					}
				}finally {
					DBUtil.closeQuietly(stmt1);;

				}
				

				try {
					stmt2 = conn.prepareStatement(
							"select * from players"
					);
					stmt2.executeQuery();
				}
				catch(SQLException e) {
					tableExistPl = false;
				}
				finally {
					DBUtil.closeQuietly(stmt2);;
				}
				
				
				//if gameMoves.length is = 0, drop tables
				if(tableExistPi && tableExistPl) {
					try {					
						stmt3 = conn.prepareStatement(
								"drop table pieces"
						);
						stmt3.executeUpdate();
						stmt4 = conn.prepareStatement(
								"drop table players"
						);
						stmt4.executeUpdate();
						
						DBUtil.closeQuietly(stmt3);
						DBUtil.closeQuietly(stmt4);
						
						return true;
					}
					
					finally {
						DBUtil.closeQuietly(stmt3);
						DBUtil.closeQuietly(stmt4);
					}
				}
				else if(tableExistPi) {
					try {
						stmt3 = conn.prepareStatement(
							"drop table pieces"
					);
					stmt3.executeUpdate();
					DBUtil.closeQuietly(stmt3);
					}
					finally {
						DBUtil.closeQuietly(stmt3);
					}
				}
				else if(tableExistPl) {
					try {
						stmt4 = conn.prepareStatement(
								"drop table players"
						);
						stmt4.executeUpdate();
						DBUtil.closeQuietly(stmt4);
					}
					finally {
						DBUtil.closeQuietly(stmt4);
					}
				}
				return false;
			}
				
		});	
		
		//create tables by create table function in this file
		createTables();
		loadInitialData();
		}
		executeTransaction(new Transaction<Boolean>() {
			Piece[][] board = new Piece[8][8];
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				ResultSet resultSet = null;					
				try {
			
					//create pieces for all tuples where isCaptured = false, add piece to board at right location
					stmt1 = conn.prepareStatement(
							"select type, x, y, color "+
							"from pieces "+
							"where isCaptured  = 'false'"
							);
					resultSet = stmt1.executeQuery();
			
				
				
					while(resultSet.next()) {
						String type = resultSet.getObject(1).toString();
						int x = Integer.parseInt(resultSet.getObject(2).toString());
						int y = Integer.parseInt(resultSet.getObject(3).toString());
						int color = Integer.parseInt(resultSet.getObject(4).toString());
						Piece piece = Piece.findPiece(type);
						piece.setXpos(x);
						piece.setYpos(y);
						piece.setColor(color);
						board[y][x] = piece;
						
						
						//set board in model			
					}
					model.setBoard(board);
					return true;
			}
		finally {
			DBUtil.closeQuietly(stmt1);
			}
		}	
	});
	}
		
	@Override
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove, String player) {
		return false;
	}

}
