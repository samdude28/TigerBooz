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

public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DB_TABLE = "review";
	private static final String DB_NAME  = "tigerbooz";
	private static final String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME;
	private PrintWriter out;
	private Connection conn = null;
	private Statement  stmt = null;
	
	/**
	 * This servlet displays the users home page which consists of a random featured liquor as well as a listing
	 *   of all the users past reviews.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setup the PrintWriter response to be browser HTML compatible
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

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
			out.println("<h2>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Your Past Reviews</h2>\n&nbsp;"
                      + "<table id='keywords' cellspacing=0 cellpadding=0>"
					  + "<thead><tr><th>&nbsp;&nbsp;</th>\n");
			out.println("<th><span>Liquor Name</span></th>\n"
					  + "<th><span>Your Review</span></th>\n</tr></thead>\n");
			
			//loop through the result set and print in the table
			while(rs.next()) {
				//get the liquor name passing the liquor_id from SQL to Liquor.java 
				out.println("<tr><td></td><td>"+Liquor.getLiquorNameByID(rs.getInt("liquor_id"))+"</td>\n");
				out.println("<td>"+rs.getString("review")+"</td>\n</tr>\n");
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

		//finally close out all the html tags
		out.println("</table><br></div></div>\n<br></div></body></html>");
	}
		
    /**
	 * boilerplate servlet code
	 */
    public Home() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}