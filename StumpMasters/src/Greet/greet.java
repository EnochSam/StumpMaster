package Greet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class greet
 */
@WebServlet("/greet")
public class greet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out=null;
		try {
			out=response.getWriter();
			String n = request.getParameter("t1");
			
			out.println("<center>");
			out.println("Hello, " + n);
		}
		catch(Exception e) {
			out.println("Error: " + e.getMessage());
		}
		finally {
			out.println("<br></br>");
			out.println("To go to MainPage, <a href=index.html> ClICK HERE </a>");
			out.println("</center>");
		}
	}

}
