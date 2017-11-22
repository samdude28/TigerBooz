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
 * Servlet implementation class Search
 */
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DB_TABLE       = "liquor";
	private static final String DB_NAME        = "tigerbooz";
	private static final String DB_URL         = "jdbc:mysql://localhost:3306/"+DB_NAME;
	private PrintWriter out;
	private Connection conn = null;
	private Statement  stmt = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type and print writer
		response.setContentType("text/html;charset=UTF-8");
		out=response.getWriter();

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//setup a temp variable for liquor ID number and a counter
		int liquorID;
		int count=0;

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);
		
		String searchInput  = request.getParameter("searchFieldInput");
		
		//start the table to show all the liquors with names similar to searchFieldInput 
		out.println("<div id='rightpanewrap'><div id='rightpane'><br>"
				  + "<table id='keywords' cellspacing=0 cellpadding=0>"
				  + "<thead><tr>\n");
		out.println("<th><span>Liquor Name</span></th>\n"
				  + "<th><span>Average Price</span></th>\n"
				  + "<th><span>Average Rating</span></th></tr></thead><tr>\n" );
		
		try {
			//Open a connection to the db
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//run a SQL query for the users search terms
			stmt = (Statement) conn.createStatement();
			String sql = "SELECT * FROM "+DB_TABLE+" WHERE name like'%"+searchInput+"%';";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//look through result set for the user's name and password
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
			    count++;
			}
			
			//if no results were found then inform the user
			if(count==0)
				out.println("<tr><td colspan='2'>No results found</td></tr>\n");
			
			//close all the connections to the db
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
				stmt.close();
	 		if(conn != null)
	 			conn.close();

			//finally close out the html tags
			out.println("</body></html>");
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}
	
    public Search() {
    	super();    
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}