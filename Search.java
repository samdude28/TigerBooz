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

public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DB_TABLE = "liquor";
	private static final String DB_NAME  = "tigerbooz";
	private static final String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME;
	private PrintWriter out;
	private Connection conn = null;
	private Statement  stmt = null;
	
	/**
	 * This servlet displays the result of a search request has input into an HTML form, the data is read from a form post
	 *   and the liquor table is searched using a semi-flexible search method
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//setup the PrintWriter response to be browser HTML compatible
		response.setContentType("text/html;charset=UTF-8");
		out = response.getWriter();

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);
		
		//get the users desired search from the form post
		String searchInput  = request.getParameter("searchFieldInput");
		
		//start the table to show all the liquors with names similar to searchFieldInput 
		out.println("<div id='rightpanewrap'><div id='rightpane'><br>"
				  + "<table id='keywords' cellspacing=0 cellpadding=0>"
				  + "<thead><tr>\n");
		out.println("<th><span>Liquor Name</span></th>\n"
				  + "<th><span>Average Price</span></th>\n"
				  + "<th><span>Average Rating</span></th></tr></thead><tr>\n" );

		//setup a temp variable for liquor ID number and a results counter
		int liquorID;
		int count = 0;
		
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for the users search terms
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE name like'%"+searchInput+"%';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//look through result set for liquor id and use that to get the liquor name, price and rating
			while(rs.next()) {
				liquorID = rs.getInt("id");
				//for readability each table cell get it's own Out statement
				out.println("<tr><td><font color='#c507e6'>"+Liquor.getLiquorNameByID(liquorID)+"</font><br>"
                          + "<form action='http://52.26.169.0:8080/4330/ShowIndividualLiquor' method='post'>"
						  + "<input type='hidden' name='liquorID' value='"+liquorID+"'>"
						  + "<input type='hidden' name='liquorName' value='"+rs.getString("name")+"'>\n"
			              + "<button type='submit'>"+rs.getString("name")+"</button></form></td>\n");
				out.println("<td>"+Liquor.getLiquorPrice(liquorID)+"</td>\n");
			    out.println("<td>"+Liquor.getLiquorRatingImage(liquorID)+"</td></tr>\n");
			    //keep track of how many results were found
			    count++;
			}
			
			//if no results were found then inform the user
			if(count==0)
				out.println("<tr><td colspan='2'>No results found</td></tr>\n");
			
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

		//finally close out the html tags
		out.println("</body></html>");
	}
	
    /**
	 * boilerplate servlet code
	 */
    public Search() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}