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
		//setting up variables passed from the html	form	
		String dobInput   = request.getParameter("dob");
		String nameInput  = request.getParameter("name");
		String passInput  = request.getParameter("password");
		String emailInput = request.getParameter("email");

		//Define the database parameters for this servlet
		String DB_TABLE = "user";
		String DB_NAME  = "tigerbooz";
		String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";

		//default year to 1 so if invalid input it will boot user
		int dobYear = 1;
		try {
			//grab just the last 2 digits of the dob from the users input
			dobYear = Integer.parseInt(dobInput.substring(Math.max(dobInput.length()-2,0)));
		} catch (NumberFormatException nfe) {
			System.out.println(nfe);
		}
		
		//if the username is shorter than 3 characters, don't allow registration, redirect to login
		if(nameInput.length() < 3)
			printMessageAndRedirect("Please use a longer username", 0, false, response);
		//if the email is shorter than 6 characters, it can't be a valid email so don't allow registration, redirect to login
		else if(emailInput.length() < 6)
			printMessageAndRedirect("Invalid email address", 0, false, response);
		//if the password is shorter than 3 characters, don't allow registration, redirect to login
		else if(passInput.length() < 3)
			printMessageAndRedirect("Please use a longer password", 0, false, response);
		//if the person isn't 21 or older, don't allow registration, redirect to login
		else if(dobYear > 96 || dobYear < 17)
			printMessageAndRedirect("TigerBooz is for adults only", 0, false, response);
		//else create the new user and redirect them to the home screen, redirect to login
		else
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
				
				//get the users newly created unique ID number
				int userID = User.getUserIDByName(nameInput, emailInput);
				
				//close all the connections to the database
		 		if(stmt != null) 
					stmt.close();
		 		if(conn != null)
		 			conn.close();
		 		
		 		printMessageAndRedirect("Thanks for signing up "+nameInput, userID, true, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Prints any message to the user and either redirects them to the home screen (after creating the correct login cookie)
	 *    or redirects them to the login screen
	 * @param message the message to send to the user
	 * @param userID the unique ID number of the user (can be 0 if redirecting to login screen)
	 * @param redirectHome if true, create login cookie and redirect to home screen, otherwise just redirect to login screen
	 * @param response the calling methods HttpServletResponse, needed to access output to users browser
	 * @throws IOException for HttpServletResponse
	 */
	public static void printMessageAndRedirect(String message, int userID, boolean redirectHome, HttpServletResponse response) throws IOException {
		//setup the PrintWriter response to be browser HTML compatible
		response.setContentType("text/html;charset=UTF-8");
		final PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		
		//print out the first bit of html
		out.println(docType+"<html>\n<head><title>Message</title></head>\n <body>\n");

		//print message
		out.println("<h1><br>"+message+"</h1><br>You'll be redirected shortly");

		//finally close out the html tags
		out.println("</body></html>");
		
		//if calling method indicates we need to redirect home
		if(redirectHome) {
			//create the login token cookie
			Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(userID));
			loginCookie.setMaxAge(60 * 60);
		
			//add the cookie and redirect the user to the home screen
			response.addCookie(loginCookie);
			response.setHeader("Refresh", "3; URL=/4330/Home");
		}
		//otherwise the calling method indicates to redirect to login screen
		else
			response.setHeader("Refresh", "3; URL=http://52.26.169.0");
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