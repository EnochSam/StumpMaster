package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Database.DerbyDatabase;
import Database.IDatabase;
import controllers.LoginController;
import models.inputType;
import sun.security.util.Debug;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private LoginController controller = new LoginController();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		IDatabase db = new DerbyDatabase();
		
		// Check if user asked to delete account
		String delete = request.getParameter("delete");
		String username = request.getParameter("username");
		String newUsername = request.getParameter("newUsername");
		
		if(delete != null) {
			if(delete.equals("delete")) {
				db.deleteUser(username);
			}
		}
		else if(newUsername != null) {
			db.changeUsername(username, newUsername);
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("view/Login.jsp");
		rd.include(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		PrintWriter out = null;
		String errorMessage = null;
		String username = null;
		String password = null;
		boolean login = false;
		boolean exists = false;
		// Display username and password
		try {
			// Get parameters
			// Get Username
			username = request.getParameter("username");
			System.out.println("Username: " + username);
			//Get Password
			password = request.getParameter("password");
			System.out.println("Password: " + password);
			
			//User clicked login button
			login = (request.getParameter("login") != null);
		}
		catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		if(login)
		{
			//Check if user exists
			exists = controller.checkExists(username, password);
			if(exists) {
				// Pass user object to main menu
				request.setAttribute("user", controller.getUser(username, password));
				request.setAttribute("username", username);
				
				// Forward to Main menu
				request.getRequestDispatcher("view/menu.jsp").forward(request, response);
			}
			else {
				request.setAttribute("error", "The username or password is not correct");
				doGet(request, response);
			
			}	//User clicked create account button
		
		}
		else {
			// Validate Username and password
			boolean isValid = (controller.validate(username, inputType.USERNAME) && controller.validate(password, inputType.PASSWORD));
			if(isValid) {
					//Username already exists
					if(controller.checkUsernameExists(username) == false) {
						controller.createUser(username, password);
						// Pass user object to main menu
						request.setAttribute("user", controller.getUser(username, password));
						request.setAttribute("username", username);
						
						// Forward to Main menu
						request.getRequestDispatcher("view/menu.jsp").forward(request, response);
					}
					else {
						request.setAttribute("error", "Username is taken, please enter a different username");
						doGet(request, response);
					}
				}
				else {
					request.setAttribute("error", "Please enter valid username and password");
					doGet(request, response);
				}
		}
		
	}
	

}
