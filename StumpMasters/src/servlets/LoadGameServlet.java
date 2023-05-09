package servlets;

import java.io.IOException;

//import org.json.JSONException;
import org.json.JSONObject;    
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
@WebServlet("/Load")
public class LoadGameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		//Set Controller and Models
		//GameController controller = new GameController();
		
		GameController controller = new GameController();
		Game model = new Game();
		controller.setModel(model);
		controller.setUsername(request.getParameter("username"));
		controller.loadGame();
		//response.
		GameServlet StringCreation = new GameServlet();
		
		JSONObject board = new JSONObject();
		JSONObject rightCaptured = new JSONObject();
		JSONObject leftCaptured = new JSONObject();
		String[][] loadBoard =  StringCreation.loadBoard(request, model.getBoard());
		try{
			board.put("board", loadBoard);
			request.setAttribute("boardJson", board.toString());
			rightCaptured.put("rightCaptured",controller.getCapturedPieces(Piece.BLACK));
			request.setAttribute("rightCaptured", rightCaptured.toString());
			leftCaptured.put("leftCaptured", controller.getCapturedPieces(Piece.WHITE));
			request.setAttribute("leftCaptured", leftCaptured.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		controller.serverCheckForMateIn();
		request.setAttribute("board",loadBoard);
		request.setAttribute("gameMoves", model.getGameMoves());
		if(model.getWhitePlayer().getTurn()){
			request.setAttribute("playerTurn", "White");
		}else {
			request.setAttribute("playerTurn", "Black");

		}
		request.setAttribute("selectingPiece","True");
		request.setAttribute("selectValidMoves","false");
		request.setAttribute("mateinone", model.getChekForMateInOne());
		request.setAttribute("username", request.getParameter("username"));
		request.setAttribute("beginningOfGame", false);
		request.setAttribute("Save", "Save");
		request.setAttribute("tile", "Save");
		RequestDispatcher rd = request.getRequestDispatcher("view/load.jsp");
		rd.forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}
