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
 * Servlet implementation class ShowIndividualLiquor
 */
public class ShowIndividualLiquor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//Define the database parameters for this servlet
	private final static String DB_NAME     = "tigerbooz";
	private final static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	private static PrintWriter out;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String DB_TABLE = "liquor";

		//set the file type and print writer
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();

		//get the users ID number
		int userID = User.getUserIDByCookie(request.getCookies());
//todo important: if login cookie isn't valid boot the user to the login screen
System.out.println("user id"+userID);		

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);

		//get the liquor ID and name from the http form post
		int liquorID = Integer.parseInt(request.getParameter("liquorID"));
		String liquorName = request.getParameter("liquorName");
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			//Create and execute a query to look for all of this users reviews
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
		
			//for readability this html is shown as grouped together horizontally
			out.println("<div id='alcoholimagewrap'><div id='alcoholimage'>"
                      + "<img src='http://52.26.169.0/pictures/"+liquorName+".jpg'></div></div>"
					  + "<div id='nameofalcwrap'><div id='nameofalc'><p>"+liquorName+"</p></div></div>");
	        out.println("<div id='ratingswrap'><div id='ratings'><p>"+Liquor.getLiquorRating(liquorID)+"</p></div></div>"
	        		  + "<div id='nameofalcwrap'><div id='nameofalc'><p>Summary of product/Health facts</p></div></div>");
	        out.println("<div id='commentswrap'><div id='comments'><p>Comments</p></div></div>"
	        		  + "<div id='commentswrap'><div id='comments'><p>Recipes</p></div></div>"
	        		  + "<div id='commentswrap'><div id='comments'><p>Known sellers and pricing</p></div></div>");
	        out.println("<div id='socialmediawrap'><div id='socialmedia'><p>Social Media postings here</p></div></div>");
	        out.println("<div id='footerwrap'><div id='footer'><p>TigerBooz</p></div></div></div>");

		
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
					stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println("connection closed in ShowIndividualLiquor");	 		
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

    public ShowIndividualLiquor() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
