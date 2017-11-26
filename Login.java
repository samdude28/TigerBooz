import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nathanael Bishop (of this particular Java servlet)
 * TigerBooz  CSC 4330 Project
 * These Java servlets represent the dynamic portion of the TigerBooz website, a website built to let people
 *   read and share ratings, reviews and prices of liquors. 
 */

public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

    /**
	 * This servlet is called when a user tries to login. The login page sends and HTML form post containing
	 *   the username and password the user typed in. If a match is found to a registered user, send the user
	 *   to their home screen.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Define the database parameters for this servlet
		String DB_TABLE    = "user";
		String DB_NAME     = "tigerbooz";
		String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;

		//get the name & password from the HTML form
		String nameInput = request.getParameter("name");
		String passInput = request.getParameter("password");
		
		//temporary variables used to comb through the db
		String name, password;
		int userID;
		
		//try to connect to db and search for the user
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for all users with a matching name AND matching password
			Statement stmt = (Statement) conn.createStatement();
			String sql     = "SELECT * FROM "+DB_TABLE+" WHERE name='"+nameInput+"' AND password='"+passInput+"';";
			ResultSet rs   = (ResultSet) stmt.executeQuery(sql);
			
			//if the resultset isn't empty, the user has entered valid input
			if(rs.next()) {
				//get this users unique ID 
		 		userID   = rs.getInt("id");

		 		//create the login token cookie
		 		Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(userID));
		 		loginCookie.setMaxAge(60 * 60);

		 		//add the cookie and send the user to the home screen
		 		response.addCookie(loginCookie);
		 		response.sendRedirect("Home");
			 	}
			//else there was no match, so inform the user
			else {
				//setup the PrintWriter response to be browser HTML compatible
				response.setContentType("text/html;charset=UTF-8");
				final PrintWriter out=response.getWriter();
				String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
				
				//create the first bit of html to be displayed
				out.println(docType + "<html><head><title>User Login</title></head><body>\n");
				out.println("<h1>Incorrect name or password!</h1><a href='index.html'>Go Back</a>");

				//finally close out the html tags
				out.println("</body></html>");
			}
			
			//close all the connections to the db
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
    /**
	 * boilerplate servlet code
	 */
    public Login() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}