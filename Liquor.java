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

public class Liquor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String DB_NAME     = "tigerbooz";
	private final static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	private static PrintWriter out;
	
	/**
	 * This servlet displays all the liquors in a particular category. That category is set by whatever calling
	 *   method or HTML form post.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set response type to be UTF-8 standard and start the PrintWriter
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();
		
		//set the database table that this method will use
		String DB_TABLE = "liquor";

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);

		//get the type of liquor from the http form post
		String liquorCategory = request.getParameter("liquorCategory");
				
		//start the table to show all the liquors of this type 
		out.println("<div id='rightpanewrap'><div id='rightpane'><div id='wrapper'><br>"
				  + "<table id='keywords' cellspacing='0' cellpadding='0'>"
				  + "<thead><tr>\n");
		out.println("<th><span>&nbsp;</span></th>\n"
				  + "<th><span>Liquor Name</span></th>\n"
				  + "<th><span>Average Price</span></th>\n"
				  + "<th><span>Average Rating</span></th></tr></thead>\n" );
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all liquors in this category
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE category='"+liquorCategory+"';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//initialize a temp variable so we dont have to keep calling rs.getInt("id") (and readability)
			int liquorID = 0;

			//go through the ResultSet and print out all the values in the html table
			while(rs.next()) {
				liquorID = rs.getInt("id");
				//for readability each table cell get it's own out statement
				out.println("<tr><td><form action='http://52.26.169.0:8080/4330/ShowIndividualLiquor' method='post'>\n"
						  + "<input type='hidden' name='liquorID' value='"+liquorID+"'>\n"
						  + "<input type='hidden' name='liquorName' value='"+rs.getString("name")+"'>\n"
			              + "<input type=image src='http://52.26.169.0/pictures/play.png'></form></td>");
				out.println("<td>"+getLiquorNameByID(liquorID)+"</td>");
				out.println("<td>"+getLiquorPrice(liquorID)+"</td>");
			    out.println("<td>"+Liquor.getLiquorRatingImage(liquorID)+"</td></tr>\n");
			}
			
			//close out the html table tag
			out.println("</tr></table></div>\n");
			
			//close all the connections to the db
	 		if(rs   != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}		

		//finally close out the html tags
		out.println("<br></div></body></html>");
	}
	
	/**
	 * Given a liquor ID number, return the average price of that liquor
	 * @param liquorID the unique ID number of a particular liquor
	 * @return the price of the particular liquor, as a float
	 */
	public static float getLiquorPrice(int liquorID) {
		//the database table this method will be using
		String DB_TABLE = "price";

		//initialize the variables that will be used to calculate the average price
		float totalPrice = 0;
		int count        = 0;

		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all liquors with the same id as liquorID
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//add up all the prices in the query so we can get an average price
			while(rs.next()) {
				count++;
				totalPrice += rs.getFloat("price");
			}

			//close all the connections to the db
	 		if(rs   != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}		
		
		//if no prices were found we'll return 0
		if(count==0)
			return 0;
		
		//else return the average price rounded to 2 digits
		return (float) ((float)Math.round(totalPrice / count * 100.0) / 100.0);		
	}
	
	/**
	 * Given a liquor ID number, return the average rating of that liquor
	 * @param liquorID the unique ID number of a particular liquor
	 * @return the rating of the particular liquor, as a float
	 */
	public static float getLiquorRating(int liquorID) {
		//the database table this method will be using
		String DB_TABLE = "rating";

		//initialize the variables that will be used to calculate the average rating
		float totalRating = 0;
		int count         = 0;
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all liquors with the same id as liquorID
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);

			//add up all the ratings in the query so we can get an average rating
			if(rs != null)
				while(rs.next()) {
					count++;
					totalRating += rs.getFloat("rating");
				}

			//close all the connections to the db
	 		if(rs   != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}		
		
		//if no prices were found we'll return 0
		if(count==0)
			return 0;
		
		//else return the average rating rounded to 1 digit
		return (float) ((float)Math.round(totalRating / count * 10.0) / 10.0);
	}

	/**
	 * Overloaded method getLiquorRating - Given a liquor ID number, return the average rating of that liquor 
	 *   for a particular user
	 * @param liquorID the unique ID number of a particular liquor
	 * @param userID the uniqur ID number of a user
	 * @return the users rating of the particular liquor, as a float
	 */
	public static float getLiquorRating(int liquorID, int userID) {
		//the database table this method will be using
		String DB_TABLE = "rating";

		//initialize the users rating to 0 in case they haven't rated the product yet
		float thisUserRating = 0;
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all liquors with the same id as liquorID
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+" and user_id="+userID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there are any results, it's the users star rating
			if(rs.next())
				thisUserRating = rs.getFloat("rating");

			//close all the connections to the db
	 		if(rs   != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}		
		
		//return the users rating
		return thisUserRating;
	}
	
	/**
	 * Given a liquor ID number, return a String that consists of a table of images indicating graphically 
	 *   how this liquor has been rated by other users. Additionally the user can click on the images to
	 *   indicate how they would like to rate the liquor. The leftmost image indicates the user wants to rate
	 *   at a 1, while the rightmost indicates a desired rating of 5
	 * @param liquorID the unique ID number of a particular liquor
	 * @return a String consisting of a fully formated HTML table with clickable images of a rated liquor
	 */
	public static String getLiquorRatingImage(int liquorID) {
		//start the HTML String and the fraction of a whole number image we'll use later
		String liquorImage        = "<table class='keywords'><tr>";
		String starRatingFraction = "star25.jpg";

		//this float is counted down until it reaches 0, for each count an image is displayed
		float liquorRating = getLiquorRating(liquorID);

		//this float keeps track of how many images have been printed, part of the user rating form
		float imageCounter = 1.0f;
		
		//countdown until liquorRating is less than a whole number
		while (liquorRating >= 1.0) {
						//add the form for the user to leave their own rating
			liquorImage += "<td><form action='/4330/Rating' method='post'><input type='hidden' name='starRating' value='"+imageCounter+"'>"
						//indicate to the next servlet which liquor ID we're rating
						+  "<input type='hidden' value='"+liquorID+"' name='liquorID'>"
						//for each rating over 1.0 add a full star image the string
					    +  "<input type='image' src='http://52.26.169.0/pictures/star.jpg' alt='"+imageCounter+" star' width='40' height='50'></form></td>";
			liquorRating--;
			imageCounter++;
		}
		
		//now pick the correct fraction image with 50% or 75% of a "star"
		if (liquorRating > 0.74) 
			starRatingFraction = "star75.jpg";
		else if (liquorRating > 0.49) 
			starRatingFraction = "star50.jpg";

		//if there's a fraction above ~1/4, display the appropriate image and form
		if (liquorRating > 0.24) {
			liquorImage += "<td><form action='/4330/Rating' method='post'><input type='hidden' name='starRating' value='"+imageCounter+"'>"
						//indicate to the next servlet which liquor ID we're rating
						+  "<input type='hidden' value='"+liquorID+"' name='liquorID'>"
						//display the appropriate fraction image
				        +  "<input type='image' src='http://52.26.169.0/pictures/"+starRatingFraction+"' alt='"+imageCounter+" star' width='20' height='25'></form></td>";
			imageCounter++;
		}

		//print all empty stars until we've reached the maximum rating possible, 5 stars.
		while(imageCounter <= 5) {
			liquorImage += "<td><form action='/4330/Rating' method='post'><input type='hidden' name='starRating' value='"+imageCounter+"'>"
						//indicate to the next servlet which liquor ID we're rating
						+  "<input type='hidden' value='"+liquorID+"' name='liquorID'>"
						//display empty star
				        +  "<input type='image' src='http://52.26.169.0/pictures/star0.jpg' alt='"+imageCounter+" star' width='20' height='25'></form></td>";
			imageCounter++;
		}

		//finally return the entire String along with the HTML tags necessary to end the table 
		return liquorImage+"</tr></table>";
	}
	
	/**
	 * Build a string that contains HTML to print out a particular liquor's rating that one particular user has
	 *   left.
	 * @param liquorID the unique ID number of a particular liquor
	 * @param userID the unique ID number of a particular user
	 * @return a String consisting of HTML that displays 5 images in a row
	 */
	public static String getLiquorRatingImage(int liquorID, int userID) {
		//initialize the HTML String to be returned, and retrieve the users rating for this liquor
		String liquorImage = "";
		float liquorRating = getLiquorRating(liquorID, userID);
		
		//for each rating over 1.0 add a full star to the string
		while (liquorRating > 1.0) {
			liquorImage += "<img src='http://52.26.169.0/pictures/star.jpg' width='20' height='25'>";
			liquorRating--;
		}
		
		//now pick the correct image with 25% 50% or 75% of a "star"
		if (liquorRating > 0.74)
			liquorImage += "<img src='http://52.26.169.0/pictures/star75.jpg' width='20' height='25'>";
		else if (liquorRating > 0.49)
			liquorImage += "<img src='http://52.26.169.0/pictures/star50.jpg' width='20' height='25'>";
		else if (liquorRating > 0.24)
			liquorImage += "<img src='http://52.26.169.0/pictures/star25.jpg' width='20' height='25'>";
		 
		//if no ratings were found, return the no ratings image
		if(liquorImage.equals(""))
				return "<img src='http://52.26.169.0/pictures/norating.jpg' width='20' height='25'>";
		//else return the HTML images String
		return liquorImage;
	}
	
	/**
	 * Retrieve a liquor's name from the database given the liquor's unique ID number
	 * @param liquorID the unique ID number of a particular liquor
	 * @return the liquors name as a String
	 */
    public static String getLiquorNameByID(int liquorID) {
		//initialize the liquorName and DB_TABLE variables
		String liquorName  = "";
		String DB_TABLE    = "liquor";
		
    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query to grab the only name attached to that ID
			String sql   = "SELECT name FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, get the name for that record 
			if(rs.next())
				liquorName = rs.getString("name");
			
			//close all the connections
	 		if(rs   != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//finally return the liquorName
		return liquorName;
    }
    
    /**
     * Send this method a liquor's unique ID and it will return the liquor's category
     * @param liquorID the liquor to be searched for
     * @return the name of the liquors category as a String
     */
    public static String getLiquorCategoryByID(int liquorID) {
		//initialize the liquorName and DB_TABLE variables
    	String liquorCategory = "";
		String DB_TABLE       = "liquor";
		
    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query to grab the liquor's category
			String sql   = "SELECT category FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, set the name for that record 
			if(rs.next())
				liquorCategory = rs.getString("category");
			
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//finally return the liquor's category
		return liquorCategory;
    }
	
    /**
     * This method will pull a random Liquor from the database and output a string
     *   that contains all the HTML needed to print a self contained table 
     * @return the formatted string containing an HTML table of 1 random liquor
     */
    public static String getFeaturedLiquor() {
    	//initialize the String to be returned and the table we'll be accessing
    	String featuredLiquor="";
		String DB_TABLE    = "liquor";

		//start the HTML table  
		featuredLiquor += "<table id='keywords' cellspacing=0 cellpadding=0>";
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for one random liquor
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" ORDER BY RAND() LIMIT 1;";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//temp variable so we don't have to call rs as much, and for readability
			int liquorID = 0;

			//go through the one value in the ResultSet and print out all the values in the html table
			if(rs.next()) {
				liquorID = rs.getInt("id");
				String liquorName=rs.getString("name");
				featuredLiquor += "<tr><td><font color='#c507e6'>"+getLiquorNameByID(liquorID)+"</font><br>"
                               +  "<form action='http://52.26.169.0:8080/4330/ShowIndividualLiquor' method='post'>"
						       +  "<input type='hidden' name='liquorID' value='"+liquorID+"'>"
						       +  "<input type='hidden' name='liquorName' value='"+liquorName+"'>\n"
			                   +  "<input type='image' src='http://52.26.169.0/pictures/"+liquorName+".jpg' width='168' height='420' alt='"+liquorName+"'>"
			                   +  "</form></td>\n";
				featuredLiquor += "<td>"+rs.getString("name")+"<br>\n$"+getLiquorPrice(liquorID)+"</td>\n";
			    featuredLiquor += "<td>"+Liquor.getLiquorRatingImage(liquorID)+"<br>\n"
			                   +  "Number of Reviews: "+getNumReviews(liquorID)+"</td></tr></table>\n";
			}
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
		}
		//finally returned the HTML table containing the featured liquor
    	return featuredLiquor;
    }
    
    /**
     * counts the number of times a particular liquor has been reviewed
     * @param liquorID the liquor to count number of reviews
     * @return the number of reviews that liquor has as an int
     */
    public static int getNumReviews(int liquorID) {
    	//initialize the number of reviews counter and the db table name for this method
    	int numReviews  = 0;
    	String DB_TABLE = "review";
    	
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all the reviews with that liquorID
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT COUNT(*) FROM "+DB_TABLE+" WHERE liquor_id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//grab the results of the query, that will be the number of rows for that liquorID
			if(rs.next()) 
				numReviews=rs.getInt(1);
			
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
			
		} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
		}
		//finally return the number of reviews this liquor has received
    	return numReviews;
    }
    
	/**
	 * Takes an array of cookies and looks for one that contains TigerBoozLiquorID, signifying that
	 *   cookie contains the liquor's unique ID number.
	 * @param cookies an array of cookies
	 * @return the liquor's unique ID as an int
	 */
	public static  int getLiquorIDFromCookie(Cookie[] cookies){
		//initialize the liquor ID to 0 in case none were found
		int liquorID = 0;
		
		//look through the array of cookies for one named TigerBoozLiquorID
		if(cookies != null)
			for(Cookie cookie : cookies) 
				//if found, grab the cookie value which is a String so we convert it to int
				if(cookie.getName().equals("TigerBoozLiquorID"))
					liquorID = Integer.parseInt(cookie.getValue());

		//finally return the liquor's unique ID number
		return liquorID;
	}
    	
	/**
	 * boilerplate servlet code
	 */
    public Liquor() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}