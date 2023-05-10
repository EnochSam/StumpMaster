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
import models.User;
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
	private String username;

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
				PreparedStatement stmt3 = null;

				try {
					stmt1 = conn.prepareStatement(
							"create table players (" +
							"	player_id integer primary key " +
							"		generated always as identity (start with 0, increment by 1), " +
							"	username varchar(70),"+
							"	color integer,"+
							"	type varchar(70) " +
							")"
					);
					stmt1.executeUpdate();
					stmt2 = conn.prepareStatement(
						"create table pieces (" +
						"	piece_id integer primary key " +
						"		generated always as identity (start with 0, increment by 1), " +
						"	player_id integer constraint player_id references players, " +
						" color integer,"+
						"	type varchar(40)," +
						"	x integer," +
						"	y integer,"+
						"	isCaptured varchar(10),"+
						"	movedAlready varchar(10)"+					
						")"
					);	
					stmt2.executeUpdate();
					/*stmt3 = conn.prepareStatement(
							"create table saveStates (" +
							"	game_id integer primary key " +
							"		generated always as identity (start with 0, increment by 1), " +
							"	username varchar(70), " +
							"	gameMoves varchar(900)," +
							"	isWhiteTurn varchar(10)"+
							")"
						);	
						stmt3.executeUpdate();
					*/
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);

				}
			}
		});
	}
	
	public void loadInitialData(String username) {
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
					insertPlayer = conn.prepareStatement("insert into players (username) values (?)");
//						// auto-generated primary key, don't insert this
						insertPlayer.setString(1, username);
					insertPlayer.executeUpdate();		
					PreparedStatement getPlayerId = null;
					getPlayerId = conn.prepareStatement("select player_id from players where username = ?");
//					// auto-generated primary key, don't insert this
					getPlayerId.setString(1, username);
					ResultSet playerId = getPlayerId.executeQuery();
					playerId.next();
					// populate books table (do this after authors table,
					// since author_id must exist in authors table before inserting book)
					insertPiece = conn.prepareStatement("insert into pieces (color, type, x, y, isCaptured, movedAlready, player_id) values (?, ?, ?, ?,?, ?,?)");
					for (Piece piece : pieceList) {
//						insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertPiece.setInt(1, piece.getColor());
						insertPiece.setString(2, piece.type());
						insertPiece.setInt(3, piece.getXpos());
						insertPiece.setInt(4,  piece.getYpos());
						insertPiece.setString(5,  ""+piece.getCaptured());
						insertPiece.setString(6,  ""+piece.getHasMovedAlready());
						insertPiece.setInt(7,Integer.parseInt(""+playerId.getObject(1)));
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
		db.dropTables();
		db.createTables();
		db.setUsername("EnochIsForeverAPoopie");
		db.loadInitialData("EnochIsForeverAPoopie");
		Player[] p = new Player[] {new Player(0), new Player(1)};
		db.overwritePieces(p);
	}
	
	@Override
	public void setUsername(String username) {
		this.username= username;
		
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
								"select pieces.type, pieces.x, pieces.y, pieces.color, pieces.movedAlready, pieces.piece_id "+
								"from pieces,players "+
								"where isCaptured  = 'false' and players.player_id = pieces.player_id and players.username = ?"
								);
						stmt1.setString(1,username);
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
				PreparedStatement stmt5 = null;
				boolean tableExistPi = true;
				boolean tableExistPl = true;
				try {
					try {
					stmt1 = conn.prepareStatement(
							"select pieces.color from pieces, players where pieces.player_id = players.player_id and players.username = ?"
					);
					stmt1.setString(1, username);		
					stmt1.executeQuery();
					}
					catch(SQLException e) {

						tableExistPi = false;
					}
				}finally {
					DBUtil.closeQuietly(stmt1);

				}
				
				try {
				try {
					stmt2 = conn.prepareStatement(
							"select color from players where username = '?'"
					);
					stmt2.setString(1, username);
					stmt2.executeQuery();
				}
				catch(SQLException e) {
					tableExistPl = false;
				}
				}finally {
					DBUtil.closeQuietly(stmt2);
				}
				
				if(tableExistPi && tableExistPl) {
					try {					
						stmt3 = conn.prepareStatement(
								"select piece_id from  pieces, players where pieces.player_id = players.player_id and players.username = ?"
						);
						stmt3.setString(1, username);
						stmt3.executeQuery();
						ResultSet stmt3R = stmt3.getResultSet();
						while(stmt3R.next()) {
							stmt1 = conn.prepareStatement(
									"delete from  pieces where piece_id = ?"
									);
							stmt1.setString(1, ""+stmt3R.getObject(1));
							stmt1.executeQuery();
						}
						stmt4 = conn.prepareStatement(
								"delete from players where username = ?"
						);
						stmt4.setString(1, username);
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
								"select piece_id from pieces, players where pieces.player_id = players.player_id and players.username = ?"
						);
						stmt3.setString(1, username);
						ResultSet stmt4R = stmt3.executeQuery();
						while(stmt4R.next()) {
							stmt5 = conn.prepareStatement(
									"delete from  pieces where piece_id = ?"
									);
							stmt5.setInt(1, Integer.parseInt(""+stmt4R.getObject(1)));
							stmt5.executeUpdate();
							DBUtil.closeQuietly(stmt5);
						}
					}
					finally {
						DBUtil.closeQuietly(stmt3);
						DBUtil.closeQuietly(stmt5);

					}
				}
				else if(tableExistPl) {
					try {
						stmt4 = conn.prepareStatement(
								"delete from players where username = ?"
						);
						stmt4.executeUpdate();
						stmt4.setString(1,username);
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
		loadInitialData(username);
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
							"select pieces.type, pieces.x, pieces.y, pieces.movedAlready, pieces.isCaptured from pieces, players where pieces.color = ? "+
					"	and players.player_id = pieces.player_id and players.username = ? "
					);
					stmt1.setInt(1,playerColor);
					stmt1.setString(2,username);
					ResultSet resultSet = stmt1.executeQuery();
					while(resultSet.next()) {
						Piece piece = Piece.findPiece(""+resultSet.getObject(1));
						piece.setXpos(Integer.parseInt(""+resultSet.getObject(2)));
						piece.setYpos(Integer.parseInt(""+resultSet.getObject(3)));
						piece.setColor(playerColor);
						piece.setHasMovedAlready((""+resultSet.getObject(4)).equals("true"));
						piece.setCaptured((""+resultSet.getObject(5)).equals("true"));
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
						"select pieces.type, pieces.color , pieces.movedAlready from pieces, players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false"+
								" and players.player_id = pieces.player_id and players.username = ?"
				);
				stmt1.setInt(1, pieceXpos);
				stmt1.setInt(2, pieceYpos);
				stmt1.setString(3,username);
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
						"select pieces.piece_id from pieces,players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false "+
								"and players.player_id = pieces.player_id and players.username = ?"
				);
				stmt1.setInt(1, oldX);
				stmt1.setInt(2, oldY);
				stmt1.setString(3, username);
				ResultSet resultSet = stmt1.executeQuery();
				resultSet.next();
				
				int movingPiece_id = Integer.parseInt(""+resultSet.getObject(1));
				int capturedPiece_id = -1;
				stmt2 = conn.prepareStatement(
						"select pieces.piece_id from pieces,players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false"+
				"	and players.player_id = pieces.player_id and players.username = ?"
				);
				stmt2.setInt(1, newX);
				stmt2.setInt(2, newY);
				stmt2.setString(3, username);
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
							"select pieces.piece_id from pieces,players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false"
							+"	 and players.player_id = pieces.player_id and players.username = ?"
					);
					stmt1.setInt(1, rookXpos);
					stmt1.setInt(2, rookYpos);
					stmt1.setString(3, username);
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
							"select pieces.piece_id from pieces,players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false "+
					"                   and players.player_id = pieces.player_id and players.username = ?"
					);
					stmt1.setInt(1, xpos);
					stmt1.setInt(2, capturedy);
					stmt1.setString(3, username);
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
							"select pieces.piece_id from pieces,players where pieces.x = ? and pieces.y = ? and pieces.isCaptured = false"
							+"	and players.player_id = pieces.player_id and players.username = ?"
					);
					stmt1.setInt(1, x);
					stmt1.setInt(2, y);
					stmt1.setString(3, username);
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
							"select pieces.type from pieces,players where pieces.color = ? and pieces.isCaptured = true"
					+"	and players.player_id = pieces.player_id and players.username = ?");
					stmt1.setInt(1, playerColor);
					stmt1.setString(2, username);
					
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
	
	public void dropTables() {
		executeTransaction(new Transaction<Boolean>() {

			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;

				try {
					stmt1 = conn.prepareStatement(
							"drop table pieces");					
				 stmt1.executeUpdate();
				 stmt2 = conn.prepareStatement(
							"drop table players");					
				 stmt2.executeUpdate();
				 stmt3 = conn.prepareStatement(
							"drop table saveStates");					
				 stmt3.executeUpdate();
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
				return true;
			}});
	}
	@Override
	public void saveGame(String gameMoves, boolean turn) {
		// TODO Auto-generated method stub
		/* 1 Check to see if username is in query
		 * 2 If in query, remove
		 * 3 Add gameMoves, username
		 * username is already set in this.username, gameMoves will be passed in
		 */
		executeTransaction(new Transaction<Boolean>() {
			
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				try {
					stmt1 = conn.prepareStatement(
							"select * from saveStates where username = ? "
					);
					stmt1.setString(1, username);
					ResultSet resultSet = stmt1.executeQuery();
					if(resultSet.next()) {
					stmt2 = conn.prepareStatement(
							"delete from saveStates where username = ?"
					);
					stmt2.setString(1, username);
					stmt2.executeUpdate();
					}
					stmt3 = conn.prepareStatement(
							"insert into saveStates (username, gameMoves, isWhiteTurn) values (?,?,?)"
					);
					stmt3.setString(1, username);
					stmt3.setString(2, gameMoves);
					stmt3.setString(3, ""+turn);
					stmt3.executeUpdate();
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
				}
				return true;
			}});
	}
	@Override
	public boolean doesSaveExist() {
		boolean[] exists = new boolean[1];
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {

				PreparedStatement stmt1 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select * from saveStates where username = ?");
					stmt1.setString(1, username);
					
					ResultSet resultSet = stmt1.executeQuery();
					if(resultSet.next()) {
						exists[0] = true;
					}
					
				}finally {
					DBUtil.closeQuietly(stmt1);
				}
				return true;
			}});
		return exists[0];
	}
	@Override
	public String loadGame() {
		String[] gameMoves = new String[1];
		executeTransaction(new Transaction<Boolean>() {
			
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				try {
					stmt1 = conn.prepareStatement(
							"select gameMoves, isWhiteTurn from saveStates where username = ? "
					);
					stmt1.setString(1, username);
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					gameMoves[0] = resultSet.getObject(1).toString();
					System.out.println(resultSet.getObject(2).toString());
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
				}
				return true;
			}});
		this.resetLocations();
		return gameMoves[0];
	}
	@Override
	public void overwritePieces(Player[] players) {
		this.resetLocations();
executeTransaction(new Transaction<Boolean>() {
		
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;
				PreparedStatement stmt4 = null;
				PreparedStatement stmt5 = null;
				PreparedStatement stmt6 = null;
				PreparedStatement stmt7 = null;

				try {
					stmt5 = conn.prepareStatement(
							"select player_id from players where username = ?"
					);
					stmt5.setString(1, username);
					ResultSet resultSets = stmt5.executeQuery();
					resultSets.next();
					int deletingId = Integer.parseInt(resultSets.getObject(1).toString());
					stmt6 = conn.prepareStatement(
							"delete from pieces where player_id = ?"
					);
					stmt6.setInt(1, deletingId);
					stmt6.executeUpdate();
					DBUtil.closeQuietly(stmt6);
					stmt7 = conn.prepareStatement(
							"delete from players where username = ?"
					);
					stmt7.setString(1, username);
					stmt7.executeUpdate();
					DBUtil.closeQuietly(stmt7);
					DBUtil.closeQuietly(stmt5);

					stmt4 = conn.prepareStatement(
							"insert into players (username) values(?)"
					);
					stmt4.setString(1, username);
					stmt4.executeUpdate(); 
					DBUtil.closeQuietly(stmt4);
					//For Each Player, grab the created Player id
					stmt1 = conn.prepareStatement(
							"select player_id from players where username = ?"
					);
					stmt1.setString(1, username);
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					int player_id = Integer.parseInt(resultSet.getObject(1).toString());
					for(Player player : players) {
						
						for(Piece piece : player.getPieces()) {
							//
							stmt3 = conn.prepareStatement("insert into pieces (color, type, x, y, "
									+ "isCaptured, movedAlready, player_id) values (?, ?, ?, ?,?, ?,?)");
//								insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
							stmt3.setInt(1, piece.getColor());
							stmt3.setString(2, piece.type());
							stmt3.setInt(3, piece.getXpos());
							stmt3.setInt(4,  piece.getYpos());
							stmt3.setString(5,  ""+piece.getCaptured());
							stmt3.setString(6,  ""+piece.getHasMovedAlready());
							stmt3.setInt(7,player_id);
							stmt3.executeUpdate();
							DBUtil.closeQuietly(stmt3);
						}

							
					
					}
				}finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
				}
				return true;
			}});
		
		
	}
	@Override
	public boolean getPlayerTurnFromSave() {
		boolean[] isWhiteTurn = new boolean[1];
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {

				PreparedStatement stmt1 = null;

				try {
					stmt1 = conn.prepareStatement(
							"select isWhiteTurn from saveStates where username = ?");
					stmt1.setString(1, username);
					
					ResultSet resultSet = stmt1.executeQuery();
					resultSet.next();
					isWhiteTurn[0] = Boolean.parseBoolean(resultSet.getObject(1).toString());
					
					
				}finally {
					DBUtil.closeQuietly(stmt1);
				}
				return true;
			}});
		return isWhiteTurn[0];
		
		
	}
	
	@Override
	public void createUser(String username, String password) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement createUser = null;
				try {
					createUser = conn.prepareStatement(
							"insert into users (username, password) values (?,?) "
					);
					createUser.setString(1, username);
					createUser.setString(2, password);
					createUser.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(createUser);

				}
			}
		});
	}
	
	@Override
	public boolean checkExists(String username, String password) {
		boolean[] exists = new boolean[1];
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				
				PreparedStatement getUser = null;

				try {
					getUser = conn.prepareStatement(
							"select * from users where username = ? and password = ? ");
					getUser.setString(1, username);
					getUser.setString(2, password);
					
					ResultSet resultSet = getUser.executeQuery();
					exists[0] = resultSet.next();
					
					
				}finally {
					DBUtil.closeQuietly(getUser);
				}
				return true;
			}});
		return exists[0];
		
		
	}
	
	@Override
	public boolean checkUsernameExists(String username) {
		boolean[] exists = new boolean[1];
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				
				PreparedStatement getUser = null;

				try {
					getUser = conn.prepareStatement(
							"select * from users where username = ? ");
					getUser.setString(1, username);
					
					ResultSet resultSet = getUser.executeQuery();
					exists[0] = resultSet.next();
					
					
				}finally {
					DBUtil.closeQuietly(getUser);
				}
				return true;
			}});
		return exists[0];
		
		
	}
	
	@Override
	public User getUser(String username, String password) {
		User[] user = new User[1];
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				
				PreparedStatement getUser = null;

				try {
					getUser = conn.prepareStatement(
							"select username, password from users where username = ? ");
					getUser.setString(1, username);
					
					ResultSet resultSet = getUser.executeQuery();
					resultSet.next();
					
					user[0] = new User(resultSet.getString(1), resultSet.getString(2));
					
					
				}finally {
					DBUtil.closeQuietly(getUser);
				}
				return true;
			}});
		return user[0];
		
		
	}
	
	@Override
	public void deleteUser(String username) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement deleteUser = null;
				try {
					deleteUser = conn.prepareStatement(
							"delete from users where username = ? "
					);
					deleteUser.setString(1, username);
					deleteUser.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(deleteUser);

				}
			}
		});
	}
	
	@Override
	public void changeUsername(String username, String password, String newUsername) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement deleteUser = null;
				try {
					deleteUser = conn.prepareStatement(
							"update users set username = ? where username = ? and password = ? "
					);
					deleteUser.setString(1, newUsername);
					deleteUser.setString(2, username);
					deleteUser.setString(3, password);
					deleteUser.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(deleteUser);

				}
			}
		});
	}
	
	@Override
	public void changePassword(String username, String password, String newPassword) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement updateUser = null;
				try {
					updateUser = conn.prepareStatement(
							"update users set password = ? where username = ? and password = ? "
					);
					updateUser.setString(1, newPassword);
					updateUser.setString(2, username);
					updateUser.setString(3, password);
					updateUser.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(updateUser);

				}
			}
		});
	}

}
