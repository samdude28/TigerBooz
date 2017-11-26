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

		//get the users ID number, if it's not found, boot them to the login screen
		int userID = User.getUserIDByCookie(request.getCookies());
		if(userID==0)
			response.sendRedirect("http://52.26.169.0");

		//display the website template, giving it the users name for personalization
		LoadTemplate.loadTemplate(User.getUserNameByID(userID), out);

		//get the liquor ID and name from the http form post
		int liquorID = Liquor.getLiquorIDFromCookie(request.getCookies());
		
		//if the liquor ID is 0, no cookie was found, so pull liquorID from form data
		if(liquorID==0) 
			try {
				liquorID = Integer.parseInt(request.getParameter("liquorID").trim());
			} catch (NumberFormatException nfe) {
				System.out.println(nfe.getMessage());
			}

		//get the liquor name and setup the iframe for the Google Map integration
		String liquorName = request.getParameter("liquorName");
		String iframe="https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d55049.33166631953!2d-91.13767542879442!3d30.41956442503378!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1s"+Liquor.getLiquorCategoryByID(liquorID)+"!5e0!3m2!1sen!2sus!4v1511378188658";
		
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");
						
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			out.println("<div id='rightpanewrap'><div id='rightpane'>\n"); //600
			out.println("<br>&nbsp;&nbsp;&nbsp;<table id='noBorder' width=100%><tr><td style='text-align:center;'>"
					  + "<img src='http://52.26.169.0/pictures/"+liquorName+".jpg' width='168' height='420'><br><br>&nbsp;</td>");
			out.println("<td><div style='text-align:center'><h2>"+liquorName+"</h2><br><br>"
                      +  Liquor.getLiquorRatingImage(liquorID)+"<br><br>"
                      + "Average Price - $"+Liquor.getLiquorPrice(liquorID)+"<br><br>"
                      + "<iframe src='"+iframe+"' style='border:0' width='500' heigh='500' frameborder='0' allowfullscreen>"
                      + "</iframe></div></td></table>\n");
			
/**	out.println("<div id='alcoholimagewrap'><div id='alcoholimage'>" 
	          + "<img src='http://52.26.169.0/pictures/"+liquorName+".jpg'></div></div>"); //360
out.println("<div id='ratingswrap'><div id='ratings'><p>"+Liquor.getLiquorRating(liquorID)+"</p></div></div>"); //100

out.println("<div id='nameofalcwrap'><div id='nameofalc'><p>"+liquorName+"</p></div></div>"); //200	        out.println("<div id='commentswrap'><div id='comments'><p>TODO: Known sellers and pricing</p></div></div>"); //220
**/
                      
	        out.println("<div id='socialmediawrap'><div id='comments'><p>"+Review.printOneReview(liquorID, userID)+"</p></div></div>"); //600
	        
	        out.println("<div id='socialmediawrap'><div id='socialmedia'>"
	        		  + "<p>"+SocialMedia.getSocialMediaButtons(liquorID)+"</p></div></div>");
	

		
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
	}

    public ShowIndividualLiquor() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}
