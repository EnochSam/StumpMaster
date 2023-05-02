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
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

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
						"	isCaptured varchar(10),"+
						"	movedAlready varchar(10)"+					
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
					// populate players table (do authors first, since color is foreign key in books table)
					insertPlayer = conn.prepareStatement("insert into players (type) values (?)");
					for (Player player : playerList) {
//						// auto-generated primary key, don't insert this
						insertPlayer.setString(1,player.getType());
						insertPlayer.addBatch();
					}
					insertPlayer.executeBatch();
					
					// populate books table (do this after authors table,
					// since author_id must exist in authors table before inserting book)
					insertPiece = conn.prepareStatement("insert into pieces (color, type, x, y, isCaptured, movedAlready) values (?, ?, ?, ?,?, ?)");
					for (Piece piece : pieceList) {
//						insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertPiece.setInt(1, piece.getColor());
						insertPiece.setString(2, piece.type());
						insertPiece.setInt(3, piece.getXpos());
						insertPiece.setInt(4,  piece.getYpos());
						insertPiece.setString(5,  ""+piece.getCaptured());
						insertPiece.setString(6,  ""+piece.getHasMovedAlready());
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

	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		
		DerbyDatabase db = new DerbyDatabase();
		db.resetLocations();
	}
	
	@Override
	
	public Piece[][] populateBoard(String boardLocations) {
		Piece[][] board = new Piece[8][8];
		executeTransaction(new Transaction<Boolean>() {
			
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				ResultSet resultSet = null;					
				try {					
						
					//create pieces for all tuples where isCaptured = false, add piece to board at right location
						stmt1 = conn.prepareStatement(
								"select type, x, y, color, movedAlready, piece_id "+
								"from pieces "+
								"where isCaptured  = 'false'"
								);
						resultSet = stmt1.executeQuery();			
				
				
					while(resultSet.next()) {
						String type = resultSet.getObject(1).toString();
						int x = Integer.parseInt(resultSet.getObject(2).toString());
						int y = Integer.parseInt(resultSet.getObject(3).toString());
						int color = Integer.parseInt(resultSet.getObject(4).toString());
						boolean movedAlready = resultSet.getString(5).equals("true");
						Piece piece = Piece.findPiece(type);
						piece.setXpos(x);
						piece.setYpos(y);
						piece.setColor(color);
						piece.setHasMovedAlready(movedAlready);
						board[y][x] = piece;
						
						//set board in model			
					}
		}		
		finally {
			DBUtil.closeQuietly(stmt1);
			}
				return true;
		}	
	});
		return board;
	}
	@Override
	public void resetLocations() {
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
	@Override
	public Player populatePlayer(int playerColor) {
		//King needs to be first, that is all
		Player player = new Player();
		player.setPieces(new Piece[16]);
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				try {
					stmt1 = conn.prepareStatement(
							"select type,x,y,movedAlready from pieces where color = ?"
					);
					stmt1.setInt(1,playerColor);
					ResultSet resultSet = stmt1.executeQuery();
					while(resultSet.next()) {
						Piece piece = Piece.findPiece(""+resultSet.getObject(1));
						piece.setXpos(Integer.parseInt(""+resultSet.getObject(2)));
						piece.setYpos(Integer.parseInt(""+resultSet.getObject(3)));
						piece.setColor(playerColor);
						piece.setHasMovedAlready((""+resultSet.getObject(4)).equals("true"));
						if(piece instanceof King) {
							player.getPieces()[0] = piece;
						}else {
							int i = 1;
							while(player.getPieces()[i] != null) {i++;}
							player.getPieces()[i]= piece;
						}
					}
				}finally {
					DBUtil.closeQuietly(stmt1);;

				}
				
			return null;
			}});
		return player;
	}
	@Override
	public Piece getPiece(int pieceXpos, int pieceYpos){
		//for some reason when passing in a Piece object, it doesn't work, so I just made a 1 index array
		Piece[] pieces= new Piece[1];
		executeTransaction(new Transaction<Boolean>() {
		public Boolean execute(Connection conn) throws SQLException {
			PreparedStatement stmt1 = null;
			try {
				stmt1 = conn.prepareStatement(
						"select type, color ,movedAlready from pieces where x = ? and y = ? and isCaptured = false"
				);
				stmt1.setInt(1, pieceXpos);
				stmt1.setInt(2, pieceYpos);
				ResultSet resultSet = stmt1.executeQuery();
				while(resultSet.next()) {
					Piece piece = Piece.findPiece(""+resultSet.getObject(1));
					piece.setXpos(pieceXpos);
					piece.setYpos(pieceYpos);
					piece.setColor(Integer.parseInt(""+resultSet.getObject(2)));
					piece.setHasMovedAlready((""+resultSet.getObject(3)).equals("true"));
					pieces[0] = piece;

				}

			}finally {
				DBUtil.closeQuietly(stmt1);;

			}
			return true;
		}});
		return pieces[0];
	}
	
	public void updateDatabase(int oldX, int oldY, int newX, int newY) {
		executeTransaction(new Transaction<Boolean>() {

		public Boolean execute(Connection conn) throws SQLException {
			PreparedStatement stmt1 = null;
			PreparedStatement stmt2 = null;
			PreparedStatement stmt3 = null;
			PreparedStatement stmt4 = null;

			try {
				stmt1 = conn.prepareStatement(
						"select piece_id from pieces where x = ? and y = ? and isCaptured = false"
				);
				stmt1.setInt(1, oldX);
				stmt1.setInt(2, oldY);
				ResultSet resultSet = stmt1.executeQuery();
				resultSet.next();
				
				int movingPiece_id = Integer.parseInt(""+resultSet.getObject(1));
				int capturedPiece_id = -1;
				stmt2 = conn.prepareStatement(
						"select piece_id from pieces where x = ? and y = ? and isCaptured = false"
				);
				stmt2.setInt(1, newX);
				stmt2.setInt(2, newY);
				resultSet = stmt2.executeQuery();
				while(resultSet.next()){
				capturedPiece_id = Integer.parseInt(""+resultSet.getObject(1));
				}
				if(capturedPiece_id != -1) {
					stmt3 = conn.prepareStatement(
							"update pieces set isCaptured = true where piece_id = ?"
					);
					stmt3.setInt(1, capturedPiece_id);
					stmt3.executeUpdate();
				}
				stmt4 = conn.prepareStatement(
						"update pieces set x = ?, y = ?,movedAlready = true where piece_id = ?"
				);
				stmt4.setInt(1, newX);
				stmt4.setInt(2, newY);
				stmt4.setInt(3, movingPiece_id);
				stmt4.executeUpdate();
			}finally {
				DBUtil.closeQuietly(stmt1);
				DBUtil.closeQuietly(stmt2);
				DBUtil.closeQuietly(stmt3);
				DBUtil.closeQuietly(stmt4);;

			}
			return true;
		}});
	}
	@Override
	public void updateDatabaseForCastling(int rookXpos, int rookYpos) {
		/* TODO Passed in are the original x and y positions of Rook, 
		 * if RookXpos == 0, left Castle, else, Right Castle 
		 * Update rook's position in Database
		 */
		int newXpos = rookXpos == 0? 3 : 5;
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select piece_id from pieces where x = ? and y = ? and isCaptured = false"
					);
					stmt1.setInt(1, rookXpos);
					stmt1.setInt(2, rookYpos);
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					
					int capturedPiece_id = Integer.parseInt(""+resultSet.getObject(1));
					stmt2 = conn.prepareStatement(
							"update pieces set x=?,y=? where piece_id = ?"
					);
					stmt2.setInt(1, newXpos);
					stmt2.setInt(2, rookYpos);
					stmt2.setInt(3, capturedPiece_id);
					stmt2.executeUpdate();
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
				return true;
			}});
		
		
	}
	@Override
	public void updateDatabaseForEnPassant(int xpos, int ypos, int color) {
		int capturedy = color == Piece.WHITE? ypos-1 : ypos+1;
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select piece_id from pieces where x = ? and y = ? and isCaptured = false"
					);
					stmt1.setInt(1, xpos);
					stmt1.setInt(2, capturedy);
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					
					int capturedPiece_id = Integer.parseInt(""+resultSet.getObject(1));
					stmt2 = conn.prepareStatement(
							"update pieces set isCaptured = true where piece_id = ?"
					);
					stmt2.setInt(1, capturedPiece_id);
					stmt2.executeUpdate();
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
				return true;
			}});
		
	}
	@Override
	public void updatDatabaseForPawnPromotion(int x, int y, char promotedPawnChar) {
		String newType;
		if(promotedPawnChar == 'Q') {
			newType = "Queen";
		}else if(promotedPawnChar == 'R') {
			newType = "Rook";
		}else if(promotedPawnChar == 'B') {
			newType = "Bishop";
		}else{
			newType = "Knight";
		}
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select piece_id from pieces where x = ? and y = ? and isCaptured = false"
					);
					stmt1.setInt(1, x);
					stmt1.setInt(2, y);
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					
					int capturedPiece_id = Integer.parseInt(""+resultSet.getObject(1));
					stmt2 = conn.prepareStatement(
							"update pieces set type = ? where piece_id = ?"
					);
					stmt2.setInt(2, capturedPiece_id);
					stmt2.setString(1, newType);
					stmt2.executeUpdate();
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
				return true;
			}});
		
		
	}
	@Override
	public List<String> getCapturedPlayersList(int playerColor) {
		// TODO Auto-generated method stub
		List<String> captured= new ArrayList<String>();
		String color = playerColor == Piece.WHITE? "White":"Black";
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select type from pieces where color = ? and isCaptured = true"
					);
					stmt1.setInt(1, playerColor);
					ResultSet resultSet = stmt1.executeQuery();
					while(resultSet.next()) {
						captured.add(color+resultSet.getObject(1));
					}
					
				}finally {
					DBUtil.closeQuietly(stmt1);
				}
				return true;
			}});
		return captured;
	}

}
