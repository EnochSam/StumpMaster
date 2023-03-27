package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pieceModels.Bishop;
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

		Piece[][] board = loadTestBoard();
		String[][] loadBoard = loadBoard(request,board);
		request.setAttribute("board", loadBoard);
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
				}else if(board[i][j] instanceof Piece) {
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
	
	protected Piece[][] loadTestBoard() {
		Piece board[][] = new Piece[8][8];
		//Load White Pawn
		board[1][0] = new Pawn(0,1,Piece.WHITE);
		board[1][1] = new Pawn(1,1,Piece.WHITE);
		board[1][2] = new Pawn(2,1,Piece.WHITE);
		board[1][3] = new Pawn(3,1,Piece.WHITE);
		board[1][4] = new Pawn(4,1,Piece.WHITE);
		board[1][5] = new Pawn(5,1,Piece.WHITE);
		board[1][6] = new Pawn(6,1,Piece.WHITE);
		board[1][7] = new Pawn(7,1,Piece.WHITE);
		
		board[0][0] = new Rook(0,0,Piece.WHITE);
		board[0][7] = new Rook(7,0,Piece.WHITE);
		
		board[0][1] = new Knight(0,0,Piece.WHITE);
		board[0][6] = new Knight(7,0,Piece.WHITE);
		
		board[0][2] = new Bishop(0,0,Piece.WHITE);
		board[0][5] = new Bishop(7,0,Piece.WHITE);
		
		board[0][4] = new Queen(4,0,Piece.WHITE);
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
