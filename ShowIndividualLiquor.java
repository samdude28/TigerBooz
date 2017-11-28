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

public class ShowIndividualLiquor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String DB_TABLE = "liquor";
	private final static String DB_NAME  = "tigerbooz";
	private final static String DB_URL   = "jdbc:mysql://localhost:3306/"+DB_NAME;   
	private static Connection conn;
	private static Statement stmt;
	private static PrintWriter out;

	/**
	 * This servlet takes HTML form post data that indicates which liquor a user wants to see more information on.
	 *   It then displays an image of that liquor, the liquors name, the average ratings this liquor has received from
	 *   other users on TigerBooz, an average price, a map to the cheapest places to find this liquor, a randomly chosen
	 *   single review of this liquor (with the option to see more reviews), and finally present the user with links to
	 *   this liquor's manufacturers social media pages.
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

		/**
		 * This method will be called from either a form post or a redirect so input can come
		 *   from form data or a cookie.
		 * Get the liquor ID and name from cookie, values will be 0 and "" if no cookie found
		 */
		int liquorID = Liquor.getLiquorIDFromCookie(request.getCookies());
		String liquorName = Liquor.getLiquorNameByID(liquorID); 

		//if the liquor ID is 0, no cookie was found, so pull liquorID and name from form data
		if(liquorID==0) 
			try {
				//convert the liquorID string into an int and retrieve the liquor's name
				liquorID   = Integer.parseInt(request.getParameter("liquorID"));
				liquorName = request.getParameter("liquorName");
			//occasionally a NFE error was occuring so this try/catch block was added
			} catch (NumberFormatException nfe) {
				System.out.println(nfe.getMessage());
			}

		//setup the iframe for the Google Map integration
		String iframe="https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d220148.3775087238!2d-91.25150430965397!3d30.441249586068306!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1scheap+liquor+"+Liquor.getLiquorCategoryByID(liquorID)+"+baton+rouge+grocery!5e0!3m2!1sen!2sus!4v1511845154252";
		try {
			//Open a connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL,"root","ilovepizza");

			//create the sql statement to find all matches with the same liquorID as the form or cookie input						
			stmt         = (Statement) conn.createStatement();
			String sql   = "SELECT * FROM "+DB_TABLE+" WHERE id="+liquorID+";";
			ResultSet rs = (ResultSet) stmt.executeQuery(sql);
			
			//setup the liquors picture
			out.println("<div id='rightpanewrap'><div id='rightpane'>\n"); //600
			out.println("<br>&nbsp;&nbsp;&nbsp;<table id='noBorder' width=100%><tr><td style='text-align:center;'>"
					  + "<img src='http://52.26.169.0/pictures/"+liquorName+".jpg' width='168' height='420'><br><br>&nbsp;</td>");

			//add liquor name
			out.println("<td style='text-align:center;'><table width=100%><tr><td><div style='text-align:center'><h1>"+liquorName+"</h1><br></td></tr>");

			//add liquor rating picture and allow user to leave their own rating
			out.println("<tr><td style='margin:auto;'><table width=90%><tr><td>&nbsp;</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td width=100%>"+Liquor.getLiquorRatingImage(liquorID)
					+ "</td><td width=100%>"+SocialMedia.getSocialMediaButtons(liquorID)+"</td></tr></table><br><br></td></tr>");

			//add the liquor average price 
			out.println("<tr><td style='text-align:center;'>Average Price - $"+Liquor.getLiquorPrice(liquorID)
				      + "<form action='/4330/Price' method='post'><input type='text' name='price'>"
				      + "<input type='hidden' name='liquorID' value='"+liquorID+"'>"
				      + "<button type='submit'>Leave Price</button></form><br><br>&nbsp;</td></tr></table>");

			//google maps showing cheapest place to get this particular liquor
			out.println("<iframe src='"+iframe+"' style='border:0' width='500' height='400' frameborder='0' allowfullscreen>"
                      + "</iframe></div></td></table>\n");
			
			//print one randomly chosen user review of this liquor
	        out.println("<div id='socialmediawrap'><div id='comments'><p>"+Review.printOneReview(liquorID, userID)+"</p>"); //600
	        
			//print the social media links
	       // out.println("<p>"+SocialMedia.getSocialMediaButtons(liquorID)+"</p></div></div>");
	
			//close all the database connections
	 		if(rs != null)
	 			rs.close();
	 		if(stmt != null) 
					stmt.close();
	 		if(conn != null)
	 			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

    /**
	 * boilerplate servlet code
	 */
    public ShowIndividualLiquor() {
        super();
    }

	/**
	 * boilerplate servlet code
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}