package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		rd.include(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
