package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Database.DerbyDatabase;
import Database.IDatabase;

/**
 * Servlet implementation class manageServlet
 */
@WebServlet("/Manage")
public class manageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public manageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String newUsername = null;
		String newPassword = null;
		String username = null;
		IDatabase db = new DerbyDatabase();
		
		if(request.getParameter("username") != null) {
			username = request.getParameter("username");
			request.setAttribute("username", username);
		}
		
		if(request.getParameter("newUsername") != null) {
			newUsername = request.getParameter("newUsername");
			db.changeUsername(username, newUsername);
		}
		else if(request.getParameter("newPassword") != null) {
			newPassword = request.getParameter("newPassword");
			db.changePassword(username, newPassword);
		}
		
		
		RequestDispatcher rd = request.getRequestDispatcher("view/manage.jsp");
		rd.forward(request, response);
//		request.setAttribute("username", request.getAttribute("username"));
//		request.setAttribute("user", request.getAttribute("user"));
//		
//		RequestDispatcher rd = request.getRequestDispatcher("view/manage.jsp");
//		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
