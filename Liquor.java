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
 * Servlet implementation class Liquor
 */
public class Liquor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//Define the database parameters for this servlet
	private final static String DB_TABLE    = "liquor";
	private final static String DB_NAME     = "tigerbooz";
	private final static String DB_URL      = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//set the file type and print writer
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out=response.getWriter();

		//for readability this html is shown as grouped together horizontally
		out.println("<div id='alcoholimagewrap'><div id='alcoholimage'><p>Image of Alcohol</p></div></div>"
				   +"<div id='nameofalcwrap'><div id='nameofalc'><p>Name of Alcohol</p></div></div>");
        out.println("<div id='ratingswrap'><div id='ratings'><p>how many stars rated</p></div></div>"
        		   +"<div id='nameofalcwrap'><div id='nameofalc'><p>Summary of product/Health facts</p></div></div>");
        out.println("<div id='commentswrap'><div id='comments'><p>Comments</p></div></div>"
        		   +"<div id='commentswrap'><div id='comments'><p>Recipes</p></div></div>"
        		   +"<div id='commentswrap'><div id='comments'><p>Known sellers and pricing</p></div></div>");
        out.println("<div id='socialmediawrap'><div id='socialmedia'><p>Social Media postings here</p></div></div>");
        out.println("<div id='footerwrap'><div id='footer'><p>TigerBooz</p></div></div></div>");



		
	}
	
    public static String getLiquorNameByID(int liquorID) {
		String liquorName="";
		
    	try {
	    	//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
	
			//Create and execute a query for this
			String sql   = "SELECT name FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			stmt         = (Statement) conn.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//if there were results, set the name for that record 
			if(rs.next())
				liquorName = rs.getString("name");
			
			//close all the connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
 				stmt.close();
	 		if(conn != null)
	 			conn.close();
System.out.println("connection closed in Liquor.getLiquorNameByID");	 		
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return liquorName;
}
	
	public Liquor() {
        super();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
