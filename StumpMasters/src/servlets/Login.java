package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.LoginController;
import models.inputType;
import sun.security.util.Debug;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private LoginController controller = new LoginController();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		RequestDispatcher rd = request.getRequestDispatcher("view/Login.jsp");
		rd.include(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = null;
		// Get parameters
		// Get Username
		String username = request.getParameter("username");		
		//Get Password
		String password = request.getParameter("password");
		
		// Validate Username and password
		boolean isValid = (controller.validate(username, inputType.USERNAME) && controller.validate(password, inputType.PASSWORD));
		
		// Username and password are valid
		if(isValid) {
			// Create new user
			controller.createUser(username, password);
			
			// Display username and password
			try {
				out=response.getWriter();
			
				out.println("<center>");
				out.println("Username" + username);
			}
			catch(Exception e) {
				out.println("Error: " + e.getMessage());
			}
			finally {
				out.println("<br></br>");
				out.println("</center>");
			}
			
			// Forward to Main menu
			request.getRequestDispatcher("view/menu.jsp").forward(request, response);
		}
	}

}
