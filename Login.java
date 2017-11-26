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
 * @author Nathanael Bishop 
 * TigerBooz login servlet 
 */

public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

    /**
	 * Using information from a html form post, attempt to load a user's info
	 *   from the mysql server. If found display it, otherwise inform user.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Define the database parameters for this servlet
		String DB_TABLE    = "user";
		String DB_NAME     = "tigerbooz";
		String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;

		//get the name & password from the HTML form
		String nameInput  = request.getParameter("name");
		String passInput  = request.getParameter("password");
		
		//temporary variables used to comb through the db
		String name, password;
		int userID;
		
		//try to connect to db and search for the user
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for all users with a matching name
			Statement stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE name='"+nameInput+"';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//look through result set for the user's name and password
			while(rs.next()){
				//Retrieve by column name (from the SQL server)
				name     = rs.getString("name");
				password = rs.getString("password");
				
				//if we find a match, print it
			 	if(name.equals(nameInput) && password.equals(passInput)) { 
					//get this users unique ID 
			 		userID   = rs.getInt("id");
			 		//create the login token cookie
			 		Cookie loginCookie = new Cookie ("TigerBoozID", Integer.toString(userID));
			 		loginCookie.setMaxAge(60 * 60);

			 		//add the cookie to the response returned to the client
			 		response.addCookie(loginCookie);
			 		response.sendRedirect("Home");
			 	}
			}
			//close all the connections to the db
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();

			//The user only gets this far if name and password not found
			//set the file type, print writer, and declare the document html type
			response.setContentType("text/html;charset=UTF-8");
			final PrintWriter out=response.getWriter();
			String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
			
			//create the first bit of html to be displayed
			out.println(docType + "<html><head><title>User Login</title></head><body>\n");
			out.println("<h1>Incorrect name or password!</h1><a href='index.html'>Go Back</a>");

			//finally close out the html tags
			out.println("</body></html>");
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}
	
    public Login() {
    	super();    
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}