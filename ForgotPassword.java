import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Nathanael Bishop (of this particular Java servlet)
 * TigerBooz  CSC 4330 Project
 * These Java servlets represent the dynamic portion of the TigerBooz website, a website built to let people
 *   read and share ratings, reviews and prices of liquors. 
 */

public class ForgotPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * This servlet takes an HTTP form which is the user requesting their forgotten password. If they've input their
	 *   correct date of birth and email they will be presented with their password and redirected into the home page
	 *   of the website, otherwise they're redirected to the login screen
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setup the PrintWriter response to be browser HTML compatible
		String docType        = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		final PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");

		//print out the first bit of html
		out.println(docType+"<html>\n<head><title>Forgot Password</title></head>\n"
		        		   +"<body>\n <h1 align=\"center\">");

		//Define the database parameters for this method
		String DB_TABLE = "user";
		String DB_NAME  = "tigerbooz";
		String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME;

		//get the email and date of birth the user entered into the form
		String emailInput = request.getParameter("email");
		String dobInput   = request.getParameter("dob");
		
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for all entries with that email and date of birth
			Statement stmt = (Statement) conn.createStatement();
			String sql     = "SELECT * FROM "+DB_TABLE+" WHERE email='"+emailInput+"' AND dob='"+dobInput+"';";
			ResultSet rs   = (ResultSet) stmt.executeQuery(sql);
			
			//if there was a result, print the password for the user
			if(rs.next()){
				out.println("<h2>"+rs.getString("name")+", your password is "+rs.getString("password")+"</h2>\n");
				out.println("Redirecting you to TigerBooz home\n");

		 		//create the login token cookie
		 		Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(rs.getInt("id")));
		 		loginCookie.setMaxAge(60 * 60);

		 		//add the cookie and redirect the user to the home page
			 	response.addCookie(loginCookie);
			 	response.setHeader("Refresh", "5; URL=Home");
			}
			//if there was no result, inform the user and redirect them to the login screen
			else {
				out.println("<h3>email and date of birth not found</h3>");
				out.println("Redirecting you to TigerBooz login page\n");
			 	response.setHeader("Refresh", "5; URL=http://52.26.169.0/");
			}

			//close all the connections to the db
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();

	 		//close out the HTML tags
			out.println("</body></html>");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
    /**
	 * boilerplate servlet code
	 */
    public ForgotPassword() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}