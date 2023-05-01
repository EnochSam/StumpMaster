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
					// populate players table (do authors first, since author_id is foreign key in books table)
					insertPlayer = conn.prepareStatement("insert into players (type) values (?)");
					for (Player player : playerList) {
//						// auto-generated primary key, don't insert this
						insertPlayer.setString(1,player.getType());
						insertPlayer.addBatch();
					}
					insertPlayer.executeBatch();
					
					System.out.println("Passed1");
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
					System.out.println("Executed");
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
		System.out.println("Doone");
	}
	
	/*
	public String getPossibleMoves(String clickedOnLocation, String playerTurn) {
		return executeTransaction(new Transaction<String>() {
			@Override
			public String execute(Connection conn) throws SQLException {
				String listOfLocations = "";
				int pieceXpos = Integer.parseInt(""+clickedOnLocation.charAt(0))-1;
				int pieceYpos = Integer.parseInt(""+clickedOnLocation.charAt(2))-1;
				
				Piece selectedPiece = model.getBoard()[pieceYpos][pieceXpos];
				
				// Check if moved already
				PreparedStatement getMovedAlready = conn.prepareStatement(
						"select pieces.movedAlready from pieces "
						+ " where pieces.x = ? and pieces.y = ? and pieces.isCaptured = 'false' ");
				
				// Substitute x and y values into call
				getMovedAlready.setInt(1, pieceXpos);
				getMovedAlready.setInt(2, pieceYpos);
				
				ResultSet resultSet = getMovedAlready.executeQuery();
				
				// Load has moved already into piece object
				if(resultSet.next()) {
					if(resultSet.getString(1).equals("false")) {
						selectedPiece.setHasMovedAlready(false);
					}
					else {
						selectedPiece.setHasMovedAlready(true);
					}
				}
				

				//Checks for EnPassant
				PreparedStatement getEnPassant = conn.prepareStatement(
						"select enPassantX, enPassantY from pieces "+
						" where piece_id = ? and enPassant = 'true' ");
				
				
				resultSet = getEnPassant.executeQuery();
				
				if(resultSet.next()) {
					int enPassantX = Integer.parseInt(resultSet.getString(1));
					int enPassantY = Integer.parseInt(resultSet.getString(2));
					
					listOfLocations+= ""+(enPassantX + 1);
					listOfLocations+= ""+(enPassantY + 1);
					listOfLocations+=" ";
				}
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

	public void setBoard(String boardLocations) {
		
	}
	*/
	/*
	public boolean moveValidMove(String newPieceLoc, String attemptingToMove, String player) {
		return (boolean)executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
						// Get the selected piece id
						PreparedStatement getPieceId = conn.prepareStatement(
								"select pieces.piece_id, pieces.type, pieces.color, pieces.movedAlready, pieces.enPassantX, pieces.enPassantY from pieces"
								+ " where pieces.x = ? and pieces.y = ? "
								+ " and pieces.isCaptured = 'false' ");
						
						// Substitute selected piece x and y positions into query
						getPieceId.setInt(1, selectedPieceXpos);
						getPieceId.setInt(2, selectedPieceYpos);
						
						// Execute Query
						ResultSet resultSet = getPieceId.executeQuery();
						
						String type = null;
						boolean moved = false;
						int enPassantX = 100;
						int enPassantY = 100;
						int color = 2;
						//Set id
						int id = 0;
						if(resultSet.next()) {
							id = resultSet.getInt(1);
							type = resultSet.getString(2);
							moved = Boolean.getBoolean(resultSet.getString(4));
							color = resultSet.getInt(3);
							enPassantX = resultSet.getInt(5);
							enPassantY = resultSet.getInt(6);
						}

						
						// Update captured Piece
						PreparedStatement getCapturedPiece = conn.prepareStatement(
								"select pieces.piece_id from pieces "
								+ " where pieces.x = ? and pieces.y = ? "
								+ " and pieces.isCaptured = 'false' and not pieces.color =  ?"
							);
						// If piece performed en passant, capture piece directly behind
						getCapturedPiece.setInt(1, newPieceXpos);
						getCapturedPiece.setInt(3, color);
						
						if(newPieceXpos == enPassantX && newPieceYpos == enPassantY) {
							if(color == 0) {
								getCapturedPiece.setInt(2, newPieceYpos - 1);
							}
							else {
								getCapturedPiece.setInt(2, newPieceYpos + 1);
							}
						}	// Otherwise capture piece at position moved to
						else {
							getCapturedPiece.setInt(2, newPieceYpos);
						}
						
						// Execute Query
						resultSet = getCapturedPiece.executeQuery();
						
						if(resultSet.next()) {
							// Mark piece as captured
							PreparedStatement setCaptured = conn.prepareStatement(
									"update pieces "
									+ " set pieces.isCaptured = 'true' "
									+ " where pieces.piece_id = ? ");
							setCaptured.setInt(1, resultSet.getInt(1));
							int updateCapture = setCaptured.executeUpdate();
						}
						
						// Update the selected piece location
						PreparedStatement updatePiece = conn.prepareStatement(
								"update pieces "
								+ " set pieces.x = ?, pieces.y = ?, movedAlready = 'true' "
								+ " where piece_id = ?");
						
						//Substitute id into query
						updatePiece.setInt(1, newPieceXpos);
						updatePiece.setInt(2, newPieceYpos);
						updatePiece.setInt(3, id);
						
						// Execute Query
						int update = updatePiece.executeUpdate();
						
						
						// Update new locations of any pieces that moved (castling)
						PreparedStatement setLocation = null;
						
						//Iterate through board to find every piece
						for(Piece[] pieces: model.getBoard()){
								for(Piece piece: pieces) {
									if(piece != null) {
										if(piece.getCaptured() == false) {
											// Update the pieces current x,y locations
											setLocation = conn.prepareStatement(
													"update pieces "+
													" set pieces.x = ?, pieces.y = ?, pieces.enPassant = 'false' " +
													" where piece_id = ? ");
											
											setLocation.setInt(1, piece.getXpos());
											setLocation.setInt(2, piece.getYpos());
											setLocation.setInt(3, piece.getID());
											
											System.out.println("X: " + piece.getXpos() + " Y: " + piece.getYpos());
											update = setLocation.executeUpdate();
											System.out.println("Updating Piece: " + piece.type());
										}
									}
								}
						}
						
						if(type.equals("Pawn") && moved == false)
						{
							// Update opponent pawn to be able to perform en passant on current pawn
							PreparedStatement enPassant = conn.prepareStatement(
									"update pieces "+
									" set enPassant = 'true', enPassantX = ?, enPassantY = ? "+
									" where color = ? and (x = ? or x = ?) and y = ?");
							
							// Mark position that opponent pawn will move to to perform en passant
							enPassant.setInt(1, selectedPieceXpos);
							if(color == 0) {
								enPassant.setInt(2, selectedPieceYpos + 1);
							}
							else {
								enPassant.setInt(2, selectedPieceYpos - 1);
							}
							
							// Find pawn of opponent's color
							if(color == 0) {
								enPassant.setInt(3, 1);
							}
							else {
								enPassant.setInt(3, 0);
							}
							
							// Find opponent's pawns that are in position to perform en passant
							enPassant.setInt(4,  selectedPieceXpos - 1);
							enPassant.setInt(5,  selectedPieceXpos + 1);
							
							if(color == 0) {
								enPassant.setInt(6, selectedPieceYpos + 2);
							}
							else {
								enPassant.setInt(6, selectedPieceYpos - 2);
							}
							
							update = enPassant.executeUpdate();
						}
						return true;
						}
					}
				return false;
			}
		});
		}
	*/
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
		System.out.println("Resettin Locatoins...");
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
		System.out.println("Creating Tables");
		createTables();
		System.out.println("Loading Initial Data");
		loadInitialData();
		System.out.println("Locations Reset");
	}	
	@Override
	public Player populatePlayer(int playerColor) {
		executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				try {
					stmt1 = conn.prepareStatement(
							"select * from pieces where x = ? and y = ? and isCaptured = false"
					);
					ResultSet resultSet = stmt1.executeQuery();
					while(resultSet.next()) {
						for(int i = 1; i <= 6; i++) {
							System.out.println(resultSet.getObject(i));
						}
					}
				}finally {
					DBUtil.closeQuietly(stmt1);;

				}
			return null;
			}});
		return null;
	}
	@Override
	public Piece getPiece(int pieceXpos, int pieceYpos){
		Piece piece[] = new Piece[1];
		executeTransaction(new Transaction<Boolean>() {
		public Boolean execute(Connection conn) throws SQLException {
			PreparedStatement stmt1 = null;
			try {
				stmt1 = conn.prepareStatement(
						"select * from pieces where x = ? and y = ? and isCaptured = false"
				);
				stmt1.setInt(1, pieceXpos);
				stmt1.setInt(2, pieceYpos);
				ResultSet resultSet = stmt1.executeQuery();
				//piece;
				while(resultSet.next()) {
					for(int i = 1; i <= 6; i++) {
						System.out.println(resultSet.getObject(i));
					}
				 piece[0] = Piece.findPiece(""+resultSet.getObject(3));
					
				}
			}finally {
				DBUtil.closeQuietly(stmt1);;

			}
			return true;
		}});
		return piece[0];
	}
	@Override
	public void updateDatabase(Piece[][] board) {
		/* TODO Will update the location of piece based off
		 * old location sent in, if piece at new location, update their isCaptured to true BEFORE updating Piece 
		 */
		
	}
	@Override
	public void updateDatabaseForCastling(int rookXpos, int rookYpos) {
		/* TODO Passed in are the original x and y positions of Rook, 
		 * if RookXpos == 0, left Castle, else, Right Castle 
		 * Update rook's position in Database
		 */
		
		
	}
	@Override
	public void updateDatabaseForEnPassant(int xpos, int ypos, int color) {
		/* TODO Passed in are the new x and y positions of Pawn, 
		 * if Pawn.color == White, captured piece== ypos -1, else, captured piece == ypos +1
		 * Update the captured piece's location in database
		 */
		
	}

}
