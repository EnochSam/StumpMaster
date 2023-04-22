package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Database.DerbyDatabase;
import Database.IDatabase;
import controllers.GameController;
import models.Game;
import pieceModels.Bishop;
import pieceModels.King;
import pieceModels.Knight;
import pieceModels.Pawn;
import pieceModels.Piece;
import pieceModels.Queen;
import pieceModels.Rook;

/**
 * Servlet implementation class Game
 */
@WebServlet("/Game")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//Set Controller and Models
		Game model = new Game();
		//GameController controller = new GameController();
		IDatabase controller = new DerbyDatabase();
		controller.setModel(model);
		//Variable From client to retrieve all moves made in the round
		String gameMoves = "";
		//Variable From client to see whose turn it is
		String playerTurn = "";
		//Variable From client to see if the Player is in the "Selecting Piece" Phase
		String selectingPiece = "";
		//Varibale Form client to see if the Player is in the "SelectValidMoves"Phase
		String selectValidMoves = "";
		//Variable to retrieve which tile is being selected
		String tileSelected = "";
		
		//Variable to retrieve which Piece the Queen is getting promoted to 
		String pawnPromotion = "";
			//Grabs all variables from client
			pawnPromotion = request.getParameter("pawnPromotion");
			tileSelected = request.getParameter("tile");
			if(tileSelected !=null || pawnPromotion!= null ) {
			gameMoves = request.getParameter("gameMoves");
			playerTurn = request.getParameter("playerTurn");
			selectingPiece = request.getParameter("selectingPiece");
			selectValidMoves = request.getParameter("selectValidMoves");
			//to tell client to save session data
			request.setAttribute("beginningOfGame", false);
			
			}else {
			//If tileSelected == null, this is the creation of the Game
			gameMoves = "";
			playerTurn = "White";
			selectingPiece = "True";
			selectValidMoves = "False";
			tileSelected = "START";
			//to tell Servlet to save the sessionData
			request.setAttribute("beginningOfGame", true);
			}	
			
			try {
				controller.setBoard(gameMoves);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Checks if the Player is selecting a piece
			if(selectingPiece.equals("True") && !(tileSelected.equals("START"))) {
				//Checks to see if there is a player's piece at specified location
				try { 
					//Grabs location from client
					//sends string version of possible list to views
					String getPossibleMoves = controller.getPossibleMoves(tileSelected,playerTurn);
					if(!getPossibleMoves.equals("False") && !getPossibleMoves.contentEquals("")) {
						request.setAttribute("possibleMoves",getPossibleMoves);
						selectingPiece = "False";
						selectValidMoves = "True";
					}else {
						selectingPiece = "True";
						selectValidMoves = "False";
					}
					
				}catch(Exception e){
					//PrintWriter write = response.getWriter();
					//write.println(e.toString()+"<br></br>");
				}
				
		}else
		if(selectValidMoves.equals("True")) {
			String pieceAttemptingToMove = gameMoves.substring(gameMoves.length()-2, gameMoves.length());
			String previousTitle = ""+pieceAttemptingToMove.charAt(0)+":"+pieceAttemptingToMove.charAt(1);
			Boolean isMoveValid = controller.moveValidMove(tileSelected,previousTitle,playerTurn );
			if(!isMoveValid) {
				selectValidMoves = "True";
				selectingPiece = "False";
				
				//Convert Piece into a tile to pass to getPossibleMoves;
				request.setAttribute("possibleMoves",controller.getPossibleMoves(previousTitle,playerTurn));
					
			}else 
			{
				
				if(previousTitle.equals(tileSelected)){
					request.setAttribute("AddMove","No");
					
				}
				else if(playerTurn.equals("White")){
					playerTurn = "Black";
				}else {
					playerTurn = "White";
				}
				selectValidMoves = "False";
				selectingPiece = "True";
				if(model.getPawnPromotion()) {
					request.setAttribute("PawnPromotion",true);
				}
			}
			
		}
		 //let Controller Create Board

		
		
		
		// Set Content Type
		response.setContentType("text/html");
		
		// Get Board JSP File
		RequestDispatcher rd = request.getRequestDispatcher("view/board.jsp");
		
		response.setContentType("text/html");
		PrintWriter out=null;
		try {
			out=response.getWriter();
			out.println("Hello, Here is the Print Request since Enoch is a Poopie: "+ tileSelected);
		}
		catch(Exception e) {
			out.println("Error: " + e.getMessage());
		}
		finally {
			out.println("<br></br>");
			out.println("To go to MainPage, <a href=index.html> ClICK HERE </a>");
			out.println("</center>");
		}

		String[][] loadBoard = loadBoard(request,model.getBoard());
		
		//sends all atributes to client
		request.setAttribute("board", loadBoard);
		request.setAttribute("gameMoves", model.getGameMoves());
		request.setAttribute("playerTurn",playerTurn);
		request.setAttribute("selectingPiece",selectingPiece);
		request.setAttribute("selectValidMoves",selectValidMoves);
		if(model.getInCheckmate()) {
			request.setAttribute("inCheckmate", true);
		}
		else 
			if(model.getInCheck()) {
			request.setAttribute("inCheck", true);
		}
		rd.include(request, response);
	}

	
	protected String[][] loadBoard(HttpServletRequest req, Piece[][] board){
		String[][] pieceLocations = new String[board.length][board[0].length];
		for(int j = 0; j < board.length; j++) {
			for(int i = 0; i < board[0].length; i++) {
				
				String obj;
				if(board[i][j] != null) {
				//selects color to put int obj String
				if(board[i][j].getColor() == Piece.WHITE){
					obj = "White";
				}else{
					obj = "Black";
				}
				if(board[i][j] instanceof Pawn) {
					obj+="Pawn";
				}else
				if(board[i][j] instanceof Rook) {
					obj+="Rook";
				}else
					if(board[i][j] instanceof Bishop) {
						obj+="Bishop";
				}else
				//KnightModel
				if(board[i][j] instanceof Knight) {
						obj+="Knight";
				}else
					if(board[i][j] instanceof Queen) {
						obj+="Queen";
				//KingModel
				}else if(board[i][j] instanceof King) {
					obj+="King";
				}
				pieceLocations[j][i] = obj;
				}else {
					pieceLocations[j][i] = "nothing";
				}
			}
		}
		return pieceLocations;
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
