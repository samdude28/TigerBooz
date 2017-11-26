import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class Signup extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * This servlet uses information from a html form post to attempt to add a user to the mysql database (create a new login)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setup the PrintWriter response to be browser HTML compatible
		response.setContentType("text/html;charset=UTF-8");
		final PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		
		//print out the first bit of html
		out.println(docType+"<html>\n<head><title>User registration</title></head>\n"
		        		   +"<body>\n <h1 align=\"center\">");
		
		//setting up variables passed from the html	form	
		String dobInput   = request.getParameter("dob");
		String nameInput  = request.getParameter("name");
		String passInput  = request.getParameter("password");
		String emailInput = request.getParameter("email");

		//Define the database parameters for this servlet
		String DB_TABLE = "user";
		String DB_NAME  = "tigerbooz";
		String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";

		try {
			//open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn=(Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");

			//create the statement to write the data to the database
			Statement stmt=(Statement) conn.createStatement();
			String sql = "INSERT INTO "+DB_TABLE+"(name, dob, email, password)" 
					   + "VALUES ('"+nameInput+"','"+dobInput+"','"+emailInput+"','"+passInput+"');";

			//send that statement to the db and commit
			stmt.executeUpdate(sql);
			conn.commit();
			
		 	//print welcome message
			out.println("<h1><br>Welcome "+nameInput+"</h1><ul>"+
				        "<b>You're registered with email</b>: "+emailInput+"\n");

			//get the users newly created unique ID number
			int userID = User.getUserIDByName(nameInput, emailInput);
			
			//close all the connections to the database
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
	 		
			//create the login token cookie
			Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(userID));
			loginCookie.setMaxAge(60 * 60);
		
			//add the cookie and redirect the user to the home screen
			response.addCookie(loginCookie);
			response.sendRedirect("Home");
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		//finally close out the html tags
		out.println("</body></html>");
	}

	/**
	 * boilerplate servlet code
	 */
    public Signup() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}