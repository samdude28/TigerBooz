import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nathanael Bishop 
 */

public class WriteReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String DB_TABLE             = "review";
	private static String DB_NAME              = "tigerbooz";
	private static String DB_URL               = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";	
	private static Connection conn = null;
	private static Statement  stmt = null;
	private PrintWriter out;
	
	/**
	 * Servlet accepts a users new or edited review and makes the neccessary changes to the database
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		
		//load all the variables from the form data (converting strings to ints)
		int userID                = Integer.parseInt(request.getParameter("userID"));
		int liquorID              = Integer.parseInt(request.getParameter("liquorID"));
		String reviewText         = request.getParameter("reviewText");
		String hasExisitingRecord = request.getParameter("hasExistingRecord");

		try {
			//open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn=(Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
			
			//if user already left a review for this liquor, delete old record 
			if(hasExisitingRecord.equals("true")) 
				deleteOldReview(liquorID, name);

			//taking a performance hit with prepareStatement to sanitize inputs
			String sql = "INSERT INTO "+DB_TABLE+" VALUES (?, ?, ?)";
			pstmt      = conn.prepareStatement(sql);

			//sanitize the users input
			pstmt.setInt(1, userID);
			pstmt.setInt(2, liquorID);
			pstmt.setString(3, reviewText);

			//now write the update to the db
			pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			//finally, attempt close the connection
			try {
				if(stmt!=null)
					stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//create a cookie for the liquorID 
		Cookie liquorIDCookie;
		liquorIDCookie = new Cookie("TigerBoozLiquorID", liquorID);
		
		//cookies only need to live long enough for Review to read what liquor the user was looking at
		liquorIDCookie.setMaxAge(5); 

 		//add the cookie to the response returned to the client
 		response.addCookie(liquorIDCookie);

 		//return the user to the Review page for the product they just reviewed
 		response.sendRedirect("Review");
	}

	/**
	 * deletes an old review so a new one can be written in it's place
	 * @param liquorID the unique ID number of a liquor
	 * @param userID the unique ID number of a user
	 */
	private static void deleteOldReview(int liquorID, int userID) {
		//start the delete sql statement
		String sql = "delete from "+DB_TABLE+" where user_id="+userID+" and ";
				   + "liquor_id="+liquorID+";";
		
		//try to execute the statement
		try {
			stmt = (Statement) conn.createStatement();
			stmt.executeUpdate(sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * boilerplate servlet code
	 */
    public WriteReview() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
