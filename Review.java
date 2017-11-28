import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nathanael Bishop (of this particular Java servlet)
 * TigerBooz  CSC 4330 Project
 * These Java servlets represent the dynamic portion of the TigerBooz website, a website built to let people
 *   read and share ratings, reviews and prices of liquors. 
 */

public class Review extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String DB_TABLE = "review";
	private static String DB_NAME  = "tigerbooz";
	private static String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";	
	private static Connection conn = null;
	private static Statement  stmt = null;
	private PrintWriter out;
	
	/**
	 * This servlet displays all the reviews for a particular liquor and allows the user to add new reviews or edit their
	 *   past reviews.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the database table that this method will use
		String DB_TABLE = "review";

		//setup the PrintWriter response to be browser HTML compatible
		response.setContentType("text/html;charset=UTF-8");
		out = response.getWriter();
		
		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);
		
		/**
		 * If WriteReview.java calls, liquor ID will be retrieved via cookie
		 * If Liquor.java calls, liquor ID will be retrieved via a form post
		 */
		int liquorID = Liquor.getLiquorIDFromCookie(request.getCookies());
		
		//if the liquor ID is 0, no cookie was found, so pull liquorID from form data
		if(liquorID==0) 
			liquorID = Integer.parseInt(request.getParameter("liquorID"));

		//start the HTML table
		out.println("<div id='rightpanewrap'><div id='rightpane'><br><div id='wrapper'>"
				  + "<table id='keywords' cellspacing=0 cellpadding=0>"
				  + "<thead><tr>\n"
				  + "<th><span>"+Liquor.getLiquorNameByID(liquorID)+" Reviews</span></th>\n"
				  + "<th><span>User</span></th>"
				  + "<td><span>Rating</span></tr></thead>\n");
		
		try {
			//Open a connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");

			//Create a query to look for all ID's that match liquorID in the review table
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE liquor_ID='"+liquorID+"';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//print the entire result set in the table
			while(rs.next()) {
				out.println("<tr><td width='500'>"+rs.getString("review")+"</td>\n");
				out.println("<td>"+User.getUserNameByID(rs.getInt("user_id"))+"</td>\n");
				out.println("<td>"+Liquor.getLiquorRatingImage(liquorID, rs.getInt("user_id"))+"</td></tr>");
			}
			
			//close out the table tags
			out.println("</tr></table>");

			//close all the database connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		//call the method that prints the users choice of adding a review or editing their previous review
		createOrEditReview(userID, liquorID);
	}

	/**
	 * Prints a small form that allows a user to write a review for this particular liquor, or edit a previous
	 *   review they left for this liquor.
	 * @param liquorID the unique ID number of a liquor
	 * @param userID the unique ID number of a user
	 */
	private void createOrEditReview(int userID, int liquorID) {
		try {
			//Open a connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//create the statement that will be used to query the DB
			stmt = (Statement) conn.createStatement();
			
			//look for any reviews from this user for this liquor
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE user_id="+userID+" AND liquor_id="+liquorID+";";

			//now execute the query into a resultset
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//start the table
			out.println("<br><br><br><br><br>\n <table><tr><td>");
			
			//start the user review form
			out.println("<form action='http://52.26.169.0:8080/4330/WriteReview' method='post' accept-charset='UTF-8'>\n");
			
			//display the users review and allow input
			out.println("<label>Your review or recipe for "+Liquor.getLiquorNameByID(liquorID)+":</label><br>"
			          + "<input type='hidden' name='userID' value="+userID+"> \n"
					  + "<input type='hidden' name='liquorID' value='"+liquorID+"'> \n");
			
			//start the form textarea where a user can type in a review
			out.println("<textarea name='reviewText' class='bigtextbox' maxlength='800'>");

			//if user has left a review put it in the form for editing
			if(rs.next()) { 
				out.println(rs.getString("review")+"</textarea>\n");
		        out.println("<input type='hidden' name='hasExistingRecord' value='true'>");
			}
			//otherwise present an empty form
			else { 
				out.println("</textarea>\n");
				out.println("<input type='hidden' name='hasExistingRecord' value='false'>");
			}
			
			//now place the submit button and close the table tags
			out.println("<br><button type='submit'>Publish Review</button></form>"
					  + "</td></tr></table>");
				
			//close all the database connections
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
	 * Given the liquor's unique ID, create a self contained table in HTML that contains one random review
	 *   that when clicked sends the user to the full list of that particular liquor's reviews
	 * @param liquorID the number that uniquely identifies a particular liquor
	 * @param userID the user who is looking at the reviews, only used to be passed as form data
	 * @return a String that contains a self contained HTML table
	 */
	public static String printOneReview(int liquorID, int userID) {
		//initialize the random review string and get the users name
		String oneLiquorReview = "";
		String userName = User.getUserNameByID(userID);

		try {
			//Open a database connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//create the statement that will be used to query the DB and randomly pick 1 row
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+" ORDER BY RAND() LIMIT 1;";

			//now execute the query into a resultset
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//start the table and the form input that sends the user to see more reviews
			oneLiquorReview+="<table id='keywords'><tr><td><form action='http://52.26.169.0:8080/4330/Review' method='post' accept-charset='UTF-8'>\n"
			          + "<input type='hidden' name='liquorID' value="+liquorID+"> \n"
					  + "<input type='hidden' name='userName' value='"+userName+"'> \n"
					  + "<button type='submit'>See more "+Liquor.getLiquorNameByID(liquorID)+" reviews and recipes</button></form></td>"
					  + "<td>User</td><td>Rating</td></tr><tr>";
			
			//if found, grab the review string and convert the reviewer's ID into their name
			if(rs.next()) { 
				oneLiquorReview+="<td>"+rs.getString("review")+"</td>";
		        oneLiquorReview+="<td>"+User.getUserNameByID(rs.getInt("user_id"));
				oneLiquorReview+="<td>"+Liquor.getLiquorRatingImage(liquorID, rs.getInt("user_id"))+"</td></tr>";
			}
			//else there were no reviews found, print that in the table
			else { 
				oneLiquorReview+="<td>No Reviews found";
			}
			
			//close up the HTML table
			oneLiquorReview+="</td></tr></table>";
				
			//close all the database connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		//finally return the String of HTML containing one random review in a table
		return oneLiquorReview;
	}
	
	/**
	 * boilerplate servlet code
	 */
    public Review() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}