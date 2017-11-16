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

public class BoilerPlate extends HttpServlet {
	private static final long serialVersionUID = 1L;

/**
 *   Change DB_TABLE to be whatever table you'll be using. 
 */
	private static final String DB_TABLE       = "review";
	private static final String DB_NAME        = "tigerbooz";
	private static final String DB_URL         = "jdbc:mysql://localhost:3306/"+DB_NAME;
	private PrintWriter out;
	private Connection conn = null;
	private Statement  stmt = null;
	
/**
* Everything that gets display to a user will be in this method, as well as the receiving of information from other pages
* for example the search function will receive the text the user is searching for from the HttpServletRequest
* You can write other methods outside of this one but you'll have to manually pass form data
*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type and print writer
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();

		//get the users ID number
		int userID = User.getUserIDByCookie(request.getCookies());

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);

		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
/**
 * 	Here is where you interact with the database. The two statements that will cover 99% of everything
 *  we do will be SELECT and INSERT.  This particular statement reads every cell from DB_TABLE that matches
 *  the provided userID in the ResultSet.  The INSERT statement is used to add to the database (see Signup.java)
 */
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE user_id="+userID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
/*
 * Now if we use out.println we print into the html document. So in this case you need to know a bit of html.
 * Note the \n is just to make the html code more readable for troubleshooting
 */
			out.println("<div id='socialmediawrap'><div id='socialmedia'><br>"
					  + "<table id='keywords' cellspacing=0 cellpadding=0>"
					  + "<thead><tr>\n");
			out.println("<th><span>Liquor Name</span></th>\n"
					  + "<th><span>Your Review</span></th>\n</tr></thead><tr>\n");
			
			//loop through the result set and print in the table
			while(rs.next()) {
/** 
 * get the liquor name passing the liquor_id from SQL to Liquor.java 
 * Liquor.getLiquorNameByID is something I wrote, so all you really need to notice is the 
 * rs.getInt("liquor_id")    this looks through this ResultSet for the SQL column "liquor_id"
 * and the getInt does have to match. getFloat and getString are the only other 2 we need
 */
				out.println("<tr><td>"+Liquor.getLiquorNameByID(rs.getInt("liquor_id"))+"</td>\n");
				out.println("<td>"+rs.getString("review")+"</td>\n</tr>\n");
			}

/**
 * And that's pretty much it, the rest of this just closes the html tags that LoadTemplate started
 * and closes the database connections
 */
			
			//close out the table tags and the div wrapper
			out.println("</table><br></div></div>\n");
			
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

		//finally close out the html tags
		out.println("<br></div></body></html>");
	}
	
	
    public Home() {
        super();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}