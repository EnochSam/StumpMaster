package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.GameController;

/**
 * Servlet implementation class MainMenu
 */
@WebServlet("/MainMenu")
public class MainMenuServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//Get Menu JSP File
		//Checks to see if there is a stored Game
		GameController controller = new GameController();
		/*if(controller.doesSaveExist(request.getParameter("username"))) {
			request.setAttribute("loadGame", true);
		}*/
		if(request.getParameter("username") != null) {
			request.setAttribute("username",request.getParameter("username") );
		}
		RequestDispatcher rd = request.getRequestDispatcher("view/menu.jsp");
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
