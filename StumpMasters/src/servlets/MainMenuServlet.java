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
		if(request.getParameter("username") != null) {
			String username = request.getParameter("username");
			GameController controller = new GameController();
			controller.setUsername(username);
			if(controller.doesSaveExist()) {
				request.setAttribute("loadGame", true);
			}
			request.setAttribute("username", username);
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
