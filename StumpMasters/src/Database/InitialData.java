package Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Player;
import pieceModels.Piece;

public class InitialData {
	public static List<Piece> getPieces() throws IOException {
		List<Piece> pieceList = new ArrayList<Piece>();
		ReadCSV readPieces = new ReadCSV("PieceStartLocation.csv");
		try {
			// auto-generated primary key for pieces table
			while (true) {
				List<String> tuple = readPieces.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				int color = Integer.parseInt(i.next());
				
				Piece piece = Piece.findPiece(i.next());
				try {
				piece.setColor(color);
				}catch(Exception e) {
				piece.setColor(0);
				}
				piece.setXpos(Integer.parseInt(i.next()));			
				piece.setYpos(Integer.parseInt(i.next()));
				piece.setCaptured(Boolean.parseBoolean(i.next()));
				pieceList.add(piece);
			}
			return pieceList;
		} finally {
			readPieces.close();
		}
	}
	
	public static List<Player> getPlayer() throws IOException {
		List<Player> playerList = new ArrayList<Player>();
		ReadCSV playerPieces = new ReadCSV("Player.csv");
		try {
			// auto-generated primary key for authors table
			while (true) {
				List<String> tuple = playerPieces.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				Player player = new Player();
				try {
				player.setColor(Integer.parseInt(i.next()));
				}catch( Exception e){
					player.setColor(0);
				}
				player.setType(i.next());
				playerList.add(player);
			}
			return playerList;
		} finally {
			playerPieces.close();
		}
	}
}
