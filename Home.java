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
 * @author Nathanael Bishop 
 * This is a full featured 3-tier website that contains static content (Apache), dynamic content (Tomcat), 
 *   and a database (MySQL).  These Java Servlets pull static content and database content to present
 *   the user with a ratings and review website called TigerBooz. 
 *   https://www.google.com/maps/search/cheap+liquor/@30.4300052,-91.1310276,12.5z
 */

public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DB_TABLE       = "review";
	private static final String DB_NAME        = "tigerbooz";
	private static final String DB_URL         = "jdbc:mysql://localhost:3306/"+DB_NAME;
	private PrintWriter out;
	private Connection conn = null;
	private Statement  stmt = null;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//set the file type and print writer
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();

		//get the users ID number
		int userID = User.getUserIDByCookie(request.getCookies());
//todo important: if login cookie isn't valid boot the user to the login screen
System.out.println("user id"+userID);		

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);

		//print out the featured liquor
		out.println("<div id='socialmediawrap'><div id='socialmedia'><br>\n");
		out.println(Liquor.getFeaturedLiquor()+"<br><br><br>\n");
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all of this users reviews
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE user_id="+userID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//start the table for the users own reviews 
			out.println("<h2>Your Past Reviews</h2>"
                      + "<table id='keywords' cellspacing=0 cellpadding=0>"
					  + "<thead><tr>\n");
			out.println("<th><span>Liquor Name</span></th>\n"
					  + "<th><span>Your Review</span></th>\n</tr></thead><tr>\n");
			
			//loop through the result set and print in the table
			while(rs.next()) {
				//get the liquor name passing the liquor_id from SQL to Liquor.java 
				out.println("<tr><td>"+Liquor.getLiquorNameByID(rs.getInt("liquor_id"))+"</td>\n");
				out.println("<td>"+rs.getString("review")+"</td>\n</tr>\n");
			}
			
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