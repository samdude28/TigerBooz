import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.Cookie;

public class User {
	//Define the database parameters for this servlet
	private final static String DB_TABLE    = "user";
	private final static String DB_NAME     = "tigerbooz";
	private final static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	
	
	/**
	 * Helper method to handle keeping users logged in based on a cookie. Looks through all the array of
	 *    cookies to find one with the TigerBoozID name, indicating that cookie stores the users ID #
	 * @param cookies an array of Cookies
	 * @return the users name if found, blank otherwise
	 */
    public static int getUserIDByCookie(Cookie[] cookies) {
		int userID = 0;
		
		//look through the array of cookies for one named TigerBoozName
		if(cookies != null)
			for(Cookie cookie : cookies) 
				if(cookie.getName().equals("TigerBoozName"))
					userID = Integer.parseInt(cookie.getValue());
		
		return userID;
    }

    /**
     * Helper method to take a users name and email and return their user ID number
     * @param name the users name
     * @param email the users email (this field should be unique)
     * @return the users ID number or 0 if none found
     */
    public static int getUserIDByName(String name, String email) {
    	int userID = 0;
    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query for all of this users reviews
			String sql   = "SELECT id FROM "+DB_TABLE+" WHERE name='"+name+"' and email='"+email+"';";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, set the userID for that 
			if(rs.next())
				userID = rs.getInt("id");
			
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println("connection closed in User.getUserID");	 		
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return userID;
    }

    
}