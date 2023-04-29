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
	
	private void checkForCheck() {
		//Creates King of Both Players
		King WhiteKing = (King) this.model.getWhitePlayer().getPieces()[0];
		King BlackKing = (King) this.model.getBlackPlayer().getPieces()[0];
		
		//Checks Both Kings to See if they Are in Check
		WhiteKing.checkForCheckMate(this.model.getBoard());
		BlackKing.checkForCheckMate(this.model.getBoard());
		
		if(WhiteKing.getInCheck()) {
			//if the WhiteKing is in Check, set list of availableMoves to Kings getOutOfCheckList
			this.model.setAvailableMoves(WhiteKing.getGetOutOfCheckMoves());
			this.model.setInCheck(true);
			this.checkForCheckMate(WhiteKing);
		}else if(BlackKing.getInCheck()) {
			this.model.setAvailableMoves(BlackKing.getGetOutOfCheckMoves());
			this.model.setInCheck(true);
			this.checkForCheckMate(BlackKing);
		}else {
			this.model.setInCheck(false);
		}
		
	}
	
	private Boolean checkForCheckMate(King king) {
		//Check if king can move
		String checkKingTile = ""+(king.getXpos()+1)+":"+(king.getYpos()+1);
		Player player;
		String playerTurn;
		if(king.getColor() == Piece.WHITE) {
			player = this.model.getWhitePlayer();
			playerTurn ="White";
		}
		else{
			player = this.model.getBlackPlayer();
			playerTurn = "Black";
		}
		if(this.getPossibleMoves(checkKingTile, playerTurn)!="") {
			return false;
		}
		//3 WAYS TO Instances of Checkmate:
		//1. Can the king move out of the way
		//2. Can a piece of same color reach the opposing location reach opposing piece
			
		//1. Find the piece of opposing team that can reach king
		Piece piecesthatcanReachKing = null;
		for(int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				
				if(this.model.getBoard()[j][i] != null) {
					boolean canreach = false;
					for(Integer[] loc : this.model.getBoard()[j][i].getValidMoves(this.model.getBoard())) {
						if(loc[0] == king.getXpos() && loc[1] == king.getYpos()) {
						
							canreach = true;

							}
						}
						if(canreach) {
							piecesthatcanReachKing = this.model.getBoard()[j][i];
						}
					}
				}
			}
			
			//Adds Location of attacking Piece to getOutOfCheckMovesList
			Integer[] availableMove = {};
			String getOutOfCheckMoves = ""+piecesthatcanReachKing.getXpos()+""+piecesthatcanReachKing.getYpos()+" ";
			//Check if the piece is attacking vertically or diagonal horizontally 
			if(piecesthatcanReachKing.getXpos() == king.getXpos() || piecesthatcanReachKing.getYpos() == king.getYpos()
				|| Math.abs(piecesthatcanReachKing.getXpos() - king.getXpos())
				== Math.abs(piecesthatcanReachKing.getYpos() - king.getYpos())) {
				int xincrement = 0;
				if(piecesthatcanReachKing.getXpos() - king.getXpos() > 0) xincrement = 1;
				if(piecesthatcanReachKing.getXpos() - king.getXpos() < 0) xincrement = -1;
				int yincrement = 0;
				if(piecesthatcanReachKing.getYpos() - king.getYpos() > 0) yincrement = 1;
				if(piecesthatcanReachKing.getYpos() - king.getYpos() < 0) yincrement = -1;
				int x = king.getXpos()+xincrement;
				int y = king.getYpos()+yincrement;

				while(this.model.getBoard()[y][x] != piecesthatcanReachKing) {
					getOutOfCheckMoves+=""+x+""+y+" ";
					x+=xincrement;
					y+=yincrement;
				}
			}
			//finally, check all pieces to see if any piece is able to reach any moves
			
			for(int i = 1; i < player.getPieces().length; i++) {
				Piece testingPiece = player.getPieces()[i];
				if(testingPiece.getCaptured() == false) {
				String tileSelected = ""+(testingPiece.getXpos()+1)+":"+(testingPiece.getYpos()+1);
				String possibleMoves = this.getPossibleMoves(tileSelected, playerTurn);
				try{
				for(int j = 0; j < possibleMoves.length(); j+=3) {
					for(int k = 0; k < getOutOfCheckMoves.length(); k+=3){
						int possibleMovesXpos = Integer.parseInt(""+possibleMoves.charAt(j)) -1;
						int possibleMovesYpos = Integer.parseInt(""+possibleMoves.charAt(j+1)) -1;
						int getOutofCheckXpos = Integer.parseInt(""+getOutOfCheckMoves.charAt(k));
						int getOutofCheckYpos = Integer.parseInt(""+getOutOfCheckMoves.charAt(k+1));
						if(possibleMovesXpos == getOutofCheckXpos && possibleMovesYpos == getOutofCheckYpos) {
							return false;
						}
					}
				
				}
				}catch(NumberFormatException e) {
					
				}
				}
			}	
			this.model.setInCheckmate(true);
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
		this.model.setGameMoves(boardLocations);
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
			
					// Check for pawn promotion
					int pieceID = 40;
					// Query any pawns that are at either side of the board
					PreparedStatement checkPromotion = conn.prepareStatement(
							"select pieces.piece_id from pieces " +
							" where pieces.type = 'Pawn' and pieces.isCaptured = 'false' " +
							" and (pieces.y = 0 or pieces.y = 7) ");
					resultSet = checkPromotion.executeQuery();
					
					if(resultSet.next()) {
						pieceID = resultSet.getInt(1);
						
						// Update pawn to have new type of player's choice
						PreparedStatement promote = conn.prepareStatement(
								"update pieces "+
								" set pieces.type = ? "+
								" where pieces.piece_id = ? ");
						
						promote.setInt(2, pieceID);
						
						String gameMoves = model.getGameMoves();
						if(gameMoves.charAt(gameMoves.length() - 4) == 'Q') {
							promote.setString(1, "Queen");
						}
						else if(gameMoves.charAt(gameMoves.length() - 4) == 'R') {
							promote.setString(1, "Rook");
						}
						else if(gameMoves.charAt(gameMoves.length() - 4) == 'B') {
							promote.setString(1, "Bishop");
						}
						else if(gameMoves.charAt(gameMoves.length() - 4) == 'K') {
							promote.setString(1, "Knight");
						}
						int update = promote.executeUpdate();
					}
						
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
						int ID = resultSet.getInt(6);
						Piece piece = Piece.findPiece(type);
						piece.setXpos(x);
						piece.setYpos(y);
						piece.setColor(color);
						piece.setHasMovedAlready(movedAlready);
						piece.setID(ID);
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
		return (boolean)executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				String validMoves = getPossibleMoves(attemptingToMove,player);
				
				int selectedPieceXpos = Integer.parseInt(""+attemptingToMove.charAt(0))-1;
				int selectedPieceYpos = Integer.parseInt(""+attemptingToMove.charAt(2))-1;
				
				int newPieceXpos = Integer.parseInt(""+newPieceLoc.charAt(0))-1;
				int newPieceYpos = Integer.parseInt(""+newPieceLoc.charAt(2))-1;
				if(selectedPieceXpos == newPieceXpos && selectedPieceYpos == newPieceYpos) {
					return true;
				}
				Integer[] loc;
				List<Integer[]> possibleMoves = new ArrayList<Integer[]>(); 
				for(int i = 0; i < validMoves.length(); i+=3) {
					loc = new Integer[2];
					loc[0] = Integer.parseInt(""+validMoves.charAt(i))-1;
					loc[1] = Integer.parseInt(""+validMoves.charAt(i+1))-1;
					possibleMoves.add(loc);
				}
				for(Integer[] x : possibleMoves) {
					if(x[0] == newPieceXpos && x[1] == newPieceYpos) {
						if(model.getBoard()[newPieceYpos][newPieceXpos] != null) {
							model.getBoard()[newPieceYpos][newPieceXpos].setCaptured(true); 
						}
						model.getBoard()[newPieceYpos][newPieceXpos] = model.getBoard()[selectedPieceYpos][selectedPieceXpos];
						model.getBoard()[selectedPieceYpos][selectedPieceXpos] = null;
						model.getBoard()[newPieceYpos][newPieceXpos].setXpos(newPieceXpos);
						model.getBoard()[newPieceYpos][newPieceXpos].setYpos(newPieceYpos);
						checkForCheck();
						//Checks for Pawn Promotion
						if(model.getBoard()[newPieceYpos][newPieceXpos] instanceof Pawn && 
								(model.getBoard()[newPieceYpos][newPieceXpos].getYpos() == 0
							  || model.getBoard()[newPieceYpos][newPieceXpos].getYpos() == 7)) {
							model.setPawnPromotion(true);
						}else
						//Checks for Castling
						if(model.getBoard()[newPieceYpos][newPieceXpos] instanceof King) {
							King king =(King) model.getBoard()[newPieceYpos][newPieceXpos];
							for(String castleLoc : king.getCastlingLocation()) {
								if(castleLoc != null) {
									int rookOldXpos = -1;
									int rookNewXpos = -1;
									if(castleLoc.charAt(0) == '8') {
										if( model.getBoard()[newPieceYpos][7] instanceof Rook && !model.getBoard()[newPieceYpos][7].getHasMovedAlready()) {
										//Rook is on the Right and needs to be moved to the Left
										Piece rook = model.getBoard()[newPieceYpos][7];
										rook.setXpos(newPieceXpos-1);
										rook.setYpos(newPieceYpos);
										model.getBoard()[newPieceYpos][7] = null;
										model.getBoard()[rook.getYpos()][rook.getXpos()] = rook;
										rookOldXpos = 8;
										rookNewXpos = 6;
										}
									}else {
										if( model.getBoard()[newPieceYpos][0] instanceof Rook && !model.getBoard()[newPieceYpos][0].getHasMovedAlready()) {
										//Rook is on the Left and needs to be moved to the left
										Piece rook = model.getBoard()[newPieceYpos][0];
										rook.setXpos(newPieceXpos+1);
										rook.setYpos(newPieceYpos);
										model.getBoard()[newPieceYpos][0] = null;
										model.getBoard()[rook.getYpos()][rook.getXpos()] = rook;							
										rookOldXpos = 1;
										rookNewXpos = 4;
										}
									}
									//Split String into 2 and add the string into GameMoves
									String leftOfSplit = model.getGameMoves().substring(0, model.getGameMoves().length() -2 );
									String rightOfSplit = model.getGameMoves().substring(model.getGameMoves().length() -2 );
									model.setGameMoves( leftOfSplit+rookOldXpos+(newPieceYpos+1)+rookNewXpos+(newPieceYpos+1)+" "+rightOfSplit);
								}
							}
							
						}
						
						//Get piece color
						PreparedStatement getColor = conn.prepareStatement(
								"select pieces.color from pieces "
								+ " where pieces.x = ? and pieces.y = ? ");
						
						// Substitute values into query
						getColor.setInt(1, selectedPieceXpos);
						getColor.setInt(2, selectedPieceYpos);
						
						// Execute query
						ResultSet resultSet = getColor.executeQuery();
						int color = 2;
						
						// get color
						if(resultSet.next()) {
							color = resultSet.getInt(1);
						}
						
						// Update captured Piece
						PreparedStatement getCapturedPiece = conn.prepareStatement(
								"select pieces.piece_id from pieces "
								+ " where pieces.x = ? and pieces.y = ? "
								+ " and pieces.isCaptured = 'false' and not pieces.color =  ?"
							);
						
						// Substitute values into query
						getCapturedPiece.setInt(1, newPieceXpos);
						getCapturedPiece.setInt(2, newPieceYpos);
						getCapturedPiece.setInt(3, color);
						
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
						
						// Get the selected piece id
						PreparedStatement getPieceId = conn.prepareStatement(
								"select pieces.piece_id from pieces"
								+ " where pieces.x = ? and pieces.y = ? "
								+ " and pieces.isCaptured = 'false' ");
						
						// Substitute selected piece x and y positions into query
						getPieceId.setInt(1, selectedPieceXpos);
						getPieceId.setInt(2, selectedPieceYpos);
						
						// Execute Query
						resultSet = getPieceId.executeQuery();
						
						//Set id
						int id = 0;
						if(resultSet.next()) {
							id = resultSet.getInt(1);
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
													" set pieces.x = ?, pieces.y = ? " +
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
						return true;
						}
					}
				return false;
			}
		});
		}

}
