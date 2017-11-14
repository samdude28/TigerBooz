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
 * @author Nathanael Bishop 
 * Simple login prototype intended for Apache & Tomcat integration 
 * 
 * SQL:
 *			CREATE DATABASE 4330;
 *			USE 4330;
 *			CREATE TABLE logins(name VARCHAR(30), password VARCHAR(30), email VARCHAR(30));		
 */

public class Signup extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Using information from a html form post, attempt to add that user to the
	 *   mysql database
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type, print writer, and declare the document html type
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
		String DB_TABLE    = "user";
		String DB_NAME     = "tigerbooz";
		String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;

		//try to write the data and close the connection
		try {
			//open a connection
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn=(Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");

//**** TO DO: add check for existing username first  ****
//**** TO DO  check for correct date of birth			
			//create the statement to write the data to the database
			Statement stmt=(Statement) conn.createStatement();
			String sql = "INSERT INTO "+DB_TABLE+"(name, dob, email, password)" 
					   + "VALUES ('"+nameInput+"','"+dobInput+"','"+emailInput+"','"+passInput+"';";

			//send that statement to the db and commit
			stmt.executeUpdate(sql);
			conn.commit();
			
		 	//print welcome message
			out.println("<h1><br>Welcome "+nameInput+"</h1><ul>"+
				        "<b>You're registered with email</b>: "+emailInput+"\n");

			int userID = User.getUserIDByName(nameInput, emailInput);
if(userID==0)
	System.out.println("user ID 0");
			
			//close all the connections to the db
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println("closed connections in signup");	 		
	 		
			//create the login token cookie
			Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(userID));
			loginCookie.setMaxAge(60 * 60);
		
			//add the cookie to the response returned to the client
			response.addCookie(loginCookie);
			response.sendRedirect("Home");
			
		} catch (ClassNotFoundException | SQLException e) {
			out.println("<h1>Database Error</h1>");
		}
		//close out the html tags
		out.println("</body></html>");
	}

	public Signup() {        
		super();    
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}