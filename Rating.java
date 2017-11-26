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

public class Rating extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String DB_TABLE    = "rating";
	private static String DB_NAME     = "tigerbooz";
	private static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";
	private static Connection conn    = null; 
	private static Statement  stmt    = null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//get the users desired star rating and which liquor
		float starRating = Float.parseFloat(request.getParameter("starRating"));
		int   liquorID   = Integer.parseInt(request.getParameter("liquorID"));
		
		if(hasUserRatedLiquor(userID, liquorID))
			deleteOldRating(userID, liquorID);
		
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for all users with a matching name
			Statement stmt = (Statement) conn.createStatement();
			String sql     = "INSERT INTO "+DB_TABLE+" VALUES ("+userID+", "+liquorID+", "+starRating+")";

			stmt.executeUpdate(sql);
			conn.commit();
			
			//close all the connections to the db
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//create a cookie for the liquorID 
		Cookie liquorIDCookie;
		liquorIDCookie = new Cookie("TigerBoozLiquorID", Integer.toString(liquorID));
		
		//cookies only need to live long enough for Review to read what liquor the user was looking at
		liquorIDCookie.setMaxAge(5); 

 		//add the cookie to the response returned to the client
 		response.addCookie(liquorIDCookie);

		//set the file type, print writer, and declare the document html type
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out=response.getWriter();
		String docType="<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
		
		//create the first bit of html to be displayed
		out.println(docType + "<html><head><title>User Login</title></head><body>\n");
		out.println("<h2> Thanks for the Rating, you're being redirected shortly</h2>\n");

		response.setHeader("Refresh", "3; URL=/4330/ShowIndividualLiquor");
		
		out.println("</html>");
	}
	
	/**
	 * deletes an old review so a new one can be written in it's place
	 * @param liquorID the unique ID number of a liquor
	 * @param userID the unique ID number of a user
	 */
	private static void deleteOldRating(int userID, int liquorID) {
		//start the delete sql statement
		String sql = "delete from "+DB_TABLE+" where user_id="+userID+" and liquor_id="+liquorID+";";
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
			stmt = (Statement)conn.createStatement();
			
			//now execute the delete command
			stmt.executeUpdate(sql);
			conn.commit();
			
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean hasUserRatedLiquor(int userID, int liquorID) {
		boolean hasUserRatedLiquor = false;
		
		//look for any reviews from this user for this liquor
		String sql = "SELECT * FROM "+DB_TABLE+" WHERE user_id="+userID+" AND liquor_id="+liquorID+";";

		try {
			//start the DB connection and the query statement
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
			stmt = (Statement) conn.createStatement();
			
			//now execute the query into a resultset
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
	
			//if the resultset isnt empty, this user has left a rating for this liquor
			if(rs.next())
				hasUserRatedLiquor = true;
			
			if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hasUserRatedLiquor;
	}

	/**
	 * boilerplate servlet code
	 */
    public Rating() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
