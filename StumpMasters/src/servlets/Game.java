package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pieceModels.BishopModel;
import pieceModels.KnightModel;
import pieceModels.PawnModel;
import pieceModels.PieceModel;
import pieceModels.QueenModel;
import pieceModels.RookModel;

/**
 * Servlet implementation class Game
 */
@WebServlet("/Game")
public class Game extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// Set Content Type
		response.setContentType("text/html");
		
		// Get Board JSP File
		RequestDispatcher rd = request.getRequestDispatcher("view/board.jsp");
		
		response.setContentType("text/html");
		PrintWriter out=null;
		try {
			out=response.getWriter();
			out.println("Hello, Here is the Print Request since Enoch is a Poopie: "+ request.getParameter("tile"));
		}
		catch(Exception e) {
			out.println("Error: " + e.getMessage());
		}
		finally {
			out.println("<br></br>");
			out.println("To go to MainPage, <a href=index.html> ClICK HERE </a>");
			out.println("</center>");
		}

		PieceModel[][] board = loadTestBoard();
		String[][] loadBoard = loadBoard(request,board);
		request.setAttribute("board", loadBoard);
		rd.include(request, response);
	}

	
	protected String[][] loadBoard(HttpServletRequest req, PieceModel[][] board){
		String[][] pieceLocations = new String[board.length][board[0].length];
		for(int j = 0; j < board.length; j++) {
			for(int i = 0; i < board[0].length; i++) {
				
				String obj;
				if(board[i][j] != null) {
				//selects color to put int obj String
				if(board[i][j].getColor() == PieceModel.WHITE){
					obj = "White";
				}else{
					obj = "Black";
				}
				if(board[i][j] instanceof PawnModel) {
					obj+="Pawn";
				}else
				if(board[i][j] instanceof RookModel) {
					obj+="Rook";
				}else
					if(board[i][j] instanceof BishopModel) {
						obj+="Bishop";
				}else
				//KnightModel
				if(board[i][j] instanceof KnightModel) {
						obj+="Knight";
				}else
					if(board[i][j] instanceof QueenModel) {
						obj+="Queen";
				//KingModel
				}else if(board[i][j] instanceof PieceModel) {
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
	
	protected PieceModel[][] loadTestBoard() {
		PieceModel board[][] = new PieceModel[8][8];
		//Load White Pawn
		board[1][0] = new PawnModel(0,1,PieceModel.WHITE);
		board[1][1] = new PawnModel(1,1,PieceModel.WHITE);
		board[1][2] = new PawnModel(2,1,PieceModel.WHITE);
		board[1][3] = new PawnModel(3,1,PieceModel.WHITE);
		board[1][4] = new PawnModel(4,1,PieceModel.WHITE);
		board[1][5] = new PawnModel(5,1,PieceModel.WHITE);
		board[1][6] = new PawnModel(6,1,PieceModel.WHITE);
		board[1][7] = new PawnModel(7,1,PieceModel.WHITE);
		
		board[0][0] = new RookModel(0,0,PieceModel.WHITE);
		board[0][7] = new RookModel(7,0,PieceModel.WHITE);
		
		board[0][1] = new KnightModel(0,0,PieceModel.WHITE);
		board[0][6] = new KnightModel(7,0,PieceModel.WHITE);
		
		board[0][2] = new BishopModel(0,0,PieceModel.WHITE);
		board[0][5] = new BishopModel(7,0,PieceModel.WHITE);
		
		board[0][4] = new QueenModel(4,0,PieceModel.WHITE);
		return board;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
