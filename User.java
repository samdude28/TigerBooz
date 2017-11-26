import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.Cookie;

/**
 * @author Nathanael Bishop (of this particular Java servlet)
 * TigerBooz  CSC 4330 Project
 * These Java servlets represent the dynamic portion of the TigerBooz website, a website built to let people
 *   read and share ratings, reviews and prices of liquors. 
 */

public class User {
	private final static String DB_TABLE    = "user";
	private final static String DB_NAME     = "tigerbooz";
	private final static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	
	
	/**
	 * Method handles keeping users logged in based on a cookie. Looks through all the array of
	 *    cookies to find one with the TigerBoozID name, indicating that cookie stores the users ID #
	 * @param cookies an array of Cookies
	 * @return the users unique ID number if found, 0 otherwise
	 */
    public static int getUserIDByCookie(Cookie[] cookies) {
		//initialize to 0 in case no cookie is found
		int userID = 0;
		
		//look through the array of cookies for one named TigerBoozID
		if(cookies != null)
			for(Cookie cookie : cookies) 
				if(cookie.getName().equals("TigerBoozID"))
					userID = Integer.parseInt(cookie.getValue());
		
		//return the user's unique ID number
		return userID;
    }

    /**
     * Take a users name and email and return their user ID number (by looking through the database)
     * @param name the users name
     * @param email the users email 
     * @return the users ID number or 0 if none found
     */
    public static int getUserIDByName(String name, String email) {
    	//initialize to 0 in case no matches are found
    	int userID = 0;

    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query to find the users ID using their name and email
			String sql   = "SELECT id FROM "+DB_TABLE+" WHERE name='"+name+"' and email='"+email+"';";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, set the userID for that 
			if(rs.next())
				userID = rs.getInt("id");
			
			//close all the connections to the database
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//finally return the users unique ID number (or 0 if not found)
		return userID;
    }

    /**
     * Take a users ID number and return their name (by looking through the database)
     * @param userID the unique number that identifies a user in the DB
     * @return the users name from the DB
     */
    public static String getUserNameByID(int userID) {
    	//initialize to a blank String in case no ID is found
    	String userName = "";
    	
    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query for all of this users reviews
			String sql   = "SELECT name FROM "+DB_TABLE+" WHERE id="+userID+";";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, set the name for that record 
			if(rs.next())
				userName = rs.getString("name");
			
			//close all the connections to the database
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		//finally return the users name (or "" if not found)
		return userName;
    }
}