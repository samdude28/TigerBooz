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

public class Review extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String DB_TABLE             = "review";
	private static String DB_NAME              = "tigerbooz";
	private static String DB_URL               = "jdbc:mysql://localhost:3306/"+DB_NAME+"?autoReconnect=true&relaxAutoCommit=true";	
	private static Connection conn = null;
	private static Statement  stmt = null;
	private PrintWriter out;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the database table that this method will use
		String DB_TABLE = "review";

		//set the file type and print writer for user output
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();
		
		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);
		
		/**
		 * If WriteReview.java calls, data will be via cookie
		 * If Liquor.java calls, data will be via a form post
		 */
		int liquorID = getLiquorIDFromCookie(request.getCookies());
		
		//if the serviceName isnt blank, a cookie was found so find out service type
		if(liquorID==0) 
			liquorID = Integer.parseInt(request.getParameter("liquorID"));

		//start the table
		out.println("<div id='rightpanewrap'><div id='rightpane'><br><div id='wrapper'>"+ 
					"<table id='keywords' cellspacing=0 cellpadding=0>"+
					"<thead><tr>\n"+
				    "<th><span>"+Liquor.getLiquorNameByID(liquorID)+" Reviews</span></th>\n"+
		            "<th><span>User</span></th>"
				  + "<td><span>Rating</span></tr></thead><tr>\n");
		
		//connect to the db and read all table rows into array of objects
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
				out.println("<tr><td>"+rs.getString("review")+"</td>\n");
				out.println("<td>"+User.getUserNameByID(rs.getInt("user_id"))+"</td>\n");
				out.println("<td>"+Liquor.getLiquorRatingImage(liquorID, rs.getInt("user_id"))+"</td></tr>");
			}
			
			//close out the table tags
			out.println("</tr></table>");
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		createOrEditReview(userID, liquorID);
	}

	private Boolean createOrEditReview(int userID, int liquorID) {
/**		try {
			//Open a connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//create the statement that will be used to query the DB
			stmt = (Statement) conn.createStatement();
			
			//look for any reviews from this user for this service name
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE login_name='"+name+"'"
					   + "AND storage_name='"+serviceName+"'"
					   + "OR iaas_name='"+serviceName+"';";

			//now execute the query into a resultset
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//start the user review form inside a table
			out.println("<br><br><br><br><br>\n");
			out.println("<table><tr><td>");
			out.println("<form action='52.26.169.0:8080/4330/WriteReview' method='post' accept-charset='UTF-8'>\n");
			out.println("<label>Your review of this service:</label><br>"
			          + "<input type='hidden' name='name' value="+name+"> \n"
					  + "<input type='hidden' name='serviceName' value='"+serviceName+"'> \n");

			//include in form which table we want to add this review to
			if(isStorage) 
				out.println("<input type='hidden' name='serviceType' value='storage'>\n");
			else
				out.println("<input type='hidden' name='serviceType' value='iaas'>\n");
			
			
			//if user has left a review put it in the form for editing
			out.println("<textarea name='reviewText' class='bigtextbox' maxlength='250'>");
			if(rs.next()) { 
				out.println(rs.getString("text")+"</textarea>\n");
		        out.println("<input type='hidden' name='hasExistingRecord' value='true'>");
			}
			//otherwise present an empty form
			else { 
				out.println("</textarea>\n");
				out.println("<input type='hidden' name='hasExistingRecord' value='false'>");
			}
			
			//now place the submit button and close the table tags
			out.println("<button type='submit'>Publish Review</button></form>"
					  + "</td></tr></table>");
				
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
 **/
		return false;
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
			//Open a connection
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//create the statement that will be used to query the DB and randomly pick 1 row
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+" ORDER BY RAND() LIMIT 1;";

			//now execute the query into a resultset
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			oneLiquorReview+="<table id='keywords'><tr><td><form action='http://52.26.169.0:8080/4330/Review' method='post' accept-charset='UTF-8'>\n"
			          + "<input type='hidden' name='liquorID' value="+liquorID+"> \n"
					  + "<input type='hidden' name='userName' value='"+userName+"'> \n"
					  + "<button type='submit'>See more "+Liquor.getLiquorNameByID(liquorID)+" reviews</button></form></td>"
					  + "<td>User</td><td>Rating</td></tr><tr>";
			
			//grab the review string and convert the reviewer's ID into their name
			if(rs.next()) { 
				oneLiquorReview+="<td>"+rs.getString("review")+"</td>";
		        oneLiquorReview+="<td>"+User.getUserNameByID(rs.getInt("user_id"));
				oneLiquorReview+="<td>"+Liquor.getLiquorRatingImage(liquorID, rs.getInt("user_id"))+"</td></tr>";
			}
			else { 
				oneLiquorReview+="<td>No Reviews found";
			}
			
			oneLiquorReview+="</td></tr></table>";
				
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return oneLiquorReview;
	}
	
	/**
	 * Takes an array of cookies and looks for one that contains TigerBoozLiquorID, signifying that
	 *   cookie contains the liquor's unique ID number.
	 * @param cookies an array of cookies
	 * @return the liquor's unique ID
	 */
	private int getLiquorIDFromCookie(Cookie[] cookies){
		int liquorID = 0;
		
		//look through the array of cookies for one named TigerBoozLiquorID
		if(cookies != null)
			for(Cookie cookie : cookies) 
				if(cookie.getName().equals("TigerBoozLiquorID"))
					liquorID = Integer.parseInt(cookie.getValue());

		return liquorID;
	}
	
	public Review() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}